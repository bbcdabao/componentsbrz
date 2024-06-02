package bbcdabao.componentsbrz.websocketbrz.api;

/**
 * -会话工厂接口
 * -它会实现创建ISessionServer对象内部的自动装载功能
 * @author bao
 *
 */
public interface ISessionFactory {
	/**
	 * -获取链接会话
	 * @return
	 * @throws Exception
	 */
	AbstractSessionServer getSession() throws Exception;
}
