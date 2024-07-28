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

package bbcdabao.componentsbrz.terminalhub.terminalagents.sshtelnetagent;

import java.util.Map;

import bbcdabao.componentsbrz.websocketbrz.api.AbstractSessionServer;
import bbcdabao.componentsbrz.websocketbrz.api.ISessionFactory;
import bbcdabao.componentsbrz.websocketbrz.api.annotation.SessionFactoryBrz;

@SessionFactoryBrz("sshfactory")
public class SessionAgentFactory implements ISessionFactory {

    @Override
    public AbstractSessionServer getSession(Map<String, String> queryMap) throws Exception {
    	SessionAgent sessionAgent = new SessionAgent(queryMap);
    	return sessionAgent;
    }
}
