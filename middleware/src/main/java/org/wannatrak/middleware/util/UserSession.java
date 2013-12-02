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

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Created 14.06.2009 19:07:02
 *
 * @author Andrey Khalzov
 */
public class UserSession {
    private static final ThreadLocal<Locale> locale = new ThreadLocal<Locale>();

    @Nullable
    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale locale) {
        UserSession.locale.set(locale);
    }

    public static void reset() {
        locale.set(null);
    }
}
