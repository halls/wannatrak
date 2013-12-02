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

/**
 * Created 14.10.2009 22:16:59
 *
 * @author Andrey Khalzov
 */
public class SonyEricssonCellIDInfoProvider implements CellIDInfoProvider {

    public boolean doesWorkForPlatform() {
        return getCellIDInHexFormat() != null;
    }

    public Cell getCell() {
        return new Cell(getCellID(), getMobileCountryCode(), getMobileNetworkCode(), getLocationAreaCode());
    }

    public String getCellID() {
        return String.valueOf(Integer.parseInt(getCellIDInHexFormat(), 16));
    }

    public String getMobileCountryCode() {
        return System.getProperty("com.sonyericsson.net.cmcc");
    }

    public String getMobileNetworkCode() {
        return System.getProperty("com.sonyericsson.net.cmnc");
    }

    public String getLocationAreaCode() {
        return String.valueOf(Integer.parseInt(System.getProperty("com.sonyericsson.net.lac"), 16));
    }

    private String getCellIDInHexFormat() {
        return System.getProperty("com.sonyericsson.net.cellid");
    }
}
