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

package bbcdabao.componentsbrz.messagebrz.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import bbcdabao.componentsbrz.messagebrz.IMessagePost;
import bbcdabao.componentsbrz.messagebrz.api.Message;
import bbcdabao.componentsbrz.messagebrz.api.annotation.MessageHandler;
import bbcdabao.componentsbrz.messagebrz.exception.MessageBrzException;
import bbcdabao.componentsbrz.messagebrz.impl.MessageModel;
import bbcdabao.componentsbrz.messagebrz.impl.MessagePostImpl;

/**
 * Auto config loading module
 */
public class MessageBrzAutoConfig implements ApplicationRunner, ApplicationContextAware {

	/**
	 * Core call message mapping relationshi
	 */
	private static volatile Map<String, IMessagePost> messagePostMap = null;

	public static boolean post(String messageHandlerName, Message message) {
		if (null == message) {
			return false;
		}
		IMessagePost messagePost = messagePostMap.get(messageHandlerName);
		if (null == messagePost) {
			return false;
		}
		return messagePost.post(message);
	}

	private final Logger logger = LoggerFactory.getLogger(MessageBrzAutoConfig.class);

	private ApplicationContext context;

	private IMessagePost getMessagePost(Object serviceBean) {
		IMessagePost messagePost = null;
		try {
			messagePost = MessagePostImpl.builder().setCourierModel(MessageModel.builder().setObject(serviceBean).build()).build();
		}
		catch (Exception e) {
			logger.info("getMessagePost error:{}", e.getMessage());
		}
		return messagePost;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		context = applicationContext;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Map<String, Object> serviceBeanMap = context.getBeansWithAnnotation(MessageHandler.class);

		if (null == serviceBeanMap) {
			return;
		}

		Map<String, IMessagePost> messagePostMapCreate = new HashMap<>(100);
		for (Object serviceBean : serviceBeanMap.values()) {
			MessageHandler messageHandler = serviceBean.getClass().getAnnotation(MessageHandler.class);
			String messageHandlerName = messageHandler.value();
			if (messagePostMapCreate.containsKey(messageHandlerName)) {
				throw new MessageBrzException("message handler name confict");
			}
			IMessagePost messagePost = getMessagePost(serviceBean);
			messagePostMapCreate.put(messageHandlerName, messagePost);
		}
	}
}
