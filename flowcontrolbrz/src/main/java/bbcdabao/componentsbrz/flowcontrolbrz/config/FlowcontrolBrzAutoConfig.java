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

package bbcdabao.componentsbrz.flowcontrolbrz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import bbcdabao.componentsbrz.flowcontrolbrz.service.ITokencyctl;
import bbcdabao.componentsbrz.flowcontrolbrz.service.ITokencyctl.TokenCfg;
import bbcdabao.componentsbrz.flowcontrolbrz.service.impl.TokencyctlImpl;

/**
 * Flow controlBrz autoConfig
 */
@Configuration
public class FlowcontrolBrzAutoConfig implements WebMvcConfigurer {

	private TokencyctlImpl tokencyctlImpl = new TokencyctlImpl();

	@Value("${flowcontrol-brz.tps:1}")
	private int tps = 1;
	@Value("${flowcontrol-brz.max-token-bucket:1}")
	private int maxTokenBucket = 1;

	@Autowired
	private Environment env;

    @Bean(name = "tokencyctl")
    private ITokencyctl getTokencyctl() {
		return tokencyctlImpl;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String path = env.getProperty("flowcontrol-brz.path", "/**");
		String[] paths = path.split(",");
		TokenCfg tokenCfg = new TokenCfg();
		tokenCfg.addToken = tps;
		tokenCfg.curToken = maxTokenBucket;
		tokenCfg.maxToken = maxTokenBucket;
		tokencyctlImpl.setTokenCfg(tokenCfg);
		registry.addInterceptor(tokencyctlImpl).addPathPatterns(paths);
	}
}
