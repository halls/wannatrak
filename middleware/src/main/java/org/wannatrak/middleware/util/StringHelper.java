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

/**
 * Created by Andrey Khalzov
 * 12.07.2008 14:42:11
 */
package org.wannatrak.middleware.util;

import java.io.Reader;
import java.io.IOException;

public class StringHelper {
    public static boolean isAlmostEmpty(String value) {
        if (isEmpty(value)) {
            return true;
        }

        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static String handleNull(String value) {
        return value == null ? "" : value;
    }

    public static String readToString(Reader reader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        int read = reader.read(buffer);
        while (read > 0) {
            sb.append(buffer, 0, read);
            read = reader.read(buffer);
        }
        return sb.toString();
    }
}
