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

package bbcdabao.componentsbrz.websocketbrz.api;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import bbcdabao.componentsbrz.websocketbrz.exception.WebsocketbrzException;

/**
 * Session framework abstract base class
 */
public abstract class AbstractSessionServer {

	private final Logger logger = LoggerFactory.getLogger(AbstractSessionServer.class);

	private AtomicLong timeSet = null;
	private String sessionId = null;

	/**
	 * Handle receiving text messages
	 * @param message
	 * @throws Exception
	 */
	public void onTextMessage(TextMessage message) throws Exception {	
	}

	/**
	 * Handle accepting binary messages
	 * @param message
	 * @throws Exception
	 */
	public void onBinaryMessage(BinaryMessage message) throws Exception {
	}

	/**
	 * Handle keep-alive messages
	 * @param message
	 * @throws Exception
	 */
	public void onPongMessage(PongMessage message) throws Exception {
	}

	public void onAfterConnectionEstablished(ISessionSenderGeter sessionSenderGeter, Map<String, String> queryMap)
			throws Exception {
	}

	/**
	 * Error handling occurs
	 * @param exception
	 * @throws Exception
	 */
	public void onHandleTransportError(Throwable exception) throws Exception {
	}
	
	public void onAfterConnectionClosed(CloseStatus closeStatus) throws Exception {
	}

	/**
	 * Message processing
	 * Defined as not to be overridden
	 * Subclasses do not need to override this method
	 * @param message
	 * @throws Exception
	 */
	final public void onHandleMessage(WebSocketMessage<?> message) throws Exception {
		if (message instanceof TextMessage) {
			timeSet.set(0);
			onTextMessage((TextMessage)message);
		} else if (message instanceof BinaryMessage) {
			timeSet.set(0);
			onBinaryMessage((BinaryMessage)message);
		} else if (message instanceof PongMessage) {
			logger.info("session id:{} get pong <<<", sessionId);
			onPongMessage((PongMessage)message);
		} else {
			throw new WebsocketbrzException("session id:" + sessionId + "onHandleMessage error:" + message);
		}
	}
}
