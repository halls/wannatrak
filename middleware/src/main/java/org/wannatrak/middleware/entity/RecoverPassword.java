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

package org.wannatrak.middleware.entity;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created 16.04.2009 19:28:49
 *
 * @author Sergey Grachev
 */
@Entity
@Table(name = RecoverPassword.TABLE_NAME)
public class RecoverPassword extends SimpleEntity {
    public static final String TABLE_NAME = "wt_recover_password";

    @Column(nullable = false, length = 32)
    private String linkHash;

    @OneToOne
    @JoinColumn(nullable = false)
    private User user;

    @NotNull
    public String getLinkHash() {
        return linkHash;
    }

    public void setLinkHash(@NotNull String linkHash) {
        this.linkHash = linkHash;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }
}
