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

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import bbcdabao.componentsbrz.websocketbrz.exception.WebsocketbrzException;

/**
 * Session framework abstract base class
 */
public abstract class AbstractSessionServer {

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

	/**
	 * When connected call back
	 * note:
	 * (1) If you use the original WebSocketSession to send messages, you can save and use it within your
	 *  session to send messages; however, it might be synchronous.
	 * (2) You can also use DefaultMsgQueueForSend, which requires passing a sending queue, BlockingQueue
	 * <WebSocketMessage<?>> msgList. You just need to write messages into the queue, and it will have a
	 *  thread that automatically reads and sends them; this process is certainly asynchronous.
	 * (3) Lastly, you can also implement IGetMsgForSend yourself. It has an interface, WebSocketMessage
	 * <?> getMsg(), that you need to implement, which can directly connect to the module you need to send 
	 * messages to.
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public IGetMsgForSend onAfterConnectionEstablished(WebSocketSession session)
			throws Exception {
		return null;
	}

	/**
	 * Error handling occurs
	 * @param exception
	 * @throws Exception
	 */
	public void onHandleTransportError(Throwable exception) throws Exception {
	}

	/**
	 * Close call back
	 * @param closeStatus
	 * @throws Exception
	 */
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
			onTextMessage((TextMessage)message);
		} else if (message instanceof BinaryMessage) {
			onBinaryMessage((BinaryMessage)message);
		} else if (message instanceof PongMessage) {
			onPongMessage((PongMessage)message);
		} else {
			throw new WebsocketbrzException("session" + "onHandleMessage error:" + message);
		}
	}
}
