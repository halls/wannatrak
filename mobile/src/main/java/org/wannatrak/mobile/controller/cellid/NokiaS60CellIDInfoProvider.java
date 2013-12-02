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

package org.wannatrak.mobile.controller.cellid;

import org.wannatrak.mobile.model.Cell;
import org.wannatrak.mobile.controller.ImsiParser;

/**
 * Created 14.10.2009 22:34:47
 *
 * @author Andrey Khalzov
 */
public class NokiaS60CellIDInfoProvider implements CellIDInfoProvider {

    public boolean doesWorkForPlatform() {
        return getCellID() != null;
    }

    public Cell getCell() {
        return new Cell(getCellID(), getMobileCountryCode(), getMobileNetworkCode(), getLocationAreaCode());
    }

    public String getCellID() {
        return System.getProperty("com.nokia.mid.cellid");
    }

    public String getMobileCountryCode() {
        //com.nokia.mid.countrycode
        return ImsiParser.getMobileCountryCode(getImsi());
    }

    public String getMobileNetworkCode() {
        final String networkid = System.getProperty("com.nokia.mid.networkid");
        if (networkid == null) {
            return null;
        }
        int endIndex = networkid.indexOf(' ');
        if (endIndex < 0) {
            endIndex = networkid.indexOf('(');
        }
        if (endIndex > 0) {
            return endIndex > 0 ? networkid.substring(0, endIndex) : networkid;
        } else {
            return networkid;
        }
    }

    public String getLocationAreaCode() {
        return System.getProperty("com.nokia.mid.lac");
    }

    private String getImsi() {
        String imsi = System.getProperty("com.nokia.mid.mobinfo.IMSI");
        if (imsi == null || imsi.length() == 0) {
            imsi = System.getProperty("com.nokia.mid.Mobinfo.IMSI");
        }
        if (imsi == null || imsi.length() == 0) {
            imsi = System.getProperty("com.nokia.mid.imsi");
        }
        return imsi;
    }
}
