package baobao.componentsbrz.messagebrz;

import baobao.componentsbrz.messagebrz.api.Message;

/**
 * -发送消息接口
 * @author bao
 *
 */
public interface IMessagePost {

	/**
	 * -发送要处理的消息对象
	 * @param message
	 * @return
	 */
	boolean post(Message message);
}
