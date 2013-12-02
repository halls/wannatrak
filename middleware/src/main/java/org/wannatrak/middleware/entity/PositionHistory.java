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
 * 11.07.2008 0:29:27
 */
package org.wannatrak.middleware.entity;

import org.hibernate.annotations.Index;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = PositionHistory.TABLE_NAME)
@org.hibernate.annotations.Table(
        appliesTo = PositionHistory.TABLE_NAME,
        indexes = {
            @Index(
                    name = PositionHistory.IDX_SUBJECT_ID_GPS_TIMESTAMP_NAME,
                    columnNames = {
                            PositionHistory.SUBJECT,
                            PositionHistory.GPS_TIMESTAMP
                    }
            )
        }
)
public class PositionHistory extends AbstractPosition {
    public static final String TABLE_NAME = "wt_position_history";

    public static final String IDX_SUBJECT_ID_GPS_TIMESTAMP_NAME = "idx_subject_id_gps_timestamp";
}
