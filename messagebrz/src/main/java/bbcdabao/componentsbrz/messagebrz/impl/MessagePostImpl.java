package bbcdabao.componentsbrz.messagebrz.impl;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.componentsbrz.messagebrz.IMessagePost;
import bbcdabao.componentsbrz.messagebrz.api.Message;

/**
 * -ʵ���൥�߳�����
 * @author bao
 *
 */
public class MessagePostImpl extends Thread implements IMessagePost {

	private final Logger logger = LoggerFactory.getLogger(MessagePostImpl.class);

	/**
	 * -���б��
	 */
	private AtomicBoolean running = new AtomicBoolean(true);

	/**
	 * -���ȶ���
	 */
	private LinkedList<Message> workList = new LinkedList<>();

	/**
	 * -ӳ����Ϣ
	 */
	private MessageModel messageModel = null;

	/**
	 * -���붨ʱ��Ϣ
	 * -���ö���ͷ�����㷨
	 * -�Ӷ���ͷ��β����ʱ�����
	 * -����ͷʼ�������Ҫ�������Ϣ
	 * @param message
	 */
	private synchronized void addWork(Message message) {
		boolean isAddHead = true;
		ListIterator<Message> it = workList.listIterator();
		for (; it.hasNext() ;) {
			Message idx = it.next();
			if (idx.getDelayTime() <= message.getDelayTime()) {
				isAddHead = false;
				continue;
			} else {
				it.previous();
				break;
			}
		}
		it.add(message);
		if (isAddHead) {
			notifyAll();
		}
	}

	/**
	 * -��ȡһ������ͷ������Ϣ
	 * @return
	 * @throws InterruptedException
	 */
	private synchronized Message getWork() throws InterruptedException {
		Message message = workList.peek();
		if (null == message) {
			wait(60000L);
			return null;
		}
		long now = System.currentTimeMillis();
		long tim = message.getDelayTime() - now;
		if (0 < tim) {
			wait(tim);
			return null;
		}
		workList.remove();
		return message;
	}

	private Message getWorkProc() {
		Message message = null;
		try {
			message = getWork();
		}
		catch (Exception e) {
			logger.error("Messager error:", e.getMessage());
		}
		return message;
	}

	private void processing(final Message message) {
		if (null != message) {
			messageModel.callMethod(message);
		}
	}

	public MessagePostImpl(Builder builder) {
		messageModel = builder.messageModel;
	}

	/**
	 * -ʵ��
	 */
	@Override
	public boolean post(Message message) {
		if (null != message) {
			addWork(message);
		}
		return false;
	}

	@Override
	public void run() {
		while (running.get()) {
			processing(getWorkProc());
		}
	}

	/**
	 * -������
	 */
	public static class Builder {

		private MessageModel messageModel = null;

		public Builder setCourierModel(MessageModel messageModel) {
			this.messageModel = messageModel;
			return this;
		}

		public MessagePostImpl build() {
			MessagePostImpl messagePostImpl = new MessagePostImpl(this);
			messagePostImpl.start();
			return messagePostImpl;
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}
