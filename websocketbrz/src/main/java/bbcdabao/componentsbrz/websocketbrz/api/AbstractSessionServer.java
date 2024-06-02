package bbcdabao.componentsbrz.websocketbrz.api;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import bbcdabao.componentsbrz.websocketbrz.exception.WebsocketbrzException;

/**
 * -框架抽象基础类
 * @author bao
 *
 */
public abstract class AbstractSessionServer {

	private final Logger logger = LoggerFactory.getLogger(AbstractSessionServer.class);

	private AtomicLong timeSet = null;
	private String sessionId = null;

	/**
	 * -处理接收文本消息
	 * @param message
	 * @throws Exception
	 */
	protected void onTextMessage(TextMessage message) throws Exception {	
	}

	/**
	 * -处理接受二进制消息
	 * @param message
	 * @throws Exception
	 */
	protected void onBinaryMessage(BinaryMessage message) throws Exception {
	}

	/**
	 * -处理保活消息
	 * @param message
	 * @throws Exception
	 */
	protected void onPongMessage(PongMessage message) throws Exception {
	}

	/**
	 * -出现错误处理
	 * @param exception
	 * @throws Exception
	 */
	protected void onHandleTransportError(Throwable exception) throws Exception {
	}

	/**
	 * -消息处理
	 * -定义为不可以被覆盖
	 * -子类无需重写该方法
	 * @param message
	 * @throws Exception
	 */
	final public void onHandleMessage(WebSocketMessage<?> message) throws Exception {
		if (message instanceof TextMessage) {
			timeSet.set(0);
			onTextMessage((TextMessage)message);
		} else if (message instanceof BinaryMessage) {
			timeSet.set(0);
			onBinaryMessage((BinaryMessage)message);
		} else if (message instanceof PongMessage) {
			logger.info("session id:{} get pong <<<", sessionId);
			onPongMessage((PongMessage)message);
		} else {
			throw new WebsocketbrzException("session id:" + sessionId + "onHandleMessage error:" + message);
		}
	}
}
