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
 * Created 14.10.2009 22:31:01
 *
 * @author Andrey Khalzov
 */
public class NokiaS40CellIDInfoProvider implements CellIDInfoProvider {

    public boolean doesWorkForPlatform() {
        return getCellID() != null;
    }

    public Cell getCell() {
        return new Cell(getCellID(), getMobileCountryCode(), getMobileNetworkCode(), getLocationAreaCode());
    }

    public String getCellID() {
        return System.getProperty("Cell-ID");
    }

    public String getMobileCountryCode() {
        return System.getProperty("com.nokia.mid.mcc");
    }

    public String getMobileNetworkCode() {
        return System.getProperty("com.nokia.mid.mnc");
    }

    public String getLocationAreaCode() {
        return System.getProperty("com.nokia.mid.lac");
    }
}
