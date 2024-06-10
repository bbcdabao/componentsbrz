package bbcdabao.componentsbrz.websocketbrz.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import bbcdabao.componentsbrz.websocketbrz.api.AbstractSessionServer;
import bbcdabao.componentsbrz.websocketbrz.api.ISessionFactory;
import bbcdabao.componentsbrz.websocketbrz.api.ISessionSender;
import bbcdabao.componentsbrz.websocketbrz.api.ISessionSenderGeter;
import bbcdabao.componentsbrz.websocketbrz.api.annotation.SessionFactoryBrz;
import bbcdabao.componentsbrz.websocketbrz.exception.WebsocketbrzException;

/**
 * -服务会话实现
 * -核心代码
 * @author bao
 *
 */
public class BrzWebSocketServer extends Thread implements WebSocketHandler, ApplicationContextAware {

	private final Logger logger = LoggerFactory.getLogger(BrzWebSocketServer.class);

	private final AtomicBoolean run = new AtomicBoolean(true);

	private final Map<String, Node> sessionMap = new ConcurrentHashMap<>(50);

	private class Node {
		private AbstractSessionServer sessionServer = null;
		private AtomicLong timeSet = new AtomicLong(0);
		private Map<String, String> queryMap = null;
		private WebSocketSession session = null;
	}

	/**
	 * -bean容器
	 */
	private ApplicationContext context = null;


	/**
	 * -SESSION 工厂容器
	 * -启动初始化创建
	 */
	private final Map<String, ISessionFactory> sessionFactoryMap = new HashMap<>(20);

	/**
	 * -参数
	 */
	private boolean isPartialMsg = false;

	/**
	 * -容量
	 * -异步发送队列容量
	 */
	private int senderCapacity = 0;

	private long timeCyc = 0;
	
	private long stepOut = 0;

	private int maxSessions = 0;

	private long pingCyc = 0;

	private void closeWebSocketSession(WebSocketSession session) {
		try {
			session.close();
		} catch (IOException e) {
			logger.error("session id:{} closeWebSocketSession IOException:", session.getId(), e);
		}
	}

