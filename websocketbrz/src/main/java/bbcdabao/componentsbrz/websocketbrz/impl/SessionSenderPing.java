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

import java.util.concurrent.atomic.AtomicLong;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * This module for send Ping message in time cyc
 */
public class SessionSenderPing implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(SessionSenderPing.class);

	/**
	 * The unit of sending PING cycle is milliseconds, must not be null
	 */
	private WebSocketSession session;

	private AtomicLong timeSet;

	private long pingCyc = 0;
	private long pingFlg = 0;

	private void checkAndSendPing() {
		long thisSet = timeSet.get();
		if (0 == thisSet) {
			// Just send the message and return directly without sending PING again.
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

	public SessionSenderPing(@NotNull(message = "session must not null") WebSocketSession session,
			@NotNull(message = "timeSet must not null") AtomicLong timeSet, long pingCyc) {
		this.session = session;
		this.timeSet = timeSet;
		this.pingCyc = pingCyc;
	}

	@Override
	public void run() {
		try {
			Thread currentThread = Thread.currentThread();
			boolean isRunning = !currentThread.isInterrupted();
			while (session.isOpen() && isRunning) {
				try {
					Thread.sleep(1000);
				} catch(Exception e) {
					logger.info("session id:{} sleep for ping Exception:{}", session.getId(), e.getMessage());
					continue;
				}
				checkAndSendPing();
			}
		} catch (Exception e) {
			logger.error("session id:{} doSend Exception", session.getId(), e);
		}
	}
}
