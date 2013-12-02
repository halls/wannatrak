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

package org.wannatrak.middleware.localization;

import org.wannatrak.middleware.util.UserSession;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created 16.04.2009 21:47:46
 *
 * @author Sergey Grachev
 */
public class Messages {
    private static final String RESOURCE_NAME = "META-INF/localization/messages";
    private static final Messages instance = new Messages();

    private Messages() {
    }

    public static Messages getInstance() {
        return instance;
    }

    private static synchronized ResourceBundle getBundle() {
        Locale locale = UserSession.getLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return ResourceBundle.getBundle(RESOURCE_NAME, locale);
    }

    public static synchronized String get(String message, Object... params) {
        return String.format(get(message), params);
    }

    public static synchronized String get(String message) {
        return Messages.getBundle().getString(message);
    }

    public static synchronized String getForClass(Class clazz, String message) {
        return Messages.getBundle().getString(new StringBuilder(clazz.getName()).append(".").append(message).toString());
    }

    public static synchronized String[] getList(String message) {
        return Messages.getBundle().getStringArray(message);
    }
}
