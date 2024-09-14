/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package bbcdabao.componentsbrz.flowcontrolbrz.service.impl;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import bbcdabao.componentsbrz.flowcontrolbrz.api.annotation.Flowcontrol;
import bbcdabao.componentsbrz.flowcontrolbrz.exception.FlowcontrolbrzException;
import bbcdabao.componentsbrz.flowcontrolbrz.service.ITokencyctl;

/*
 * Tokencyctl impl
 */
public class TokencyctlImpl implements HandlerInterceptor, ITokencyctl {

	private int addToken = 0;
	private int curToken = 0;
	private int maxToken = 0;

	private Timer runTimer = new Timer("TokencyctlCycle");

	private synchronized void setTokenInner(int addToken, int curToken, int maxToken) {
		this.addToken = addToken;
		this.curToken = curToken;
		this.maxToken = maxToken;
	}

	private synchronized boolean isDropped(int useToken) {
		if (curToken < useToken) {
			return true;
		}
		curToken -= useToken;
		return false;
	}

	private synchronized void addTokenStep() {
		curToken += addToken;
		if (maxToken < curToken) {
			curToken = maxToken;
		}
	}

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
