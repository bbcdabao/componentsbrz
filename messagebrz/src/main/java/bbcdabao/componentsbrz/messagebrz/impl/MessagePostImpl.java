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

package bbcdabao.componentsbrz.messagebrz.impl;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.componentsbrz.messagebrz.IMessagePost;
import bbcdabao.componentsbrz.messagebrz.api.Message;

/**
 * Implement class single-threaded driver
 */
public class MessagePostImpl extends Thread implements IMessagePost {

	private final Logger logger = LoggerFactory.getLogger(MessagePostImpl.class);

	private AtomicBoolean running = new AtomicBoolean(true);

	/**
	 * Dispatch queue
	 */
	private LinkedList<Message> workList = new LinkedList<>();

	/**
	 * Mapping message
	 */
	private MessageModel messageModel = null;

	/**
	 * Insert scheduled message
	 * Using head-of-queue scheduling algorithm
	 * Scheduling time increases from the beginning to the end of the queue
	 * The head of the queue is always the most recent message to be processed
	 * @param message
	 */
	private synchronized void addWork(Message message) {
		boolean isAddHead = true;
		ListIterator<Message> it = workList.listIterator();
		for (; it.hasNext() ;) {
			Message idx = it.next();
			if (idx.getDelayTime() <= message.getDelayTime()) {
				isAddHead = false;
				continue;
			} else {
				it.previous();
				break;
			}
		}
		it.add(message);
		if (isAddHead) {
			notifyAll();
		}
	}

	/**
	 * Get a queue head expiration message
	 * @return
	 * @throws InterruptedException
	 */
	private synchronized Message getWork() throws InterruptedException {
		Message message = workList.peek();
		if (null == message) {
			wait(60000L);
			return null;
		}
		long now = System.currentTimeMillis();
		long tim = message.getDelayTime() - now;
		if (0 < tim) {
			wait(tim);
			return null;
		}
		workList.remove();
		return message;
	}

	private Message getWorkProc() {
		Message message = null;
		try {
			message = getWork();
		}
		catch (Exception e) {
			logger.error("Messager error:", e.getMessage());
		}
		return message;
	}

	private void processing(final Message message) {
		if (null != message) {
			messageModel.callMethod(message);
		}
	}

	public MessagePostImpl(Builder builder) {
		messageModel = builder.messageModel;
	}

	/**
	 * -实现
	 */
	@Override
	public boolean post(Message message) {
		if (null != message) {
			addWork(message);
		}
		return false;
	}

	@Override
	public void run() {
		while (running.get()) {
			processing(getWorkProc());
		}
	}

	public static class Builder {

		private MessageModel messageModel = null;

		public Builder setCourierModel(MessageModel messageModel) {
			this.messageModel = messageModel;
			return this;
		}

		public MessagePostImpl build() {
			MessagePostImpl messagePostImpl = new MessagePostImpl(this);
			messagePostImpl.start();
			return messagePostImpl;
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}
