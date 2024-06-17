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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.componentsbrz.messagebrz.api.Message;
import bbcdabao.componentsbrz.messagebrz.exception.MessageBrzException;

/**
 * -函数映射关系
 * @author bao
 *
 */
public class MessageModel {

	private final Logger logger = LoggerFactory.getLogger(MessageModel.class);

	private Object object = null;
	private Map<Class<?>, Method> methodMap = new HashMap<>(100);

	private void callMethodInner(Message message) throws Exception {
		Class<?> clazz = message.getClass();
		Method method = methodMap.get(clazz);
		if (null == method) {
			logger.info("MessageModel not process message:{}", clazz.getSimpleName());
			return;
		}
		method.invoke(object, message);
	}

	public MessageModel(Builder builder) {
		object = builder.object;
		methodMap = builder.methodMap;
	}

	public void callMethod(Message message) {
		try {
			callMethodInner(message);
		}
		catch(Exception e) {
			logger.info("MessageModel Exception:{}", e.getMessage());
		}
	}

	public static class Builder {

		private Object object = null;
		private Map<Class<?>, Method> methodMap = null;

		private boolean addMethod(Class<?> clazz, Method method) {
			Method methodFind = methodMap.get(clazz);
			if (null != methodFind) {
				return false;
			}
			methodMap.put(clazz, method);
			return true;
		}
		private void paramMethod(Method method) throws Exception {
			Class<?>[] paramClazzs = method.getParameterTypes();
			if (paramClazzs.length != 1) {
				return;
			}
			Class<?> paramClazz = paramClazzs[0];
			if (!Message.class.isAssignableFrom(paramClazz)) {
				return;
			}
			Class<?> retType = method.getReturnType();
			if (!"void".equals(retType.getName())) {
				return;
			}
			if (!addMethod(paramClazz, method)) {
				throw new MessageBrzException("Message Type Multiple");
			}
		}

		public Builder setObject(Object objectInit) throws Exception {
			object = objectInit;
			methodMap = new HashMap<>(100);
			Class<?> clazz = object.getClass();
			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				paramMethod(method);
			}
			return this;
		}

		public MessageModel build() {
			return new MessageModel(this);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}
