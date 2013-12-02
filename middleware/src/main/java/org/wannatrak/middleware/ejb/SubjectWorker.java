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
 * 08.01.2009 21:10:48
 */
package org.wannatrak.middleware.ejb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.exception.SubjectAlreadyExistsException;
import org.wannatrak.middleware.exception.EntityNotFoundException;

import java.util.List;

public interface SubjectWorker {

    final String JNDI_NAME = AbstractWorkerBean.JNDI_PREFIX + "SubjectWorker";

    @NotNull
    String loginDeviceAsLastUsedOrCreateNew(@NotNull User user);

    Subject createNewOrLinkToExisting(@NotNull User user, @NotNull String name, @NotNull String deviceKey);

    void unlinkSubject(@NotNull String deviceKey) throws EntityNotFoundException;

    @Nullable
    Subject getSubject(@NotNull String deviceId);

    @NotNull
    List<Subject> getSubjects();

    @NotNull
    List<Subject> getSubjects(@NotNull User user);

    @Nullable
    Subject getSubject(@NotNull User user, @NotNull Long subjectId);

    Subject assignToSubject(@NotNull String newDeviceId, @NotNull Long linkDeviceId) throws EntityNotFoundException;

    void removeSubject(@NotNull User user, @NotNull Long subjectId);

    void modifySubject(
            @NotNull User user,
            @NotNull Long subjectId,
            @NotNull String name,
            int sendPeriod,
            int savePeriod,
            Long smsRecipient
    ) throws SubjectAlreadyExistsException;
}
