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
package org.wannatrak.middleware.ejb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.exception.SubjectAlreadyExistsException;
import org.wannatrak.middleware.exception.EntityNotFoundException;

import java.util.List;
import java.util.ArrayList;

/**
 * Created 13.09.2009 20:31:08
 *
 * @author Andrey Khalzov
 */
public class MockSubjectWorker implements SubjectWorker {

    public static final String DEVICE_CODE = "DEVICE_CODE";

    @Override
    @NotNull
    public String loginDeviceAsLastUsedOrCreateNew(@NotNull User user) {
        return DEVICE_CODE;
    }

    @Override
    public Subject createNewOrLinkToExisting(@NotNull User user, @NotNull String name, @NotNull String deviceKey) {
        return new Subject();
    }

    @Override
    @Nullable
    public Subject getSubject(@NotNull String deviceKey) {
        return new Subject();
    }

    @Override
    public Subject assignToSubject(@NotNull String newDeviceId, @NotNull Long linkDeviceId) throws EntityNotFoundException {
        return new Subject();
    }

    @Override
    public void unlinkSubject(@NotNull String deviceKey) throws EntityNotFoundException {
        if (!DEVICE_CODE.equals(deviceKey)) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    @NotNull
    public List<Subject> getSubjects() {
        return null;
    }

    @Override
    @NotNull
    public List<Subject> getSubjects(@NotNull User user) {
        return new ArrayList<Subject>();
    }

    @Override
    @Nullable
    public Subject getSubject(@NotNull User user, @NotNull Long subjectId) {
        return null;
    }

    @Override
    public void removeSubject(@NotNull User user, @NotNull Long subjectId) {
    }

    @Override
    public void modifySubject(@NotNull User user, @NotNull Long subjectId, @NotNull String name, int sendPeriod, int savePeriod, Long smsRecipient) throws SubjectAlreadyExistsException {
    }
}
