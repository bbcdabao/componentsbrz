package bbcdabao.componentsbrz.messagebrz;

import bbcdabao.componentsbrz.messagebrz.api.Message;

/**
 * -������Ϣ�ӿ�
 * @author bao
 *
 */
public interface IMessagePost {

	/**
	 * -����Ҫ�������Ϣ����
	 * @param message
	 * @return
	 */
	boolean post(Message message);
}
