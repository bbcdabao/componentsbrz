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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;

/**
 * The default message queue for sender module
 */
public class DefaultMsgQueueForSend implements IGetMsgForSend {

	private final Logger logger = LoggerFactory.getLogger(DefaultMsgQueueForSend.class);

	/**
	 * message queue from out side
	 */
	private BlockingQueue<WebSocketMessage<?>> msgList;

	public DefaultMsgQueueForSend(@NotNull BlockingQueue<WebSocketMessage<?>> msgList) {
		this.msgList = msgList;
	}

	public WebSocketMessage<?> getMsg() throws Exception {
		WebSocketMessage<?> msg = null;
		try {
			msg = msgList.poll(1000, TimeUnit.MICROSECONDS);
		} catch (InterruptedException e) {
			logger.info("sender is Interrupted");
		}
		return msg;
	}
}

