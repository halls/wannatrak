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

package org.wannatrak.middleware.util;

import java.util.Map;
import java.util.Properties;

/**
 * Created 16.04.2009 19:58:49
 *
 * @author Sergey Grachev
 */
public class ConvertHelper {
    protected static final byte[] HEX_ALPHABET = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String bytes2Hex(byte[] bytes) {
        final StringBuilder result = new StringBuilder(2 * bytes.length);
        for (final byte item : bytes) {
            result.append((char) HEX_ALPHABET[(item & 0xff) >> 4]).append((char) HEX_ALPHABET[item & 0xf]);
        }
        return result.toString();
    }

    public static Properties map2Properties(Map map) {
        final Properties properties = new Properties();
        for (final Object item : map.keySet()) {
            properties.put(item, map.get(item));
        }
        return properties;
    }
}
