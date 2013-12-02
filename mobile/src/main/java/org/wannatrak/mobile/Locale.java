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

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Created 27.06.2009 0:32:05
 *
 * @author Andrey Khalzov
 */
public class Locale {

    private static String locale = null;

    public static String get() {
        if (locale == null) {
            locale = readLocale();
        }
        return locale;
    }

    public static String getCurrent() {
        return "en".equals(get()) ? "" : "_" + get();
    }

    private static String readLocale() {
        String locale = "";
        Reader reader = null;
        try {
            final StringBuffer sb = new StringBuffer();
            reader = new InputStreamReader(org.wannatrak.mobile.Locale.class.getResourceAsStream("/locale"), "utf-8");

            char[] buffer = new char[8];
            int read = reader.read(buffer);
            while (read > 0) {
                sb.append(buffer, 0, read);
                read = reader.read(buffer);
            }
            locale = sb.toString();

        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return locale;
    }
}
