package bbcdabao.componentsbrz.messagebrz.impl;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.componentsbrz.messagebrz.IMessagePost;
import bbcdabao.componentsbrz.messagebrz.api.Message;

/**
 * -实现类单线程驱动
 * @author bao
 *
 */
public class MessagePostImpl extends Thread implements IMessagePost {

	private final Logger logger = LoggerFactory.getLogger(MessagePostImpl.class);

	/**
	 * -运行标记
	 */
	private AtomicBoolean running = new AtomicBoolean(true);

	/**
	 * -调度队列
	 */
	private LinkedList<Message> workList = new LinkedList<>();

	/**
	 * -映射消息
	 */
	private MessageModel messageModel = null;

	/**
	 * -插入定时消息
	 * -采用队列头调度算法
	 * -从队列头到尾调度时间递增
	 * -队列头始终是最近要处理的消息
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
	 * -获取一个队列头到期消息
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
	 * -实现
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
	 * -构造器
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
