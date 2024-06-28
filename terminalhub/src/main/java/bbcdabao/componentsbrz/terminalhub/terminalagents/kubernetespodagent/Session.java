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

package bbcdabao.componentsbrz.terminalhub.terminalagents.kubernetespodagent;

import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import bbcdabao.componentsbrz.websocketbrz.api.AbstractSessionServer;
import bbcdabao.componentsbrz.websocketbrz.api.IGetMsgForSend;
import io.fabric8.kubernetes.client.Config;

import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.ExecListenable;

/**
 * Implementing Remote Login Sessions with KUBERNETES
 */
public class Session  extends AbstractSessionServer {

    private final Logger logger = LoggerFactory.getLogger(Session.class);

    private String nameSpace;
    private String namePod;
    private String nameContainer;
	private Config config;

    private ExecListenable execListenable = null;

    private List<Closeable> arryCloseable = new ArrayList<>();

	public Session(@NotNull Map<String, String> queryMap) throws Exception {
		String masterUrl = queryMap.get("masterUrl");
		if (ObjectUtils.isEmpty(masterUrl)) {
			throw new Exception("Session create error no masterUrl");
		}
		String oauthToken = queryMap.get("oauthToken");		
		String namespace = queryMap.get("namespace");
		if (ObjectUtils.isEmpty(namespace)) {
			namespace = "default";
		}
        nameSpace = queryMap.get("space");
        if (ObjectUtils.isEmpty(nameSpace)) {
            throw new Exception("nameSpace not had !");
        }
        namePod = queryMap.get("pod");
        if (ObjectUtils.isEmpty(namePod)) {
            throw new Exception("namePod not had !");
        }
        nameContainer = queryMap.get("container");
	    this.config = new ConfigBuilder()
	            .withMasterUrl(masterUrl)
	            .withOauthToken(oauthToken)
	            .withNamespace(namespace)
	            .withTrustCerts(true)
	            .build();
	}

    @Override
    public void onTextMessage(TextMessage message) throws Exception {
    	String receivedMessage = message.getPayload();
    	execListenable.exec(receivedMessage);
    }

    @Override
    public IGetMsgForSend onAfterConnectionEstablished(WebSocketSession session) throws Exception {
    	KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build();
    	arryCloseable.add(client);
    	PipedOutputStream oStream = new PipedOutputStream();
    	arryCloseable.add(oStream);
        PipedInputStream iStream = new PipedInputStream(oStream);
        arryCloseable.add(iStream);
        execListenable = client.pods().inNamespace(nameSpace).withName(namePod).inContainer(nameContainer)
                		.writingOutput(oStream).writingError(oStream).withTTY();
        return new IGetMsgForSend() {
        	private BinaryMessage msg = new BinaryMessage(ByteBuffer.allocate(2048));
        	public WebSocketMessage<?> getMsg() throws Exception {
                ByteBuffer byteBuffer = msg.getPayload();
                byteBuffer.clear();
                byte[] readBuffer = byteBuffer.array();
                int readSize = iStream.read(readBuffer, 0, readBuffer.length);
                if (0 >= readSize) {
                    throw new Exception("doSendProc read lower 0");
                }
                byteBuffer.limit(readSize);
        		return msg;
        	}
        };
    }

    @Override
    public void onHandleTransportError(Throwable exception) throws Exception {
        logger.info("session hashcode:{} onHandleTransportError", hashCode());
    }

    /**
     * Close the open resource int the onAfterConnectionEstablished function
     */
    @Override
    public void onAfterConnectionClosed(CloseStatus closeStatus) throws Exception {
        logger.info("session hashcode:{} onAfterConnectionClosed", hashCode());
        for (Closeable closeable : arryCloseable) {
            try (closeable) {
            	logger.info("closed...");
            } catch (IOException e) {
                System.err.println("Failed to close " + closeable + ": " + e.getMessage());
            }
        }
    }
}
