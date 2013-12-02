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
 * 11.07.2008 3:07:58
 */
package org.wannatrak.middleware.entity;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = Group.TABLE_NAME)
public class Group extends TicketOwner {
    public static final String TABLE_NAME = "wt_group";

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = User.USER_GROUP_TABLE_NAME,
               joinColumns = {@JoinColumn(name = User.GROUP)},
               inverseJoinColumns = {@JoinColumn(name = User.USER)},
               uniqueConstraints = {
                   @UniqueConstraint(columnNames = {User.GROUP, User.USER}),
                   @UniqueConstraint(columnNames = {User.USER, User.GROUP})
               }
    )
    private Set<User> users;

    @NotNull
    public Set<User> getUsers() {
        if (users == null) {
            Collections.emptySet();
        }
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
