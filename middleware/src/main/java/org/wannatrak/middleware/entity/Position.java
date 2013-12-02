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
 * 11.07.2008 0:03:05
 */
package org.wannatrak.middleware.entity;

import org.hibernate.annotations.Index;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = Position.TABLE_NAME)
@org.hibernate.annotations.Table(
        appliesTo = Position.TABLE_NAME,
        indexes = {
            @Index(
                    name = Position.IDX_SUBJECT_ID_TIMESTAMP_NAME,
                    columnNames = {
                            Position.SUBJECT,
                            Position.GPS_TIMESTAMP
                    }
            ),
            @Index(
                    name = Position.IDX_SUBJECT_ID_VALID_TIMESTAMP_NAME,
                    columnNames = {
                            Position.SUBJECT,
                            Position.VALID,
                            Position.GPS_TIMESTAMP
                    }
            )
        }
)
public class Position extends AbstractPosition {
    public static final String TABLE_NAME = "wt_position";

    public static final String IDX_SUBJECT_ID_TIMESTAMP_NAME = "idx_subject_id_timestamp";
    public static final String IDX_SUBJECT_ID_VALID_TIMESTAMP_NAME = "idx_subject_id_valid_timestamp";
}
