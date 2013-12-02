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
 * 10.07.2008 23:30:25
 */
package org.wannatrak.middleware.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = Subject.TABLE_NAME)
public class Subject extends UserProperty {
    public static final String TABLE_NAME = "wt_subject";

    public static final String NAME = "Name";
    public static final String DEVICE_ID = "DeviceName";
    public static final String DEVICE_SETTINGS = "DeviceSettingsId";
    public static final String POSITION = "PositionId";

    @Basic
    @Column(name = NAME)
    private String name;

    @Basic
    @Column(name = DEVICE_ID, unique = true)
    private String deviceId;

    @ManyToOne
    @JoinColumn(name = Subject.DEVICE_SETTINGS)
    private DeviceSettings deviceSettings;

    @OneToOne
    @JoinColumn(name = POSITION)
    private Position position;

    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    @Column
    private DateTime lastUpdated;

    @Basic
    @Column
    private Long odometer;

    @Basic
    @Column
    private Boolean hidden;    

    @ManyToMany(targetEntity = TicketOwner.class)
    @JoinTable(name = TicketOwner.SUBJECT_TICKET_TABLE_NAME,
               joinColumns = {@JoinColumn(name = TicketOwner.SUBJECT)},
               inverseJoinColumns = {@JoinColumn(name = TicketOwner.TICKET_OWNER)},
               uniqueConstraints = {
                   @UniqueConstraint(columnNames = {TicketOwner.TICKET_OWNER, TicketOwner.SUBJECT}),
                   @UniqueConstraint(columnNames = {TicketOwner.SUBJECT, TicketOwner.TICKET_OWNER})
               }
    )
    private Set<TicketOwner> ticketOwners;

    @OneToOne
    @JoinColumn
    private TrackingContext trackingContext;

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Nullable
    public DeviceSettings getDeviceSettings() {
        return deviceSettings;
    }

    public void setDeviceSettings(DeviceSettings deviceSettings) {
        this.deviceSettings = deviceSettings;
    }

    @Nullable
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @NotNull
    public Set<TicketOwner> getTicketOwners() {
        if (ticketOwners == null) {
            return Collections.emptySet();
        }
        return ticketOwners;
    }

    public void setTicketOwners(Set<TicketOwner> ticketOwners) {
        this.ticketOwners = ticketOwners;
    }

    @Nullable
    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Nullable
    public Long getOdometer() {
        return odometer;
    }

    public void setOdometer(Long odometer) {
        this.odometer = odometer;
    }

    @Nullable
    public TrackingContext getTrackingContext() {
        return trackingContext;
    }

    public void setTrackingContext(TrackingContext trackingContext) {
        this.trackingContext = trackingContext;
    }

    @Nullable
    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
