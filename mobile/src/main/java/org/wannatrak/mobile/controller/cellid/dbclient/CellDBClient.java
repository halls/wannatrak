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

package org.wannatrak.mobile.controller.cellid.dbclient;

import org.wannatrak.mobile.model.Position;
import org.wannatrak.mobile.model.Cell;

/**
 * Created 14.10.2009 22:56:51
 *
 * @author Andrey Khalzov
 */
public interface CellDBClient {

    Position getPosition(String cellID, String mcc, String mnc, String lac);

    void postCellPosition(String cellID, String mcc, String mnc, String lac, double latitude, double longitude, long ts);
}
