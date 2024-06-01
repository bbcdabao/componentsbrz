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

	/**
	 * -构造器构造
	 */
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

	/**
	 * -构造器
	 */
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
