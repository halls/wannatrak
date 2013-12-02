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

package org.wannatrak.mobile.model;

/**
 * Created 16.10.2009 1:07:35
 *
 * @author Andrey Khalzov
 */
public class Cell {
    public String cellID;
    public String mobileCountryCode;
    public String mobileNetworkCode;
    public String locationAreaCode;

    public Cell(String cellID, String mobileCountryCode, String mobileNetworkCode, String locationAreaCode) {
        this.cellID = cellID;
        this.mobileCountryCode = mobileCountryCode;
        this.mobileNetworkCode = mobileNetworkCode;
        this.locationAreaCode = locationAreaCode;
    }

    public String toString() {
        return cellID + " " + mobileCountryCode + " " + mobileNetworkCode + " " + locationAreaCode;
    }

    public boolean equals(Object o) {
        if (o instanceof Cell) {
            final Cell cell = (Cell) o;

            //true if one of values is null
            return cell.cellID == null
                    || cell.mobileCountryCode == null
                    || cell.mobileNetworkCode == null
                    || cell.locationAreaCode == null
                    || 
                    cell.cellID.equals(cellID)
                    && cell.mobileCountryCode.equals(mobileCountryCode)
                    && cell.mobileNetworkCode.equals(mobileNetworkCode)
                    && cell.locationAreaCode.equals(locationAreaCode);

        }

        return false;
    }
}
