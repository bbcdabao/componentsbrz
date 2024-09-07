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
 * Implementing the chat room function
 */
public class Session  extends AbstractSessionServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(Session.class);

    /**
     * Extract @xxx from the sent string, and then send it in a targeted manner.
     * This is the extraction template
     */
    private static String AREGEX = "@\\S+\\b";
    private static Pattern PATTERN = Pattern.compile(AREGEX);

    /**
     * The send information
     * Add to the queue(sendChanlIndex) and it will be sent automatically
     */
    private static class SendChanlMgr {
    	/**
    	 * Send information corresponding to the registered name
    	 */
        private Map<String, Session> sessionMap = new HashMap<>(100);
        private synchronized void register(Session session) {
        	Optional.ofNullable(sessionMap.put(session.name, session)).ifPresent(sessionOld -> {
        		try (WebSocketSession tobeClose = sessionOld.session) {
        	        TextMessage messageSend = new TextMessage("有其他人用您的名字登录，您被踢掉了 :( "
        	        		+ "\n Someone else is logged in with your name, So you were fired :(");
        	        sessionOld.sendChanl.offer(messageSend);
        	        sessionOld.name = null;
        		} catch (IOException e) {
        			LOGGER.info("SendChanlMgr:IOException:%s", sessionOld.session.getId());
        		}
        	}); 
        }
        private synchronized void unregister(Session session) {
        	if (null == session.name) {
        		return;
        	}
        	sessionMap.remove(session.name);
        }
        private synchronized void sendsmsg(List<String> names, TextMessage message) {
        	if (ObjectUtils.isEmpty(names)) {
        		sessionMap.values().forEach(sessionIdx -> {
        			sessionIdx.sendChanl.offer(message);
        		});
        		return;
        	}
        	names.forEach(name -> {
        		Optional.ofNullable(sessionMap.get(name)).ifPresent(sessionIdx -> {
        			sessionIdx.sendChanl.offer(message);
        		});
        	});
        }
        private synchronized String getAllNames() {
        	StringBuilder sb = new StringBuilder();
    		sb.append("\nAll the names:");
        	sessionMap.keySet().forEach(name -> {
        		sb.append("\n@" + name);
        	});
        	return sb.toString();
        }
    }

    private static SendChanlMgr SENDCHANLMGR = new SendChanlMgr();

    /**
     * The command not message beginning with #
     */
    private static String CMDTER = "#";
    private static interface DoCmdter {
    	String  doCmd() throws Exception;
    }

    /**
     * The command mapping , key is command value is interface to run
     */
    private static Map<String, DoCmdter> CMDMAP = new HashMap<>(10);
    static {
    	CMDMAP.put("#list", () -> {
    		return SENDCHANLMGR.getAllNames();
    	});
    }

    public Session(String name) throws Exception {
    	this.name = name;
    }

    private String name;

    @SessionSenderQue
    private BlockingQueue<TextMessage> sendChanl = new LinkedBlockingQueue<>();

    private WebSocketSession session = null;

    private void doCommand(String cmd) {
    	String cmdRet = "";
    	try {
    		DoCmdter doCmdter = CMDMAP.get(cmd);
    		if (null == doCmdter) {
    			cmdRet = "\nno had the command:" + cmd;
    		} else {
    			cmdRet = doCmdter.doCmd();
    		}
    	} catch (Exception e) {
    		cmdRet = e.getMessage();
    	}
        TextMessage messageSend = new TextMessage(cmdRet);
    	sendChanl.offer(messageSend);
    }

    @Override
    public void onTextMessage(TextMessage message) throws Exception {
    	String msg = message.getPayload();
    	if (ObjectUtils.isEmpty(msg)) {
    		return;
    	}
    	if (msg.startsWith(CMDTER)) {
    		doCommand(msg);
    		return;
    	}
    	List<String> matches = null;
        Matcher matcher = PATTERN.matcher(msg);
        while (matcher.find()) {
            String match = matcher.group();
            String nameNatch = match.trim().substring(1);
            if (null == matches) {
            	matches = new ArrayList<>();
            	matches.add(name);
            }
            matches.add(nameNatch);
        }
        TextMessage messageSend = new TextMessage(name + ":" + msg);
        SENDCHANLMGR.sendsmsg(matches, messageSend);
    }

    @Override
    public void onAfterConnectionEstablished(WebSocketSession session, IRegGetMsgForSend regGetMsgForSend) throws Exception {
    	this.session = session;
    	SENDCHANLMGR.register(this);
        TextMessage messageSend = new TextMessage("*** " + name + ", 加入聊天室 / Join chat room ***");
        SENDCHANLMGR.sendsmsg(null, messageSend);
    }

    @Override
    public void onHandleTransportError(Throwable exception) throws Exception {
    }

    /**
     * Close the open resource init the onAfterConnectionEstablished function
     */
    @Override
    public void onAfterConnectionClosed(CloseStatus closeStatus) throws Exception {
    	SENDCHANLMGR.unregister(this);
    }
}
