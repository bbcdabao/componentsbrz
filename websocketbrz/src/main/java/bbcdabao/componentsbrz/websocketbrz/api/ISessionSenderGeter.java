package bbcdabao.componentsbrz.websocketbrz.api;

/**
 * -用于获取发送给客户端消息的接口
 * @author bao
 *
 */
public interface ISessionSenderGeter {
	/**
	 * -获取发送给客户端消息方法
	 * -该方法由框架实现
	 * @param complete
	 * @return
	 */
	ISessionSender getSessionSender(ISessionSender.IComplete complete) throws Exception;
}
