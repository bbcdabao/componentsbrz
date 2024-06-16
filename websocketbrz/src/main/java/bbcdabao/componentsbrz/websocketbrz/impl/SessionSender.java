/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package bbcdabao.componentsbrz.websocketbrz.impl;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import bbcdabao.componentsbrz.websocketbrz.api.ISessionSender;

/**
 * Separate thread sending for WebSocketMessage module
 */
public class SessionSender extends Thread implements ISessionSender {

	/**
	 * Send transfer interface
	 */
	private static interface ISend {
		void send(WebSocketMessage<?> msg);
	}

	private final Logger logger = LoggerFactory.getLogger(SessionSender.class);

	/**
	 * Then WebSocketMessage list waiting for send
	 */
	private BlockingQueue<WebSocketMessage<?>> msgList = null;

	/**
	 * The unit of sending PING cycle is milliseconds
	 */
	private WebSocketSession session = null;

	private IComplete complete = null;

	private long pingCyc = 0;

	private long pingFlg = 0;

	private AtomicLong timeSet = null;

	private WebSocketMessage<?> getMsg() {
		WebSocketMessage<?> msg = null;
		try {
			msg = msgList.poll(1000, TimeUnit.MICROSECONDS);
		} catch (InterruptedException e) {
			logger.info("session id:{} sender is Interrupted", session.getId());
		}
		return msg;
	}
	
	private void doSend(ISend send) {
		try (WebSocketSession sessionClose = session) {
			while (session.isOpen()) {
				WebSocketMessage<?> msg = getMsg();
				if (null != msg) {
					send.send(msg);
					timeSet.set(0);
				} else {
					checkAndSendPing();
				}
			}
		} catch (Exception e) {
			logger.error("session id:{} doSend Exception", session.getId(), e);
		}
	}

	private void checkAndSendPing() {
		long thisSet = timeSet.get();
		if (0 == thisSet) {
			// info: 如果刚刚发过Ping直接返回
			pingFlg = 0;
			return;
		}
		long thisNow = System.currentTimeMillis();
		if (0 == pingFlg) {
			pingFlg = thisNow;
			return;
		}
		long thisSub = thisNow - pingFlg;
		if (thisSub < pingCyc) {
			return;
		}
		pingFlg = thisNow;
		logger.info("session id:{} send ping >>>", session.getId());
		try {
			session.sendMessage(new PingMessage());
		} catch(Exception e) {
			logger.info("session id:{} checkAndSendPing Exception:", session.getId(), e);
		}
	}

	public SessionSender(int capacity, WebSocketSession session, IComplete complete, AtomicLong timeSet, long pingCyc) {
		this.msgList = new LinkedBlockingQueue<>(capacity);
		this.session = session;
		this.complete = complete;
		this.timeSet = timeSet;
		this.pingCyc = pingCyc;
		start();
	}

	@Override
	public boolean postMsg(WebSocketMessage<?> msg, long timeout, TimeUnit unit) throws InterruptedException {
		return msgList.offer(msg, timeout, unit);
	}

	@Override
	public boolean postMsg(WebSocketMessage<?> msg) {
		return msgList.offer(msg);
	}

	@Override
	public boolean isOpen() {
		return session.isOpen();
	}

	@Override
	public String getSessionId() {
		return session.getId();
	}

	@Override
	public void close() throws IOException {
		session.close();
		interrupt();
	}

	@Override
	public void run() {
		logger.info("session id:{} sender begin to working...", session.getId());
		if (complete != null) {
			doSend(new ISend() {
				@Override
				public void send(WebSocketMessage<?> msg) {
					Throwable exception = null;
					boolean ok = false;
					try {
						session.sendMessage(msg);
						ok = true;
					} catch(Exception e) {
						exception = e;
					} finally {
						try {
							complete.onComplete(msg, ok, exception);
						} catch (Exception e) {
							logger.error("session id:{} onComplete Exception", session.getId(), e);
						}
					}
				}
			});
		} else {
			doSend(new ISend() {
				@Override
				public void send(WebSocketMessage<?> msg) {
					try {
						session.sendMessage(msg);
					} catch(Exception e) {
						logger.info("session id:{} doSend0 Exception:", session.getId(), e);
					}
				}
			});
		}
	}
}
