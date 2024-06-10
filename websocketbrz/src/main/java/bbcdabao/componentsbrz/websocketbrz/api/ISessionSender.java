package bbcdabao.componentsbrz.websocketbrz.api;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.web.socket.WebSocketMessage;

/**
 * -�첽���ͽӿ�
 * @author bao
 *
 */
public interface ISessionSender extends Closeable {
	/**
	 * -������Ϣ
	 * -���ڲ�����������˻��������������ʱ��
	 * -��Ϊ���п������������Ի��׳�InterruptedException
	 * @param msg
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	boolean postMsg(WebSocketMessage<?> msg, long timeout, TimeUnit unit) throws InterruptedException;
	/**
	 * -������Ϣ
	 * -��������
	 * @param msg
	 * @return
	 */
	boolean postMsg(WebSocketMessage<?> msg);
	/**
	 * -ͨ���Ƿ��
	 * @return
	 */
	boolean isOpen();
	/**
	 * -��ȡ�Ự��ID
	 * @return
	 */
	String getSessionId();
	/**
	 * -�ýӿڻ��ڷ����߳����汻���ȣ�����һ�����ͽ��
	 * -�ýӿڿ������û�����WebSocketMessage��Ϣ�������ظ���ʹ�ñ���Ƶ��new
	 * @author zhao
	 *
	 */
	public static interface IComplete {
		/**
		 * -������ɻص�
		 * @param msg
		 * @param ok
		 * @param exception
		 * @throws Exception
		 */
		void onComplete(WebSocketMessage<?> msg, boolean ok, Throwable exception) throws Exception;
	}
}
