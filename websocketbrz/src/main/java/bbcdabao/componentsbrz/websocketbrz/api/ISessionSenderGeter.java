package bbcdabao.componentsbrz.websocketbrz.api;

/**
 * -���ڻ�ȡ���͸��ͻ�����Ϣ�Ľӿ�
 * @author bao
 *
 */
public interface ISessionSenderGeter {
	/**
	 * -��ȡ���͸��ͻ�����Ϣ����
	 * -�÷����ɿ��ʵ��
	 * @param complete
	 * @return
	 */
	ISessionSender getSessionSender(ISessionSender.IComplete complete) throws Exception;
}
