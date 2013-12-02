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

package org.wannatrak.device.api;

import com.google.inject.Inject;

import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.wannatrak.middleware.ejb.PositionWorker;
import org.wannatrak.middleware.ejb.SubjectWorker;
import org.wannatrak.middleware.ejb.TrackingHandler;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.entity.Position;
import org.wannatrak.middleware.entity.DeviceSettings;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.wannatrak.middleware.exception.SubjectAlreadyExistsException;
import org.wannatrak.middleware.exception.SendPeriodNotElapsedException;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

/**
 * Created 13.09.2009 22:24:59
 *
 * @author Andrey Khalzov
 */
public class TrakApiImpl implements TrakApi {

    @Inject
    PositionWorker positionWorker;

    @Inject
    SubjectWorker subjectWorker;

    @Inject
    TrackingHandler trackingHandler;

    public Double[][] get(
            @PathParam("deviceKey") String deviceKey,
            @PathParam("trakId") Long id,
            @PathParam("includeNoise") Boolean includeNoise
    ) throws EntityNotFoundException {
        return get(deviceKey, id, 0L, System.currentTimeMillis(), includeNoise);
    }

    public Double[][] get(
            @PathParam("deviceKey") String deviceKey,
            @PathParam("trakId") Long id,
            @PathParam("from") Long from,
            @PathParam("to") Long to,
            @PathParam("includeNoise") Boolean includeNoise
    ) throws EntityNotFoundException {
        Subject subject = getSubject(deviceKey);
        DateTime dateTimeFrom = new DateTime(from);
        DateTime dateTimeTo = new DateTime(to);
        List<Position> positions = positionWorker.getPositions(
                subject.getUser(),
                id,
                dateTimeFrom,
                dateTimeTo,
                Boolean.TRUE.equals(includeNoise)
        );
        Logger.getLogger(getClass()).debug(subject.getId());
        Logger.getLogger(getClass()).debug(dateTimeFrom.toString());
        Logger.getLogger(getClass()).debug(dateTimeTo.toString());

        Double[][] result = new Double[positions.size()][];
        int i = 0;
        for (Position position : positions) {
            Double[] coords = new Double[6];
            coords[0] = (double) position.getGpsTimestamp().getMillis();
            coords[1] = position.getLongitude();
            coords[2] = position.getLatitude();
            coords[3] = position.getSpeed();

            Integer course = position.getCourse();
            coords[4] = course == null ? 0 : course.doubleValue();
            coords[5] = position.getAltitude();

            result[i++] = coords;
        }

        Logger.getLogger(getClass()).debug(result.length);
        return result;
    }

    @Override
    public void post(@PathParam("deviceKey") String deviceKey, Double[][] trak)
            throws EntityNotFoundException, SendPeriodNotElapsedException
    {
        Subject subject = getSubject(deviceKey);
        DeviceSettings deviceSettings = subject.getDeviceSettings();
        DateTime lastUpdated = subject.getLastUpdated();
        if (deviceSettings != null && lastUpdated != null
                && lastUpdated.plusSeconds(30 * deviceSettings.getSendPeriod()).isAfterNow()
        ) {
            throw new SendPeriodNotElapsedException();
        }

        Position[] positions = new Position[trak.length];
        int i = 0;
        for (Double[] point : trak) {
            Position position = new Position();
            position.setGpsTimestamp(new DateTime(point[0].longValue()));
            position.setLongitude(point[1]);
            position.setLatitude(point[2]);
            position.setSpeed(point[3]);
            position.setCourse(point[4] == null ? null : point[4].intValue());
            position.setAltitude(point[5]);
            positions[i++] = position;
        }

        trackingHandler.handlePositions(deviceKey, positions);
    }

    @Override
    public Track get(@PathParam("deviceKey") String deviceKey) throws EntityNotFoundException {
        Subject subject = getSubject(deviceKey);
        return new Track(subject.getId(), subject.getName());
    }

    @Override
    public Track[] list(@PathParam("deviceKey") String deviceKey) throws EntityNotFoundException {
        Subject subject = getSubject(deviceKey);
        List<Subject> subjectList = subjectWorker.getSubjects(subject.getUser());
        Track[] tracks = new Track[subjectList.size()];
        int i = 0;
        for (Subject _subject : subjectList) {
            tracks[i++] = new Track(_subject.getId(), _subject.getName());
        }
        return tracks;
    }

    @Override
    public Track create(@PathParam("deviceKey") String deviceKey, @PathParam("trakName") String name)
            throws EntityNotFoundException
    {
        Subject subject = getSubject(deviceKey);
        subject = subjectWorker.createNewOrLinkToExisting(subject.getUser(), name, deviceKey);
        return new Track(subject.getId(), subject.getName());
    }

    @Override
    public String continueTrak(@PathParam("deviceKey") String deviceKey, @PathParam("trakId") Long id)
            throws EntityNotFoundException
    {
        subjectWorker.unlinkSubject(deviceKey);

        Subject subject = subjectWorker.assignToSubject(UUID.randomUUID().toString(), id);
        return subject.getDeviceId();
    }

    @Override
    public Settings getSettings(@PathParam("deviceKey") String deviceKey) throws EntityNotFoundException {
        Subject subject = getSubject(deviceKey);
        return getTrackSettings(subject);
    }

    private Settings getTrackSettings(Subject subject) {
        DeviceSettings deviceSettings = subject.getDeviceSettings();
        if (deviceSettings == null) {
            deviceSettings = new DeviceSettings();
        }
        return new Settings(subject.getName(), deviceSettings.getSavePeriod(), deviceSettings.getSendPeriod());
    }

    @Override
    public void saveSettings(@PathParam("deviceKey") String deviceKey, Settings settings)
            throws SubjectAlreadyExistsException, EntityNotFoundException
    {
        final Subject subject = getSubject(deviceKey);

        subjectWorker.modifySubject(
                subject.getUser(),
                subject.getId(),
                settings.getName(),
                settings.getSendPeriod(),
                settings.getSavePeriod(),
                null
        );
    }

    @Override
    public void remove(@PathParam("deviceKey") String deviceKey, @PathParam("trakId") Long id) throws EntityNotFoundException {
        final Subject subject = getSubject(deviceKey);
        subjectWorker.removeSubject(subject.getUser(), id);
    }

    private Subject getSubject(String deviceKey) throws EntityNotFoundException {
        Subject subject = subjectWorker.getSubject(deviceKey);
        if (subject == null) {
            throw new EntityNotFoundException();
        }
        return subject;
    }
}
