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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import bbcdabao.componentsbrz.terminalhub.utils.AddrUtil;
import bbcdabao.componentsbrz.websocketbrz.api.AbstractSessionServer;
import bbcdabao.componentsbrz.websocketbrz.api.IRegGetMsgForSend;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;

/**
 * Implementing Remote Login
 */
public class SessionAgent  extends AbstractSessionServer {

    private final Logger logger = LoggerFactory.getLogger(SessionAgent.class);

    private String sessionId;

    private InetSocketAddress address;
    private String user;
    private String pass;

    private OutputStream oStream;
    private List<Closeable> arryCloseable = new ArrayList<>();

	public SessionAgent(@NotNull Map<String, String> queryMap) throws Exception {
	    address = AddrUtil.parseInetSocketAddress(queryMap.get("addr"));
		user = URLDecoder.decode(queryMap.get("user"), StandardCharsets.UTF_8.name());
		if (ObjectUtils.isEmpty(user)) {
			throw new Exception("user is empty!");
		}
		pass = URLDecoder.decode(queryMap.get("pass"), StandardCharsets.UTF_8.name());
		if (ObjectUtils.isEmpty(pass)) {
			throw new Exception("pass is empty!");
		}
	}

    @Override
    public void onTextMessage(TextMessage message) throws Exception {
    	String payloadMessage = message.getPayload();
    	logger.info(">:{}", payloadMessage);
        oStream.write(payloadMessage.getBytes(StandardCharsets.UTF_8));
        oStream.flush();
    }

    @Override
    public void onAfterConnectionEstablished(WebSocketSession session, IRegGetMsgForSend regGetMsgForSend) throws Exception {
    	sessionId = session.getId();

    	SSHClient ssh = new SSHClient();
    	arryCloseable.add(ssh);

    	ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.connect(address.getHostName(), address.getPort());
        ssh.authPassword(user, pass);
        
        Session sshSession = ssh.startSession();
        arryCloseable.add(sshSession);

        sshSession.allocateDefaultPTY();
       	
        Shell shell =  sshSession.startShell();
       	arryCloseable.add(shell);

        InputStream iStream = shell.getInputStream();
       	arryCloseable.add(iStream);
        InputStream iStreamError = shell.getErrorStream();
        arryCloseable.add(iStreamError);
       	
        oStream = shell.getOutputStream();
       	arryCloseable.add(oStream);

       	byte[] buffer = new byte[1024];
        regGetMsgForSend.regGetMsgForSend(() -> {
            int readSize = iStream.read(buffer, 0, 1024);
            if (0 >= readSize) {
                throw new Exception("doSendProc read lower 0");
            }
            String data = new String(buffer, 0, readSize);
            TextMessage msg = new TextMessage(data);
    		return msg;
        });
        byte[] erinfo = new byte[1024];
        regGetMsgForSend.regGetMsgForSend(() -> {
            int readSize = iStreamError.read(erinfo, 0, 1024);
            if (0 >= readSize) {
                throw new Exception("doSendProc read lower 0");
            }
            String data = new String(buffer, 0, readSize);
            TextMessage msg = new TextMessage(data);
            return msg;
        });
    }

    @Override
    public void onHandleTransportError(Throwable exception) throws Exception {
        logger.info("session hashcode:{} onHandleTransportError", hashCode());
    }

    /**
     * Close the open resource init the onAfterConnectionEstablished function
     */
    @Override
    public void onAfterConnectionClosed(CloseStatus closeStatus) throws Exception {
        logger.info("session:{} onAfterConnectionClosed", sessionId);
        for (Closeable closeable : arryCloseable) {
            try (closeable) {
                logger.info("session:{} close:{}", sessionId, closeable.getClass().getSimpleName());
            } catch (IOException e) {
                logger.info("session:{} IOException:{}", sessionId, closeable.getClass().getSimpleName());
            }
        }
    }
}
