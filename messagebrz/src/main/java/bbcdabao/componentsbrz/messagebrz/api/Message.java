package bbcdabao.componentsbrz.messagebrz.api;

/**
 * -��Ϣ����
 * @author bao
 *
 */
public class Message {

	/**
	 * -Ŀ��ģ������
	 */
	private String dest;

	/**
	 * -�ӳٴ���ʱ�����
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
