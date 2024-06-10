package bbcdabao.componentsbrz.websocketbrz.api;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.web.socket.WebSocketMessage;

/**
 * -异步发送接口
 * @author bao
 *
 */
public interface ISessionSender extends Closeable {
	/**
	 * -发送消息
	 * -当内部缓冲队列满了会阻塞参数填入的时间
	 * -因为它有可能阻塞，所以会抛出InterruptedException
	 * @param msg
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	boolean postMsg(WebSocketMessage<?> msg, long timeout, TimeUnit unit) throws InterruptedException;
	/**
	 * -发送消息
	 * -不会阻塞
	 * @param msg
	 * @return
	 */
	boolean postMsg(WebSocketMessage<?> msg);
	/**
	 * -通道是否打开
	 * @return
	 */
	boolean isOpen();
	/**
	 * -获取会话曾ID
	 * @return
	 */
	String getSessionId();
	/**
	 * -该接口会在发送线程里面被调度，返回一部发送结果
	 * -该接口可以让用户回收WebSocketMessage消息，便于重复过使用避免频繁new
	 * @author zhao
	 *
	 */
	public static interface IComplete {
		/**
		 * -发送完成回调
		 * @param msg
		 * @param ok
		 * @param exception
		 * @throws Exception
		 */
		void onComplete(WebSocketMessage<?> msg, boolean ok, Throwable exception) throws Exception;
	}
}
