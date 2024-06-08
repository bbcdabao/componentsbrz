package bbcdabao.componentsbrz.websocketbrz.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import bbcdabao.componentsbrz.websocketbrz.api.annotation.SessionInterceptor;
import bbcdabao.componentsbrz.websocketbrz.exception.WebsocketbrzException;


/**
 * -会话拦截用于后续认证需要
 * @author bao
 *
 */
public class BrzHandshakeInterceptor implements HandshakeInterceptor, ApplicationContextAware {
	private final Logger logger = LoggerFactory.getLogger(BrzHandshakeInterceptor.class);
	private List<HandshakeInterceptor> interceptors = new ArrayList<>();
	/**
	 * -bean容器
	 */
	private ApplicationContext context;

	@PostConstruct
	public void initServer() throws Exception {
		Pattern pattern = Pattern.compile("[0-9]*");
		Map<String, Object> serviceBeanMap = context.getBeansWithAnnotation(SessionInterceptor.class);
		if (serviceBeanMap == null) {
			return;
		}
		if (serviceBeanMap.size() <= 0) {
			return;
		}
		Map<Integer, HandshakeInterceptor> handshakeInterceptorMap = new TreeMap<>(new Comparator<Integer>() {
			@Override
			public int compare(Integer obj1, Integer obj2) {
				return obj1.intValue() - obj2.intValue();
			}
		});
		for (Object serviceBean : serviceBeanMap.values()) {
			if ( !(serviceBean instanceof HandshakeInterceptor) ) {
				throw new WebsocketbrzException("the class:" + serviceBean.getClass().getSimpleName() + "must implements HandshakeInterceptor");
			}
			HandshakeInterceptor interceptor = (HandshakeInterceptor)serviceBean;
			SessionInterceptor interceptorAn = serviceBean.getClass().getAnnotation(SessionInterceptor.class);
			String priority = interceptorAn.priority();
			if (ObjectUtils.isEmpty(priority)) {
				throw new WebsocketbrzException("SessionInterceptor the value is empty");
			}
			Matcher isNum = pattern.matcher(priority);
			if (!isNum.matches()) {
				throw new WebsocketbrzException("SessionInterceptor the value is not number");
			}
			Integer priorityInteger = Integer.parseInt(priority);
			HandshakeInterceptor interceptorGet = handshakeInterceptorMap.get(priorityInteger);
			if (interceptorGet != null) {
				throw new WebsocketbrzException("SessionInterceptor the interceptor already had:" + priority);				
			}
			handshakeInterceptorMap.put(priorityInteger, interceptor);
		}
		for (Map.Entry<Integer, HandshakeInterceptor> entry : handshakeInterceptorMap.entrySet()) {
			interceptors.add(entry.getValue());
			logger.info(">>> add interceptor:{}", entry.getKey());
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		for (HandshakeInterceptor interceptor : interceptors) {
			if (!interceptor.beforeHandshake(request, response, wsHandler, attributes)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, @Nullable Exception exception) {
		for (HandshakeInterceptor interceptor : interceptors) {
			interceptor.afterHandshake(request, response, wsHandler, exception);
		}
	}
}
