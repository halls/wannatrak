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
 * 27.08.2008 23:26:50
 */
package org.wannatrak.mobile.model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Position {

    public static final int SIZE = 42;
    
    private double longitude;
    private double latitude;
    private double speed;
    private short course;
    private double altitude;
    private long timestamp;
    private int range = 50;

    public Position(DataInputStream dis) throws IOException {
        longitude = dis.readDouble();
        latitude = dis.readDouble();
        speed = dis.readDouble();
        course = dis.readShort();
        altitude = dis.readDouble();
        timestamp = dis.readLong();
//        range = dis.readInt();
    }

    public Position(
            double longitude,
            double latitude,
            double speed,
            short course,
            double altitude, 
            long timestamp
    ) {
        this(longitude, latitude, speed, course, altitude, timestamp, 50);
    }

    public Position(
            double longitude,
            double latitude,
            double speed,
            short course,
            double altitude,
            long timestamp,
            int range
    ) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.course = course;
        this.altitude = altitude;
        this.timestamp = timestamp;
        this.range = range;
    }

    public synchronized void serialize(DataOutputStream dos) throws IOException {
        dos.writeDouble(longitude);
        dos.writeDouble(latitude);
        dos.writeDouble(speed);
        dos.writeShort(course);
        dos.writeDouble(altitude);
        dos.writeLong(timestamp);
//        dos.writeInt(range);
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getSpeed() {
        return speed;
    }

    public short getCourse() {
        return course;
    }

    public double getAltitude() {
        return altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getRange() {
        return range;
    }
}
