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

package bbcdabao.componentsbrz.websocketbrz.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import bbcdabao.componentsbrz.websocketbrz.api.AbstractSessionServer;
import bbcdabao.componentsbrz.websocketbrz.api.IGetMsgForSend;
import bbcdabao.componentsbrz.websocketbrz.api.ISessionFactory;
import bbcdabao.componentsbrz.websocketbrz.api.annotation.SessionFactoryBrz;
import bbcdabao.componentsbrz.websocketbrz.exception.WebsocketbrzException;

/**
 * Web Socket callback abstract implementation, core code
 */
public class BrzWebSocketServer extends Thread implements InitializingBean, DisposableBean, WebSocketHandler, ApplicationContextAware {

	private final Logger logger = LoggerFactory.getLogger(BrzWebSocketServer.class);

	private final AtomicBoolean run = new AtomicBoolean(true);

	private final Map<String, Node> sessionMap = new ConcurrentHashMap<>(50);

	/**
 	 * Access link encapsulation for internal use
	 */
	private class Node {
		private AbstractSessionServer sessionServer = null;
		private AtomicLong timeSet = new AtomicLong(0);
		private WebSocketSession session = null;
		private SessionSender sessionSender = null;
		private SessionSenderPing sessionSenderPing = null;
	}

	@Autowired
	private Executor wscThreadPoolExecutor;

	/**
	 * Bean container
	 */
	private ApplicationContext context = null;


	/**
	 * SESSION factory container
	 * Start initial creation
	 */
	private final Map<String, ISessionFactory> sessionFactoryMap = new HashMap<>(20);

	/**
	 * Partial support
	 */
	private boolean isPartialMsg = false;

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
	 * Traverse to find out the timeout session
	 * If there is a message received or sent, the flowing timeSet will be set to 0.
	 * 
	 * @return To be clear WebSocketSession list
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
		AbstractSessionServer sessionServer = sessionFactory.getSession(queryMap);
		if (sessionServer == null) {
			throw new WebsocketbrzException("sessionServer is null");
		}
		context.getAutowireCapableBeanFactory().autowireBean(sessionServer);
		Node node = new Node();
		node.session = session;
		node.sessionServer = sessionServer;

		IGetMsgForSend getMsgForSend = sessionServer.onAfterConnectionEstablished();
		if (getMsgForSend != null) {
			node.sessionSender = new SessionSender(node.session, getMsgForSend, node.timeSet);
			wscThreadPoolExecutor.execute(node.sessionSender);
		}
		if (0 < pingCyc) {
			node.sessionSenderPing = new SessionSenderPing(node.session, node.timeSet, pingCyc);
			wscThreadPoolExecutor.execute(node.sessionSenderPing);
		}
		return node;
	}

	public BrzWebSocketServer(boolean isPartialMsg, long timeCyc, long stepOut, int maxSessions, long pingCyc) {
		this.isPartialMsg = isPartialMsg;
		this.timeCyc = timeCyc;
		this.stepOut = stepOut;
		this.maxSessions = maxSessions;
		this.pingCyc = pingCyc;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
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

	@Override
	public void destroy() throws Exception {
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
			sessionMap.put(session.getId(), node);
		} catch (Exception e) {
			logger.info("session id:{} getNode Exception:{}", session.getId(), e.getMessage());
			session.close();
			return;
		}
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		try {
			Node node = sessionMap.get(session.getId());
			node.sessionServer.onHandleMessage(message);
			node.timeSet.set(0);
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
