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
	 * @author bao
	 *
	 */
	private static interface ISend {
		/**
		 * -发送
		 * @param msg
		 */
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

	private void doComplete(WebSocketMessage<?> msg, boolean ok, Throwable exception) {
		try {
			complete.onComplete(msg, ok, exception);
		} catch (Exception e) {
			logger.error("session id:{} onComplete Exception", session.getId(), e);
		}
	}
	
	private void doSend(ISend send) {
		try (WebSocketSession sessionClose = session) {
			WebSocketMessage<?> msg = getMsg();
			if (null != msg) {
				send.send(msg);
				timeSet.set(0);
			} else {
				checkAndSendPing();
			}
			
		} catch (Exception e) {
			logger.error("session id:{} doSend Exception", session.getId(), e);
		}
	}

	private void checkAndSendPing() {
		
	}
}
