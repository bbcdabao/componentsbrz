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

package bbcdabao.componentsbrz.messagebrz.api;

import bbcdabao.componentsbrz.messagebrz.config.MessageBrzAutoConfig;

/**
 * Message base class
 */
public class Message {

	/**
	 * Dest module name
	 */
	private String dest;

	/**
	 * Process delay time ms
	 */
	private long delayTime = 0;

	public Message() {
	}

	public Message(String dest) {
		this.dest = dest;
	}

	public boolean post() {
		return MessageBrzAutoConfig.post(dest, this);
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
