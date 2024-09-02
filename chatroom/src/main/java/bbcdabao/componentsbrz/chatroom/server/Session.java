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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static String AALL = "all";
    private static String AREGEX = "@\\S+\\b";
    private static Pattern PATTERN = Pattern.compile(AREGEX);
    /**
     *  Matcher matcher = pattern.matcher(input);
     *  while (matcher.find()) {
            // 获取匹配的子串
            String match = matcher.group();
            matches.add(match);
        }
     */

    private static class SendChanlMgr {
        private Map<String, BlockingQueue<TextMessage>> mgr = new HashMap<>(100);
        private synchronized boolean register(String name, BlockingQueue<TextMessage> sendChanl) {
            return mgr.put(name, sendChanl) == null;
        }
        private synchronized void delete(String name) {
            mgr.remove(name)
        }
        private synchronized void broadcast(TextMessage message) {
            mgr.add(sendChanl);
        }
    }
    private static SendChanlMgr SENDCHANLMGR = new SendChanlMgr();

    private final Logger logger = LoggerFactory.getLogger(Session.class);

    @SessionSenderQue
    private BlockingQueue<TextMessage> sendChanl = new LinkedBlockingQueue<>();

    @Override
    public void onTextMessage(TextMessage message) throws Exception {
    }

    @Override
    public void onAfterConnectionEstablished(WebSocketSession session, IRegGetMsgForSend regGetMsgForSend) throws Exception {
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
    }
}
