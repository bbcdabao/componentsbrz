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

package bbcdabao.componentsbrz.websocketbrz.config;

import java.io.Closeable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bbcdabao.componentsbrz.websocketbrz.impl.BrzHandshakeInterceptor;
import bbcdabao.componentsbrz.websocketbrz.impl.BrzWebSocketServer;

/**
 * Start component configuration
 */
@Configuration
public class Config {

	private final Logger logger = LoggerFactory.getLogger(Config.class);

	@Value("${wscfg.isPartialMsg:true}")	
	private boolean isPartialMsg;

	@Value("${wscfg.senderCapacity:2000}")
	private long timeCyc;

	@Value("${wscfg.stepOut:30}")
	private long stepOut;

	@Value("${wscfg.maxSessions:20}")
	private int maxSessions;

	@Value("${wscfg.pingCyc:0}")
	private long pingCyc;

	/**
	 * Thread pool config
	 */
    @Value("${wscfg.threadPool.corePoolSize:20}")
    private Integer corePoolSize;

    @Value("${wscfg.threadPool.maxPoolSize:40}")
    private Integer maxPoolSize;

    @Value("${wscfg.threadPool.queueCapacity:50}")
    private Integer queueCapacity;

    @Value("${wscfg.threadPool.keepAliveSeconds:10}")
    private Integer keepAliveSeconds;

    @Value("${wscfg.threadPool.threadNamePrefix:wsc-sendthread}")
    private String threadNamePrefix;

	@Bean
	public BrzWebSocketServer brzWebSocketServer() {
		return new BrzWebSocketServer(isPartialMsg, timeCyc, stepOut, maxSessions, pingCyc);
	}

	@Bean
	public BrzHandshakeInterceptor brzHandshakeInterceptor() {
		return new BrzHandshakeInterceptor();
	}

	/**
	 * For sender worker
	 * @return
	 */
    @Bean
    public ExecutorService wscThreadPoolExecutor() {
    	ExecutorService executor = new ThreadPoolExecutor(
    				corePoolSize,
    				maxPoolSize,
    				keepAliveSeconds,
    				TimeUnit.SECONDS,
    				new ArrayBlockingQueue<>(queueCapacity),
    				new ThreadFactory() {
    					private int count = 0;
    					@Override
    					public Thread newThread(Runnable r) {
    						Thread thread = new Thread(r, threadNamePrefix + "-" + count);
    						count++;
    				        return thread;
    					}
    				},
    				new RejectedExecutionHandler() {
    					@Override
    				    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    						if (r instanceof Closeable) {
    							Closeable rClost = (Closeable)r;
    							try {
    								rClost.close();
    							} catch (Exception e) {
    								logger.info("wscThreadPoolExecutor RejectedExecutionHandler error:{}", e.getMessage());
    							}
    						}
    					}
    				}
    			);
        return executor;
    }
}
