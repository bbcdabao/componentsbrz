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

package bbcdabao.componentsbrz.terminalhub.utils;

import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * the util for addr sub 
 */
public class AddrUtil {

    public static class ParseInetSocketAddressException extends Exception {
        static final long serialVersionUID = -1L;
        public ParseInetSocketAddressException(String msg) {
            super(msg);
        }
    }

    private static final String IPREGEX =
            "^((25[0-5]|2[0-4][0-9]|[1-9][0-9]?|0)\\.){3}(25[0-5]|2[0-4][0-9]|[1-9][0-9]?|0):([0-9]{1,5})$";
    private static final Pattern PATTERN = Pattern.compile(IPREGEX);

    /**
     * convert like xxxx.xxxx.xxxx.xxxx:port to InetSocketAddress object
     * @param address
     * @return
     * @throws ParseInetSocketAddressException
     */
    public static InetSocketAddress parseInetSocketAddress(String address) throws ParseInetSocketAddressException {
        Matcher matcher = PATTERN.matcher(address);
        if (!matcher.matches()) {
            throw new ParseInetSocketAddressException("matche error");
        }
        String[] parts = address.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);
        return new InetSocketAddress(host, port);
    }
}
