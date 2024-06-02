package bbcdabao.componentsbrz.websocketbrz.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import bbcdabao.componentsbrz.websocketbrz.api.ISessionSender.IComplete;

/**
 * -服务端向客户端发送实现
 * -可被多线程调度发送
 * @author bao
 *
 */
public class SessionSender {
	/**
	 * -发送转接口
	 * @author zhao
	 *
	 */
	private static interface ISend {
		void send(WebSocketMessage<?> msg);
	}

	private final Logger logger = LoggerFactory.getLogger(SessionSender.class);

	private BlockingQueue<WebSocketMessage<?>> msgList = null;

	private WebSocketSession session = null;

	private IComplete complete = null;

	private long pingCyc = 0;

	private long pingFlg = 0;

	private AtomicLong timeSet = null;

	private WebSocketMessage<?> getMsg() {
		WebSocketMessage<?> msg = null;
		try {
			msg = msgList.poll(1000, TimeUnit.MICROSECONDS);
		} catch (InterruptedException e) {
			logger.info("session id:{} sender is Interrupted", session.getId());
		}
		return msg;
	}
}