	/**
	 * -遍历找出超市会话
	 * -如果有信息接受或者发送的流动timeSet都会被设置为0
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<WebSocketSession> checkNodes() throws Exception {
		Thread.sleep(timeCyc);
		long timeNow = System.currentTimeMillis();
		List<WebSocketSession> webSocketSessions = null;
		for (Map.Entry<String, Node> entry : sessionMap.entrySet()) {
			Node node = entry.getValue();
			if (!node.session.isOpen()) {
				logger.info("warning! the session is closed bu it here! :{}", node.session.getId());
			}
			if (0 == node.timeSet.get()) {
				node.timeSet.set(timeNow);
				continue;
			}
			long timeSub = timeNow - node.timeSet.get();
			long stepNow = timeSub / timeCyc;
			if (stepOut >= stepNow) {
				continue;
			}
			if (null == webSocketSessions) {
				webSocketSessions = new ArrayList<>();
			}
			webSocketSessions.add(node.session);
		}
		return webSocketSessions;
	}
	
	private void checkNodesAndCLose() throws Exception {
		List<WebSocketSession> webSocketSessions = checkNodes();
		if (null == webSocketSessions) {
			return;
		}
		for (WebSocketSession session : webSocketSessions) {
			logger.info("session id:{} is time out ", session.getId());
			closeWebSocketSession(session);
		}
	}

	private void runStep() {
		try {
			checkNodesAndCLose();
		} catch (Exception e) {
			logger.error("session runSt Exception:", e);
		}
	}
	
	private Node getNode(WebSocketSession session) throws Exception {
		String query = session.getUri().getQuery();
		if (ObjectUtils.isEmpty(query)) {
			throw new WebsocketbrzException("query is empty!");
		}
		Map<String, String> queryMap = new HashMap<>(20);
		List<NameValuePair> queryPairs = URLEncodedUtils.parse(query, Charset.forName("UTF-8"));
		for (NameValuePair queryPair : queryPairs) {
			String queryValue = queryMap.get(queryPair.getName());
			if (ObjectUtils.isEmpty(queryValue)) {
				throw new WebsocketbrzException("queryValue is conflict!");
			}
			queryMap.put(queryPair.getName(), queryPair.getValue());
		}
		String sessionfactory = queryMap.get("sessionfactory");
		if (ObjectUtils.isEmpty(sessionfactory)) {
			throw new WebsocketbrzException("sessionfactory name is empty");
		}
		ISessionFactory sessionFactory = sessionFactoryMap.get(sessionfactory);
		if (sessionFactory == null) {
			throw new WebsocketbrzException("sessionfactory is null");
		}
		AbstractSessionServer sessionServer = sessionFactory.getSession();
		if (sessionServer == null) {
			throw new WebsocketbrzException("sessionServer is null");
		}
		Node node = new Node();
		Field privateTimeSetField = AbstractSessionServer.class.getDeclaredField("timeSet");
		privateTimeSetField.setAccessible(true);
		privateTimeSetField.set(sessionServer, node.timeSet);
		Field privateSessionIdField = AbstractSessionServer.class.getDeclaredField("sessionId");
		privateSessionIdField.setAccessible(true);
		privateSessionIdField.set(sessionServer, session.getId());
		node.session = session;
		node.sessionServer = sessionServer;
		node.queryMap = queryMap;
		return node;
	}

	private void initNode(Node node) throws Exception {
		// info: 自动装置SESSION
		context.getAutowireCapableBeanFactory().autowireBean(node.sessionServer);
		node.sessionServer.onAfterConnectionEstablished(new ISessionSenderGeter() {
			@Override
			public ISessionSender getSessionSender(ISessionSender.IComplete complete) throws Exception {
				SessionSender sessionSender = new SessionSender(senderCapacity, node.session, complete, node.timeSet, pingCyc);
				return sessionSender;
			}
		}, node.queryMap);
	}

	public BrzWebSocketServer(boolean isPartialMsg, int senderCapacity, long timeCyc, long stepOut, int maxSessions, long pingCyc) {
		this.isPartialMsg = isPartialMsg;
		this.senderCapacity = senderCapacity;
		this.timeCyc = timeCyc;
		this.stepOut = stepOut;
		this.maxSessions = maxSessions;
		this.pingCyc = pingCyc;
	}

	@PostConstruct
	public void initServer() throws Exception {
		Map<String, Object> serviceBeanMap = context.getBeansWithAnnotation(SessionFactoryBrz.class);
		if (serviceBeanMap == null) {
			return;
		}
		if (serviceBeanMap.size() <= 0) {
			return;
		}
		for (Object serviceBean : serviceBeanMap.values()) {
			if ( !(serviceBean instanceof ISessionFactory) ) {
				throw new WebsocketbrzException("the class:" + serviceBean.getClass().getSimpleName() + " must extends ISessionFactory!");
			}
			ISessionFactory factory = (ISessionFactory)serviceBean;
			SessionFactoryBrz sessionFactoryBrz = serviceBean.getClass().getAnnotation(SessionFactoryBrz.class);
			String factoryname = sessionFactoryBrz.value();
			if (null != sessionFactoryMap.get(factoryname)) {
				throw new WebsocketbrzException("the class:" + serviceBean.getClass().getSimpleName() + " had same factoryname!");
			}
			sessionFactoryMap.put(factoryname, factory);
		}
		start();
	}

	@PreDestroy
	public void destroyServer() {
		run.set(false);
		interrupt();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.info("session id:{} come in ...", session.getId());
		if (maxSessions <= sessionMap.size()) {
			logger.info("session id:{} more than:{}", session.getId(), maxSessions);
			session.close();
			return;
		}
		Node node = null;
		try {
			node = getNode(session);
		} catch (Exception e) {
			logger.info("session id:{} getNode Exception:{}", session.getId(), e.getMessage());
			session.close();
			return;
		}
		sessionMap.put(session.getId(), node);
		try {
			initNode(node);
		} catch (Exception e) {
			session.close();
		}
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		try {
			Node node = sessionMap.get(session.getId());
			node.sessionServer.onHandleMessage(message);
		} catch (Exception e) {
			session.close();
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		try {
			Node node = sessionMap.get(session.getId());
			node.sessionServer.onHandleTransportError(exception);
		} catch (Exception e) {
			session.close();
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		try {
			Node node = sessionMap.get(session.getId());
			node.sessionServer.onAfterConnectionClosed(closeStatus);
		} catch (Exception e) {
			session.close();
		} finally {
			sessionMap.remove(session.getId());
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return isPartialMsg;
	}

	@Override
	public void run() {
		while (run.get()) {
			runStep();
		}
	}
}
