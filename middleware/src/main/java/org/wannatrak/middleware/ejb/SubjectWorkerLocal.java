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
 * 15.07.2008 0:00:04
 */
package org.wannatrak.middleware.ejb;

import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.wannatrak.middleware.exception.SubjectAlreadyExistsException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SubjectWorkerLocal extends SubjectWorker {

    @NotNull
    List<Subject> getSubjects();

    @NotNull
    List<Subject> getUnlinkedSubjects(@NotNull User user);

    @NotNull
    List<Subject> getSubjects(@NotNull User user);

    @Nullable
    Subject getSubject(@NotNull User user, @NotNull Long subjectId);

    @Nullable
    Subject getSubject(@NotNull String deviceKey);

    long createSubject(@NotNull User user, @NotNull String name, int sendPeriod, int savePeriod, Long smsRecipient);

    Subject assignName(@NotNull String deviceId, @NotNull String name) throws EntityNotFoundException;

    @NotNull
    String loginDevice(@NotNull User user);

    void modifySubject(
            @NotNull User user,
            @NotNull Long subjectId,
            @NotNull String name,
            int sendPeriod,
            int savePeriod,
            Long smsRecipient
    ) throws SubjectAlreadyExistsException;

    void removeSubject(@NotNull User user, @NotNull Long subjectId);

    @NotNull
    String linkSubject(@NotNull String deviceId, @NotNull Long linkDeviceId) throws EntityNotFoundException;

    void unlinkSubject(@NotNull String deviceKey) throws EntityNotFoundException;
}
