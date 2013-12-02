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
 * 11.07.2008 2:31:16
 */
package org.wannatrak.middleware.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = TicketOwner.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class TicketOwner extends UndeletableEntity {
    public static final String TABLE_NAME = "wt_ticket_owner";

    public static final String ZONE_TICKET_TABLE_NAME = "wt_zone_ticket";
    public static final String SUBJECT_TICKET_TABLE_NAME = "wt_subject_ticket";

    public static final String TICKET_OWNER = "ticket_owner_id";
    public static final String ZONE = "zone_id";
    public static final String SUBJECT = "subject_id";

    public static final String NAME = "name";

    @Basic
    @Column(name = NAME)
    protected String name;

    @ManyToMany(targetEntity = Zone.class)
    @JoinTable(name = ZONE_TICKET_TABLE_NAME,
               joinColumns = {@JoinColumn(name = TICKET_OWNER)},
               inverseJoinColumns = {@JoinColumn(name = ZONE)},
               uniqueConstraints = {
                   @UniqueConstraint(columnNames = {TICKET_OWNER, ZONE}),
                   @UniqueConstraint(columnNames = {ZONE, TICKET_OWNER})
               }
    )
    protected Set<Zone> zones;

    @ManyToMany(targetEntity = Subject.class)
    @JoinTable(name = SUBJECT_TICKET_TABLE_NAME,
               joinColumns = {@JoinColumn(name = TICKET_OWNER)},
               inverseJoinColumns = {@JoinColumn(name = SUBJECT)},
               uniqueConstraints = {
                   @UniqueConstraint(columnNames = {TICKET_OWNER, SUBJECT}),
                   @UniqueConstraint(columnNames = {SUBJECT, TICKET_OWNER})
               }
    )
    protected Set<Subject> subjects;

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    public Set<Zone> getZones() {
        if (zones == null) {
            return Collections.emptySet();
        }
        return zones;
    }

    public void setZones(Set<Zone> zones) {
        this.zones = zones;
    }

    @NotNull
    public Set<Subject> getSubjects() {
        if (subjects == null) {
            return Collections.emptySet();
        }
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }
}
