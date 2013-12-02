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

package org.wannatrak.mobile.controller;

/**
 * Created 17.10.2009 0:18:52
 *
 * @author Andrey Khalzov
 */
public class ImsiParser {

    public static String getMobileCountryCode(String imsi) {
        if (imsi == null) {
            return null;
        }

        if (imsi.length() > 3) {
            return imsi.substring(0, 3);
        }
        return imsi;
    }

    public static String getMobileNetworkCode(String imsi) {
        if (imsi == null) {
            return null;
        }
        final String mccS = getMobileCountryCode(imsi);
        try {
            int mcc = Integer.parseInt(mccS);

            // US
            if (mcc >= 310 && mcc <= 316) {
                return imsi.substring(3, 6);
            // Other countries
            } else {
                return imsi.substring(3, 5);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
