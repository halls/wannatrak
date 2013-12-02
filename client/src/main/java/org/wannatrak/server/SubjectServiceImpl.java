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

package org.wannatrak.server;

import org.wannatrak.client.subject.*;
import org.wannatrak.client.exception.NotLoggedInException;
import org.wannatrak.client.exception.SubjectInfoNoAccessException;
import org.wannatrak.client.exception.SubjectAlreadyExistsException;
import org.wannatrak.middleware.ejb.*;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.entity.Position;
import org.wannatrak.middleware.entity.DeviceSettings;
import org.wannatrak.middleware.util.ServiceLocator;
import org.wannatrak.middleware.util.StringHelper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Andrey Khalzov
 * 14.07.2008 21:53:54
 */
public class SubjectServiceImpl extends RemoteServiceServlet implements SubjectService {

    public PositionData getLastPosition(Long subjectId, Boolean valid) {
        final HttpSession session = getThreadLocalRequest().getSession();
        final SessionWorker sessionWorker = ServiceLocator.lookup(SessionWorker.JNDI_NAME);
        final PositionWorker positionWorker = ServiceLocator.lookup(PositionWorker.JNDI_NAME);

        final User user = sessionWorker.getUser(session.getId());

        final Position position = positionWorker.getLastPosition(user, subjectId, valid);
        if (position == null) {
            return null;
        } else {
            return new PositionData(position.getLatitude(), position.getLongitude());
        }
    }

    public SubjectData[] getSubjects() {
        final HttpSession session = getThreadLocalRequest().getSession();
        final SubjectWorker subjectWorker = ServiceLocator.lookup(SubjectWorker.JNDI_NAME);
        final SessionWorker sessionWorker = ServiceLocator.lookup(SessionWorker.JNDI_NAME);

        final User user = sessionWorker.getUser(session.getId());

        final List<Subject> subjects;
        if (user == null) {
            subjects = subjectWorker.getSubjects();
        } else {
            subjects = subjectWorker.getSubjects(user);
        }
        return parseSubjects(subjects);
    }

    public SubjectInfoData getSubjectInfo(Long subjectId) throws NotLoggedInException, SubjectInfoNoAccessException {
        final HttpSession session = getThreadLocalRequest().getSession();
        final SubjectWorker subjectWorker = ServiceLocator.lookup(SubjectWorker.JNDI_NAME);
        final SessionWorker sessionWorker = ServiceLocator.lookup(SessionWorker.JNDI_NAME);

        final User user = sessionWorker.getUser(session.getId());
        if (user == null) {
            throw new NotLoggedInException();
        }
        final Subject subject = subjectWorker.getSubject(user, subjectId);
        if (subject == null) {
            throw new SubjectInfoNoAccessException();
        }
        final DeviceSettings deviceSettings = subject.getDeviceSettings();
        final Integer savePeriod = deviceSettings == null || deviceSettings.getSavePeriod() == null
                ? 5 : deviceSettings.getSavePeriod();
        final Integer sendPeriod = deviceSettings == null || deviceSettings.getSendPeriod() == null
                ? 5 : deviceSettings.getSendPeriod();

        final Position lastPosition = subject.getPosition();
        final Double speed = lastPosition == null ? null : lastPosition.getSpeed();
        final Double altitude = lastPosition == null ? null : lastPosition.getAltitude();

        return new SubjectInfoData(
                subjectId,
                subject.getName(),
                speed,
                altitude,
                savePeriod,
                sendPeriod,
                getSubjectState(subject)
        );
    }

    public void saveSettings(Long subjectId, SubjectSettingsData subjectSettingsData)
            throws SubjectAlreadyExistsException, NotLoggedInException
    {
        final HttpSession session = getThreadLocalRequest().getSession();
        final SubjectWorker subjectWorker = ServiceLocator.lookup(SubjectWorker.JNDI_NAME);
        final SessionWorker sessionWorker = ServiceLocator.lookup(SessionWorker.JNDI_NAME);

        final User user = sessionWorker.getUser(session.getId());
        if (user == null) {
            throw new NotLoggedInException();
        }

        try {
            subjectWorker.modifySubject(
                    user,
                    subjectId,
                    subjectSettingsData.getName(),
                    subjectSettingsData.getSendPeriod(),
                    subjectSettingsData.getSavePeriod(),
                    null
            );
        } catch (org.wannatrak.middleware.exception.SubjectAlreadyExistsException e) {
            throw new SubjectAlreadyExistsException(subjectSettingsData.getName());
        }
    }

    public void removeSubject(Long showInfoSubjectId) throws NotLoggedInException {
        final HttpSession session = getThreadLocalRequest().getSession();
        final SubjectWorker subjectWorker = ServiceLocator.lookup(SubjectWorker.JNDI_NAME);
        final SessionWorker sessionWorker = ServiceLocator.lookup(SessionWorker.JNDI_NAME);

        final User user = sessionWorker.getUser(session.getId());
        if (user == null) {
            throw new NotLoggedInException();
        }

        subjectWorker.removeSubject(user, showInfoSubjectId);
    }

    private SubjectData[] parseSubjects(List<Subject> subjects) {
        final SubjectData[] data = new SubjectData[subjects.size()];
        int i = 0;
        for (Subject subject : subjects) {
            final Long odometer = subject.getOdometer();
            final DateTime lastUpdated = subject.getLastUpdated();

            final SubjectData subjectData = new SubjectData(
                    subject.getName(),
                    subject.getId(),
                    lastUpdated == null ? null : lastUpdated.toDate(),
                    odometer == null ? "0" : odometer.toString(),
                    getSubjectState(subject)
            );
            data[i++] = subjectData;
        }
        return data;
    }

    private SubjectState getSubjectState(Subject subject) {
        if (StringHelper.isAlmostEmpty(subject.getDeviceId())) {
            return SubjectState.SWITCHED_OFF;
        }
        final DeviceSettings deviceSettings = subject.getDeviceSettings();
        if (deviceSettings == null) {
            return SubjectState.SWITCHED_OFF;
        }
        final Integer sendPeriod = deviceSettings.getSendPeriod();
        if (sendPeriod == null) {
            return SubjectState.SWITCHED_OFF;
        }
        final DateTime lastUpdated = subject.getLastUpdated();
        if (lastUpdated == null
                || new DateTime().minus(lastUpdated.getMillis()).getMillis()
                    >= sendPeriod * DateTimeConstants.MILLIS_PER_MINUTE * 2
        ) {
            return SubjectState.CONNECTION_FAILED;
        }

        final Position position = subject.getPosition();
        if (position == null) {
            return SubjectState.LOCATION_NOT_DEFINED;
        }

        return SubjectState.TRACKING;
    }
}