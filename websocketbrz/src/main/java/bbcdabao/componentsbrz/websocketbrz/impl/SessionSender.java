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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;

import bbcdabao.componentsbrz.websocketbrz.api.IGetMsgForSend;

/**
 * Separate thread sending for WebSocketMessage module
 */
public class SessionSender implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(SessionSender.class);

	/**
	 * The unit of sending PING cycle is milliseconds, must not be null
	 */
	private WebSocketSession session;

	/**
	 * Then WebSocketMessage get for send
	 */
	private IGetMsgForSend getMsgForSend;

	private AtomicLong timeSet;

	public SessionSender(@NotNull(message = "session must not null") WebSocketSession session,
			@NotNull(message = "IGetMsgForSend must not null") IGetMsgForSend getMsgForSend,
			@NotNull(message = "timeSet must not null") AtomicLong timeSet) {
		this.session = session;
		this.getMsgForSend = getMsgForSend;
		this.timeSet = timeSet;
	}

	@Override
	public void run() {
		try (WebSocketSession sessionClose = session) {
			while (session.isOpen()) {
				WebSocketMessage<?> msg = getMsgForSend.getMsg();
				if (null != msg) {
					try {
						session.sendMessage(msg);
					} catch(Exception e) {
						logger.info("session id:{} doSend0 Exception:", session.getId(), e);
					}
					timeSet.set(0);
				}
			}
		} catch (Exception e) {
			logger.error("session id:{} doSend Exception", session.getId(), e);
		}
	}
}
