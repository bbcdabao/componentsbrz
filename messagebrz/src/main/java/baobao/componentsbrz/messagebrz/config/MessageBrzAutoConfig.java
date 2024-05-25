package baobao.componentsbrz.messagebrz.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import baobao.componentsbrz.messagebrz.IMessagePost;
import baobao.componentsbrz.messagebrz.api.Message;
import baobao.componentsbrz.messagebrz.api.annotation.MessageHandler;
import baobao.componentsbrz.messagebrz.exception.MessageBrzException;
import baobao.componentsbrz.messagebrz.impl.MessageModel;
import baobao.componentsbrz.messagebrz.impl.MessagePostImpl;

/**
 * -自动配置加载控制
 * @author bao
 *
 */
public class MessageBrzAutoConfig implements ApplicationRunner, ApplicationContextAware {

	/**
	 * -核心调用消息映射关系
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
