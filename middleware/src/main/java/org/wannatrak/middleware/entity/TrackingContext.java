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
 * 23.11.2008 9:24:14
 */
package org.wannatrak.middleware.entity;

import org.jetbrains.annotations.Nullable;

import javax.persistence.*;

@Entity
@Table(name = TrackingContext.TABLE_NAME)
public class TrackingContext extends UndeletableEntity {
    public static final String TABLE_NAME = "wt_tracking_context";

    @ManyToOne
    @JoinColumn
    private Position lastValidPosition;

    @ManyToOne
    @JoinColumn
    private Position centerPosition;

    @ManyToOne
    @JoinColumn
    private Position prevCenterPosition;

    @Nullable
    public Position getLastValidPosition() {
        return lastValidPosition;
    }

    public void setLastValidPosition(Position lastValidPosition) {
        this.lastValidPosition = lastValidPosition;
    }

    @Nullable
    public Position getCenterPosition() {
        return centerPosition;
    }

    public void setCenterPosition(Position centerPosition) {
        this.centerPosition = centerPosition;
    }

    @Nullable
    public Position getPrevCenterPosition() {
        return prevCenterPosition;
    }

    public void setPrevCenterPosition(Position prevCenterPosition) {
        this.prevCenterPosition = prevCenterPosition;
    }
}
