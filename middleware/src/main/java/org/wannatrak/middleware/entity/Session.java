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
 * 29.07.2008 22:50:37
 */
package org.wannatrak.middleware.entity;

import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.wannatrak.middleware.util.BooleanHelper;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = Session.TABLE_NAME)
public class Session implements Serializable {
    public static final String TABLE_NAME = "wt_session";

    public static final String ID = "Id";
    public static final String LAST_ACCESS = "LastAccess";
    public static final String USER = "UserId";

    @Id
    @Column(name = Session.ID)
    private String id;

    @Basic
    private Boolean keepInCookie;

    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    @Column(name = LAST_ACCESS, nullable = false)
    private DateTime lastAccess;

    @ManyToOne
    @JoinColumn(name = USER)
    private User user;

    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    @NotNull
    public Boolean isKeepInCookie() {
        return BooleanHelper.valueOf(keepInCookie);
    }

    public void setKeepInCookie(Boolean keepInCookie) {
        this.keepInCookie = keepInCookie;
    }

    public DateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(@NotNull DateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
