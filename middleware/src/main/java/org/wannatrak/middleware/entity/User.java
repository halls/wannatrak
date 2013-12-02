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
 * 11.07.2008 2:23:09
 */
package org.wannatrak.middleware.entity;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Set;
import java.util.Collections;

@Entity
@Table(name = User.TABLE_NAME)
public class User extends TicketOwner {
    public static final String TABLE_NAME = "wt_user";

    public static final String USER_GROUP_TABLE_NAME = "wt_user_group";

    public static final String LOGIN = "Login";
    public static final String DISPLAY_LOGIN = "DisplayLogin";
    public static final String PASSWORD = "Password";
    public static final String EMAIL = "Email";
    public static final String HOST_NAME = "HostName";

    public static final String USER = "user_Id";
    public static final String GROUP = "group_Id";

    @Basic
    @Column(name = LOGIN, nullable = false)
    private String login;

    @Basic
    @Column(name = DISPLAY_LOGIN)
    private String displayLogin;

    @Basic
    @Column(name = PASSWORD, precision = 40, nullable = false)
    private BigInteger password;

    @Basic
    @Column(name = HOST_NAME)
    private String hostName;

    @Basic
    @Column(name = EMAIL)
    private String email;

    @ManyToMany(targetEntity = Group.class)
    @JoinTable(name = USER_GROUP_TABLE_NAME,
               joinColumns = {@JoinColumn(name = USER)},
               inverseJoinColumns = {@JoinColumn(name = GROUP)},
               uniqueConstraints = {
                   @UniqueConstraint(columnNames = {GROUP, USER}),
                   @UniqueConstraint(columnNames = {USER, GROUP})
               }
    )
    private Set<Group> groups;


    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    public String getDisplayLogin() {
        return displayLogin;
    }

    public void setDisplayLogin(@NotNull String displayLogin) {
        this.displayLogin = displayLogin;
    }

    public BigInteger getPassword() {
        return password;
    }

    public void setPassword(@NotNull BigInteger password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(@NotNull String hostName) {
        this.hostName = hostName;
    }

    @NotNull
    public Set<Group> getGroups() {
        if (groups == null) {
            return Collections.emptySet();
        }
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
}
