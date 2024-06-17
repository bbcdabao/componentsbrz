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
	 * -该接口会在发送线程里面被调度，返回一部发送结果
	 * -该接口可以让用户回收WebSocketMessage消息，便于重复过使用避免频繁new
	 * @author zhao
	 *
	 */
	public static interface IComplete {
		/**
		 * -发送完成回调
		 * @param msg
		 * @param ok
		 * @param exception
		 * @throws Exception
		 */
		void onComplete(WebSocketMessage<?> msg, boolean ok, Throwable exception) throws Exception;
	}
}
