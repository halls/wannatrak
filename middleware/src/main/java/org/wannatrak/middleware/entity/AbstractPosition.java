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
 * 10.07.2008 23:16:04
 */
package org.wannatrak.middleware.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class AbstractPosition implements Serializable {

    public static final String ID = "Id";
    public static final String SUBJECT = "SubjectId";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String ALTITUDE = "Altitude";
    public static final String SPEED = "Speed";
    public static final String COURSE = "Course";
    public static final String GPS_TIMESTAMP = "GpsTimestamp";
    public static final String ODOMETER = "Odometer";
    public static final String VALID = "Valid";
    public static final String ACTUAL_SPEED = "ActualSpeed";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID)
    protected Long id;

    @Basic
    @Column(name = LONGITUDE, nullable = false)
    protected Double longitude;

    @Basic
    @Column(name = LATITUDE, nullable = false)
    protected Double latitude;

    @Basic
    @Column(name = SPEED)
    protected Double speed;

    @Basic
    @Column(name = COURSE)
    protected Integer course;

    @Basic
    @Column(name = ALTITUDE)
    protected Double altitude;

    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    @Column(name = GPS_TIMESTAMP, nullable = false)
    protected DateTime gpsTimestamp;

    @ManyToOne
    @JoinColumn(name = SUBJECT, nullable = false)
    protected Subject subject;

    @Basic
    @Column(name = ODOMETER)
    protected Long odometer;

    @Basic
    @Column(name = VALID)
    protected Boolean valid;

    @Basic
    @Column(name = ACTUAL_SPEED)
    protected Double actualSpeed;


    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }
    
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@NotNull Double longitude) {
        if (Double.isNaN(longitude)) {
            System.out.println("longitude NaN");
            return;
        }
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@NotNull Double latitude) {
        if (Double.isNaN(latitude)) {
            System.out.println("latitude NaN");
            return;
        }
        this.latitude = latitude;
    }

    @Nullable
    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        if (speed != null && Double.isNaN(speed)) {
            System.out.println("speed NaN");
            return;
        }
        this.speed = speed;
    }

    @Nullable
    public Integer getCourse() {
        return course;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    @Nullable
    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        if (Double.isNaN(altitude)) {
            System.out.println("altitude NaN");
            return;
        }
        this.altitude = altitude;
    }

    public DateTime getGpsTimestamp() {
        return gpsTimestamp;
    }

    public void setGpsTimestamp(@NotNull DateTime gpsTimestamp) {
        this.gpsTimestamp = gpsTimestamp;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(@NotNull Subject subject) {
        this.subject = subject;
    }

    @Nullable
    public Long getOdometer() {
        return odometer;
    }

    public void setOdometer(Long odometer) {
        this.odometer = odometer;
    }

    @Nullable
    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Double getActualSpeed() {
        return actualSpeed;
    }

    public void setActualSpeed(Double actualSpeed) {
        this.actualSpeed = actualSpeed;
    }
}
