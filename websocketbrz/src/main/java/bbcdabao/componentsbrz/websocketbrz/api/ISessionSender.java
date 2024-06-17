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

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import org.springframework.web.socket.WebSocketMessage;

/**
 * Asynchronous sending interface
 */
public interface ISessionSender extends Closeable {
	
	/**
	 * Send message
	 * When the internal buffer queue is full, it will block the parameter filling time.
	 * Because it has the potential to block, it will throwInterruptedException
	 * @param msg
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	boolean postMsg(WebSocketMessage<?> msg, long timeout, TimeUnit unit) throws InterruptedException;
	
	/**
	 * Send message
	 * Not blocking
	 * @param msg
	 * @return
	 */
	boolean postMsg(WebSocketMessage<?> msg);
	
	/**
	 * Channel is openning
	 * @return
	 */
	boolean isOpen();
	
	/**
	 * Get Websocket seddion ID
	 * @return
	 */
	String getSessionId();
	
	/**
	 * This interface will be scheduled in the sending thread and return a sending result
	 * This interface allows users to recycle WebSocketMessage messages to facilitate reuse and avoid frequent new buffer
	 * @author zhao
	 *
	 */
	public static interface IComplete {
		
		/**
		 * Send completed call back
		 * @param msg
		 * @param ok
		 * @param exception
		 * @throws Exception
		 */
		void onComplete(WebSocketMessage<?> msg, boolean ok, Throwable exception) throws Exception;
	}
}
