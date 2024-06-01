package bbcdabao.componentsbrz.flowcontrolbrz.service.impl;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import bbcdabao.componentsbrz.flowcontrolbrz.api.annotation.Flowcontrol;
import bbcdabao.componentsbrz.flowcontrolbrz.exception.FlowcontrolbrzException;
import bbcdabao.componentsbrz.flowcontrolbrz.service.ITokencyctl;

/**
 * -ʵ��
 * @author bao
 *
 */
public class TokencyctlImpl implements HandlerInterceptor, ITokencyctl {

	/**
	 * -addToken:ÿ���������������
	 * -curToken:��ǰ����Ͱ������
	 * -maxToken:����Ͱ�������
	 */
	private int addToken = 0;
	private int curToken = 0;
	private int maxToken = 0;

	/**
	 * -���˿��Ƽ�������һ��һ�Σ�ÿ��TPS
	 */
	private Timer runTimer = new Timer("TokencyctlCycle");

	/**
	 * -�������Ʋ���
	 * @param addToken
	 * @param curToken
	 * @param maxToken
	 */
	private synchronized void setTokenInner(int addToken, int curToken, int maxToken) {
		this.addToken = addToken;
		this.curToken = curToken;
		this.maxToken = maxToken;
	}

	/**
	 * -�����Ҫ���ص�API���ж��Ƿ�������
	 * @param useToken
	 * @return
	 */
	private synchronized boolean isDropped(int useToken) {
		if (curToken < useToken) {
			return true;
		}
		curToken -= useToken;
		return false;
	}

	/**
	 * -ÿ�����һ��
	 */
	private synchronized void addTokenStep() {
		curToken += addToken;
		if (maxToken < curToken) {
			curToken = maxToken;
		}
	}

	/**
	 * -ÿ��һ�Σ��̶��ٶ�����
	 */
	private void runPerSecond() {
		addTokenStep();
	}

	public TokencyctlImpl() {
		runTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				runPerSecond();
			}
		}, 1000, 1000);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (null == handler) {
			return true;
		}
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		HandlerMethod handlerMethod = (HandlerMethod)handler;
		Method method = handlerMethod.getMethod();
		if (null == method) {
			return true;
		}
		Flowcontrol flowcontrol = method.getAnnotation(Flowcontrol.class);
		if (null == flowcontrol) {
			return true;
		}
		int useToken = flowcontrol.value();
		if (!isDropped(useToken)) {
			return true;
		}
		throw new FlowcontrolbrzException("no token!");
	}

	@Override
	public void setTokenCfg(TokenCfg tokenCfg) {
		setTokenInner(tokenCfg.addToken, tokenCfg.curToken, tokenCfg.maxToken);
	}

	@Override
	public TokenCfg getTokenCfg() {
		TokenCfg tokenCfg = new TokenCfg();
		tokenCfg.addToken = addToken;
		tokenCfg.curToken = curToken;
		tokenCfg.maxToken = maxToken; 
		return tokenCfg;
	}
}
