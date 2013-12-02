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
 * 11.07.2008 0:36:44
 */
package org.wannatrak.middleware.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class UndeletableEntity implements Serializable {
    public static final String ID = "Id";
    public static final String DELETED = "Deleted";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID)
    protected Long id;

    @Basic
    @Column(name = DELETED)
    protected Boolean deleted = false;

    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @Nullable
    public Boolean isDeleted() {
        return this.deleted;
    }

    public void setDeleted(Boolean value) {
        this.deleted = value;
    }
}
