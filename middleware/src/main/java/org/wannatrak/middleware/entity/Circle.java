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
 * 11.07.2008 1:40:08
 */
package org.wannatrak.middleware.entity;

import org.wannatrak.middleware.util.GeoHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = Circle.TABLE_NAME)
public class Circle extends Zone {
    public static final String TABLE_NAME = "wt_circle";

    public static final String NAME = "Name";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String RADIUS = "Radius";

    @Basic
    @Column(name = NAME)
    private String name;

    @Basic
    @Column(name = LONGITUDE, nullable = false)
    private Double longitude;

    @Basic
    @Column(name = LATITUDE, nullable = false)
    private Double latitude;

    @Basic
    @Column(name = RADIUS, nullable = false)
    private Double radius;


    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@NotNull Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@NotNull Double latitude) {
        this.latitude = latitude;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(@NotNull Double radius) {
        this.radius = radius;
    }

    public boolean isIn(@NotNull Subject subject) {
        final Position position = subject.getPosition();
        return position != null
                && GeoHelper.getDistance(
                    longitude,
                    latitude,
                    position.getLongitude(),
                    position.getLatitude()
                ) <= radius;
    }
}
