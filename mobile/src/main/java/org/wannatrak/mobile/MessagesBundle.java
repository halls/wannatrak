/*
 * Copyright 2009 Andrey Khalzov, and individual contributors as indicated by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.wannatrak.mobile;

import java.util.Hashtable;
import java.io.*;

/**
 * Created 25.06.2009 21:58:41
 *
 * @author Andrey Khalzov
 */
public class MessagesBundle {
    private static Hashtable map = null;

    private final String className;

    public static String getGlobal(Class clazz, String key) {
        return getGlobal(clazz.getName() + "." + key);
    }

    public static String getGlobal(String key) {
        return (String) getMap().get(key);
    }

    public static MessagesBundle getMessageBundle(String className) {
        return new MessagesBundle(className);
    }

    public String get(String key) {
        return getGlobal(className + "." + key);
    }

    private static Hashtable getMap() {
        if (map == null) {
            map = new Hashtable();
            
            String messages = readMessages();
            final char[] chars = messages.toCharArray();

            StringBuffer key = new StringBuffer();
            StringBuffer value = new StringBuffer();
            for (int i = 0; i < chars.length; i++) {
                final char ch = chars[i];
                if (ch == '#') {
                    key = new StringBuffer();
                    value = new StringBuffer();

                    while (i < chars.length && chars[i] != '\n') {
                        i++;
                    }
                    continue;
                }

                if (ch != '=') {
                    key.append(ch);
                } else {
                    i++;
                    while (i < chars.length && chars[i] != '\n') {
                        value.append(chars[i++]);
                    }
                    map.put(key.toString().trim(), value.toString().trim());
                    key = new StringBuffer();
                    value = new StringBuffer();
                }
            }
        }
        return map;
    }

    private MessagesBundle(String className) {
        this.className = className;
    }

    private static String readMessages() {
        String messages = "";
        Reader reader = null;
        try {
            final StringBuffer sb = new StringBuffer();
            reader = new InputStreamReader(
                    org.wannatrak.mobile.MessagesBundle.class.getResourceAsStream(getFileName()),
                    "utf-8"
            );

            char[] buffer = new char[1024];
            int read = reader.read(buffer);
            while (read > 0) {
                sb.append(buffer, 0, read);
                read = reader.read(buffer);
            }
            messages = sb.toString();

        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return messages;
    }

    private static String getFileName() {
        return "/messages" + Locale.getCurrent() + ".properties";
    }
}
