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
 * -��ܳ��������
 * @author bao
 *
 */
public abstract class AbstractSessionServer {

	private final Logger logger = LoggerFactory.getLogger(AbstractSessionServer.class);

	private AtomicLong timeSet = null;
	private String sessionId = null;

	/**
	 * -��������ı���Ϣ
	 * @param message
	 * @throws Exception
	 */
	protected void onTextMessage(TextMessage message) throws Exception {	
	}

	/**
	 * -������ܶ�������Ϣ
	 * @param message
	 * @throws Exception
	 */
	protected void onBinaryMessage(BinaryMessage message) throws Exception {
	}

	/**
	 * -��������Ϣ
	 * @param message
	 * @throws Exception
	 */
	protected void onPongMessage(PongMessage message) throws Exception {
	}

	/**
	 * -���ִ�����
	 * @param exception
	 * @throws Exception
	 */
	protected void onHandleTransportError(Throwable exception) throws Exception {
	}

	/**
	 * -��Ϣ����
	 * -����Ϊ�����Ա�����
	 * -����������д�÷���
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
