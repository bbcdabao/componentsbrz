package bbcdabao.componentsbrz.websocketbrz.api;

/**
 * -�Ự�����ӿ�
 * -����ʵ�ִ���ISessionServer�����ڲ����Զ�װ�ع���
 * @author bao
 *
 */
public interface ISessionFactory {
	/**
	 * -��ȡ���ӻỰ
	 * @return
	 * @throws Exception
	 */
	AbstractSessionServer getSession() throws Exception;
}
