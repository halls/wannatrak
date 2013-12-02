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
 * 11.07.2008 1:36:58
 */
package org.wannatrak.middleware.entity;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = Zone.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Zone extends UserProperty {
    public static final String TABLE_NAME = "wt_zone";

    @ManyToMany(targetEntity = TicketOwner.class)
    @JoinTable(name = TicketOwner.ZONE_TICKET_TABLE_NAME,
               joinColumns = {@JoinColumn(name = TicketOwner.ZONE)},
               inverseJoinColumns = {@JoinColumn(name = TicketOwner.TICKET_OWNER)},
               uniqueConstraints = {
                   @UniqueConstraint(columnNames = {TicketOwner.ZONE, TicketOwner.TICKET_OWNER}),
                   @UniqueConstraint(columnNames = {TicketOwner.TICKET_OWNER, TicketOwner.ZONE})
               }
    )
    protected Set<TicketOwner> ticketOwners;

    public abstract boolean isIn(@NotNull Subject subject);

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
}
