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
 * 15.07.2008 0:02:39
 */
package org.wannatrak.middleware.ejb;

import org.wannatrak.middleware.entity.DeviceSettings;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.wannatrak.middleware.exception.SubjectAlreadyExistsException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.UUID;

@Stateless(name = SubjectWorker.JNDI_NAME)
public class SubjectWorkerBean extends AbstractWorkerBean implements SubjectWorkerLocal, SubjectWorkerRemote {

    @NotNull
    public List<Subject> getSubjects() {
        return dao.findOrdered(
                Subject.class,
                0,
                5,
                "e.lastUpdated is not null and e.position is not null and (e.hidden is null or e.hidden = false)",
                "e.lastUpdated desc"
        );
    }

    @NotNull
    public List<Subject> getSubjects(@NotNull User user) {
        return dao.findOrdered(Subject.class, "e.user = ? and e.name is not null", "e.id", user);
    }

    @NotNull
    public List<Subject> getUnlinkedSubjects(@NotNull User user) {
        return dao.find(Subject.class, "e.user = ? and e.deviceId is null", user);
    }

    @Nullable
    public Subject getSubject(@NotNull User user, @NotNull Long subjectId) {
        try {
            final Subject subject = dao.find(Subject.class, subjectId);
            return subject.getUser().getId().equals(user.getId()) ? subject : null;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public Subject getSubject(@NotNull String deviceKey) {
        try {
            return dao.findSingleResult(Subject.class, "e.deviceId = ?", deviceKey);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public void removeSubject(@NotNull User user, @NotNull Long subjectId) {
        try {
            final Subject subject = dao.find(Subject.class, subjectId);
            if (subject.getUser().getId().equals(user.getId())) {
                dao.remove(subject);
            }
        } catch (EntityNotFoundException e) {
        }
    }

    public void modifySubject(
            @NotNull User user,
            @NotNull Long subjectId,
            @NotNull String name,
            int sendPeriod,
            int savePeriod,
            Long smsRecipient
    ) throws SubjectAlreadyExistsException {
        System.out.println(String.format("Params: %d; %s; %d", user.getId(), name, subjectId));
        if (!dao.find(Subject.class, "e.user = ? and e.name = ? and e.id != ?", user, name, subjectId).isEmpty()) {
            throw new SubjectAlreadyExistsException();
        }

        final Subject subject = getSubject(user, subjectId);
        if (subject != null) {
            DeviceSettings deviceSettings = subject.getDeviceSettings();
            if (deviceSettings != null) {
                deviceSettings.setSendPeriod(sendPeriod);
                deviceSettings.setSavePeriod(savePeriod);
                deviceSettings.setSmsRecipient(smsRecipient);
                deviceSettings = dao.merge(deviceSettings);
            }
            subject.setName(name);
            subject.setDeviceSettings(deviceSettings);
            dao.merge(subject);
        }
    }

    public long createSubject(
            @NotNull User user,
            @NotNull String name,
            int sendPeriod,
            int savePeriod,
            Long smsRecipient
    ) {
        final DeviceSettings deviceSettings = new DeviceSettings();
        deviceSettings.setSendPeriod(sendPeriod);
        deviceSettings.setSavePeriod(savePeriod);
        deviceSettings.setSmsRecipient(smsRecipient);
        dao.persist(deviceSettings);

        final Subject subject = new Subject();
        subject.setName(name);
        subject.setUser(user);
        subject.setDeviceSettings(deviceSettings);

        dao.persist(subject);
        return subject.getId();
    }

    public Subject assignName(@NotNull String deviceId, @NotNull String name) throws EntityNotFoundException {
        Subject subject = dao.findSingleResult(Subject.class, "e.deviceId = ?", deviceId);

        final List<Subject> subjectsWithSameName
                = dao.find(Subject.class, "e.user = ? and e.name = ?", subject.getUser(), name);
        if (!subjectsWithSameName.isEmpty()) {
            dao.remove(subject);

            subject = subjectsWithSameName.get(0);
            deviceId = generateDeviceId();
            subject.setDeviceId(deviceId);
            return dao.merge(subject);
        } else {
            final DeviceSettings deviceSettings = new DeviceSettings();
            dao.persist(deviceSettings);

            subject.setName(name);
            subject.setDeviceSettings(deviceSettings);
            return dao.merge(subject);
        }
    }

    @NotNull
    public String loginDevice(@NotNull User user) {
        final String deviceId = generateDeviceId();

        final Subject subject = new Subject();
        subject.setUser(user);
        subject.setDeviceId(deviceId);
        dao.persist(subject);
        return deviceId;
    }

    @NotNull
    public String linkSubject(@NotNull String deviceId, @NotNull Long linkDeviceId) throws EntityNotFoundException {
        final Subject oldSubject = dao.findSingleResult(Subject.class, "e.deviceId = ?", deviceId);
        dao.remove(oldSubject);

        final String newDeviceId = generateDeviceId();

        assignToSubject(newDeviceId, linkDeviceId);
        return newDeviceId;
    }

    public Subject assignToSubject(@NotNull String newDeviceId, @NotNull Long linkDeviceId) throws EntityNotFoundException {
        final Subject subject = dao.find(Subject.class, linkDeviceId);
        subject.setDeviceId(newDeviceId);
        return dao.merge(subject);
    }

    public void unlinkSubject(@NotNull String deviceKey) throws EntityNotFoundException {
        final Subject subject = dao.findSingleResult(Subject.class, "e.deviceId = ?", deviceKey);

        if (subject.getDeviceSettings() == null) {
            dao.remove(subject);
        } else {
            subject.setDeviceId(null);
            dao.merge(subject);
        }
    }

    @Override
    @NotNull
    public String loginDeviceAsLastUsedOrCreateNew(@NotNull User user) {
        String newDeviceId;
        try {
            Subject subject = (Subject) em.createQuery(
                    "select subject from Subject subject " +
                            "where subject.user = :user " +
                            "and (subject.deleted is null or subject.deleted = false) " +
                            "order by subject.lastUpdated desc"
            )
                    .setParameter("user", user)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getSingleResult();
            newDeviceId = generateDeviceId();
            subject.setDeviceId(newDeviceId);
            dao.merge(subject);
        } catch (NoResultException e) {
            Subject subject = createNewOrLinkToExisting(user, user.getLogin());
            newDeviceId = subject.getDeviceId();
        }
        return newDeviceId;
    }

    public Subject createNewOrLinkToExisting(@NotNull User user, @NotNull String name, @NotNull String deviceKey) {
        final List<Subject> subjectsWithSameName
            = dao.find(Subject.class, "e.user = ? and e.name = ?", user, name);
        String newDeviceId = generateDeviceId();
        Subject subject;
        if (!subjectsWithSameName.isEmpty()) {
            subject = subjectsWithSameName.get(0);
            if (!deviceKey.equals(subject.getDeviceId())) {
                subject.setDeviceId(newDeviceId);
                dao.merge(subject);
            }
        } else {
            final DeviceSettings deviceSettings = new DeviceSettings();
            dao.persist(deviceSettings);
            
            subject = new Subject();
            subject.setDeviceId(newDeviceId);
            subject.setName(name);
            subject.setDeviceSettings(deviceSettings);
            subject.setUser(user);
            dao.persist(subject);
        }
        return subject;
    }

    private Subject createNewOrLinkToExisting(@NotNull User user, @NotNull String name) {
        return createNewOrLinkToExisting(user, name, "");
    }

    private String generateDeviceId() {
        return UUID.randomUUID().toString();
    }
}
