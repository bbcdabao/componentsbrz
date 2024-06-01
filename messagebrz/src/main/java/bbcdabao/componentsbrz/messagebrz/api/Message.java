package bbcdabao.componentsbrz.messagebrz.api;

/**
 * -消息基类
 * @author bao
 *
 */
public class Message {

	/**
	 * -目的模块名称
	 */
	private String dest;

	/**
	 * -延迟处理时间毫秒
	 */
	private long delayTime = 0;

	public Message() {
	}

	public Message(String dest) {
		this.dest = dest;
	}

	public boolean post() {
		return false;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getDest() {
		return this.dest;
	}

	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}

	public long getDelayTime() {
		return this.delayTime;
	}
}
