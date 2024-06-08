package bbcdabao.componentsbrz.websocketbrz.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import bbcdabao.componentsbrz.websocketbrz.api.AbstractSessionServer;

/**
 * -服务会话实现
 * @author bao
 *
 */
public class BrzWebSocketServer {

	private final Logger logger = LoggerFactory.getLogger(BrzWebSocketServer.class);

	private final AtomicBoolean run = new AtomicBoolean(true);

	private final Map<String, Node> sessions = new ConcurrentHashMap<>(50);

	private class Node {
		private WebSocketSession session = null;
		private AbstractSessionServer sessionServer = null;
		private Map<String, String> queryMap = null;
		private AtomicLong timeSet = new AtomicLong(0);
	}

}
