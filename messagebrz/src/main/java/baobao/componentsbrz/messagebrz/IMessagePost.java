package baobao.componentsbrz.messagebrz;

import baobao.componentsbrz.messagebrz.api.Message;

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
