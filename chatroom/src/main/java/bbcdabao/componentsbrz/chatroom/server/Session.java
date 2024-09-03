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

package bbcdabao.componentsbrz.chatroom.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import bbcdabao.componentsbrz.websocketbrz.api.AbstractSessionServer;
import bbcdabao.componentsbrz.websocketbrz.api.IRegGetMsgForSend;
import bbcdabao.componentsbrz.websocketbrz.api.annotation.SessionSenderQue;

/**
 * Implementing Remote Login
 */
public class Session  extends AbstractSessionServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(Session.class);

    private static String AREGEX = "@\\S+\\b";
    private static Pattern PATTERN = Pattern.compile(AREGEX);

    private static class SendNode {
    	private BlockingQueue<TextMessage> sendChanlIndex;
    	private WebSocketSession session;
    }

    private static class SendChanlMgr {

        private Map<String, SendNode> mgr = new HashMap<>(100);

        private synchronized void register(String name, SendNode sendNode) {
        	Optional.ofNullable(mgr.put(name, sendNode)).ifPresent(sendNodeOld -> {
        		try (sendNodeOld.session) {
        			LOGGER.info("SendChanlMgr: tick out  %s:%s", name, sendNodeOld.session.getId());
        		} catch (IOException e) {
        			LOGGER.info("SendChanlMgr: tick out  IOException:%s:%s", name, sendNodeOld.session.getId());
        		}
        	});
        }

        private synchronized void sendsmsg(List<String> names, TextMessage message) {
        	if (ObjectUtils.isEmpty(names)) {
        		mgr.values().forEach(sendNodeNow -> {
        			sendNodeNow.sendChanlIndex.offer(message);
        		});
        		return;
        	}
        	names.forEach(name -> {
        		Optional.ofNullable(mgr.get(name)).ifPresent(sendNodeNow -> {
        			sendNodeNow.sendChanlIndex.offer(message);
        		});
        	});
        }

        private synchronized void godelete(String name, String id) {
        	SendNode sendNode = mgr.get(name);
        	if (null == sendNode) {
        		return;
        	}
        	if (!sendNode.session.getId().equals(id)) {
        		return;
        	}
        	mgr.remove(name);
        }
    }

    private static SendChanlMgr SENDCHANLMGR = new SendChanlMgr();
    
    public Session(String name) throws Exception {
    	this.name = name;
    }

    private final String name;
    private String id;

    @SessionSenderQue
    private BlockingQueue<TextMessage> sendChanl = new LinkedBlockingQueue<>();

    @Override
    public void onTextMessage(TextMessage message) throws Exception {
    	String msg = message.getPayload();
    	List<String> matches = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(msg);
        while (matcher.find()) {
            String match = matcher.group();
            matches.add(match);
        }
        SENDCHANLMGR.sendsmsg(matches, message);
    }

    @Override
    public void onAfterConnectionEstablished(WebSocketSession session, IRegGetMsgForSend regGetMsgForSend) throws Exception {
    	id = session.getId();
    	SendNode sendNode = new SendNode();
    	sendNode.sendChanlIndex = sendChanl;
    	sendNode.session = session;
    	SENDCHANLMGR.register(name, sendNode);
    }

    @Override
    public void onHandleTransportError(Throwable exception) throws Exception {
    }

    /**
     * Close the open resource init the onAfterConnectionEstablished function
     */
    @Override
    public void onAfterConnectionClosed(CloseStatus closeStatus) throws Exception {
    	SENDCHANLMGR.godelete(name, id);
    }
}
