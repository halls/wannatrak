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
 * 23.11.2008 9:59:38
 */
package org.wannatrak.middleware.ejb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.ejb.EJB;

import org.wannatrak.middleware.entity.Position;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.entity.TrackingContext;
import org.wannatrak.middleware.entity.DeviceSettings;
import org.wannatrak.middleware.util.BooleanHelper;
import org.wannatrak.middleware.util.GeoHelper;
import org.wannatrak.middleware.exception.EntityNotFoundException;

@Stateless(name = TrackingHandler.JNDI_NAME)
public class TrackingHandlerBean extends AbstractWorkerBean implements TrackingHandler {

    @EJB
    private SubjectWorkerLocal subjectWorkerLocal;

    public void handlePositions(@NotNull String deviceId, @NotNull Position[] positions)
            throws EntityNotFoundException
    {
        final Subject subject = subjectWorkerLocal.getSubject(deviceId);
        if (subject == null) {
            throw new EntityNotFoundException();
        }

        TrackingContext trackingContext = subject.getTrackingContext();
        if (trackingContext == null) {
            trackingContext = new TrackingContext();
            dao.persist(trackingContext);
        }

        DeviceSettings deviceSettings = subject.getDeviceSettings();
        if (deviceSettings == null) {
            deviceSettings = new DeviceSettings();
            dao.persist(deviceSettings);
            subject.setDeviceSettings(deviceSettings);
        }

        Long odometer = subject.getOdometer();
        if (odometer == null) {
            odometer = 0L;
        }

        Position lastValidPosition = null;
        for (Position position : positions) {
            position.setSubject(subject);

            validatePosition(trackingContext, deviceSettings, position);

            if (BooleanHelper.valueOf(position.getValid())) {
                lastValidPosition = position;
            }

            final Long positionOdometer = position.getOdometer();
            if (positionOdometer != null) {
                odometer += position.getOdometer();

                if (positionOdometer < 0) {
                    position.setOdometer(0L);
                }
            }

            dao.persist(position);
        }
        trackingContext = dao.merge(trackingContext);

        if (lastValidPosition != null) {
            subject.setPosition(lastValidPosition);
        }
        subject.setLastUpdated(new DateTime());
        subject.setOdometer(odometer);
        subject.setTrackingContext(trackingContext);
        dao.merge(subject);
    }

    public void fixValidation() {
/*
        final List<Position> positions
                = em.createQuery(
                    "select e from Position e where e.id > 646800 order by e.id"
                ).getResultList();

        final Position lastValidPosition = positions.get(0);
        final TrackingContext trackingContext = lastValidPosition.getSubject().getTrackingContext();
        trackingContext.setLastValidPosition(lastValidPosition);
        trackingContext.setCenterPosition(lastValidPosition);
        trackingContext.setPrevCenterPosition(null);
        positions.remove(lastValidPosition);
        for (Position position : positions) {
            validatePosition(trackingContext, position.getSubject().getDeviceSettings(), position);
            dao.merge(position);
        }
*/

    }

    private void validatePosition(
            @NotNull TrackingContext trackingContext,
            @NotNull DeviceSettings deviceSettings,
            @NotNull Position position
    ) {
        final Position lastValidPosition = trackingContext.getLastValidPosition();
        final Position centerPosition = trackingContext.getCenterPosition();
        final Position prevCenterPosition = trackingContext.getPrevCenterPosition();

        long timeDelta = calculateTimeDelta(position, lastValidPosition);
        double actualSpeed;
        long distanceFromCenter;
        if (lastValidPosition == null || timeDelta == 0) {
            actualSpeed = 0;
            distanceFromCenter = 0;
        } else {
            distanceFromCenter = calculateDistanceFromCenter(deviceSettings, position, centerPosition, prevCenterPosition);

            long actualMovement = (long) GeoHelper.getDistance(
                    position.getLongitude(),
                    position.getLatitude(),
                    lastValidPosition.getLongitude(),
                    lastValidPosition.getLatitude()
            );

            actualSpeed = 3600 * actualMovement / timeDelta;
        }

        boolean valid = isValid(deviceSettings, position, lastValidPosition, actualSpeed, timeDelta);
        position.setValid(valid);
        position.setActualSpeed(actualSpeed);

        updateContext(
                trackingContext,
                deviceSettings,
                position,
                centerPosition,
                prevCenterPosition,
                distanceFromCenter,
                valid
        );
    }

    private long calculateDistanceFromCenter(
            @NotNull DeviceSettings deviceSettings,
            @NotNull Position position,
            @Nullable Position centerPosition,
            @Nullable Position prevCenterPosition
    ) {
        long distanceFromCenter = 0;
        if (centerPosition != null) {

            long distanceFromPrevCenter;
            if (prevCenterPosition != null) {
                distanceFromPrevCenter = (long) GeoHelper.getDistance(
                    position.getLongitude(),
                    position.getLatitude(),
                    prevCenterPosition.getLongitude(),
                    prevCenterPosition.getLatitude()
                );

                if (distanceFromPrevCenter <= deviceSettings.getOdometerAccuracy()) {
                    distanceFromCenter = - centerPosition.getOdometer();
                    centerPosition.setOdometer(0L);
                    em.merge(centerPosition);
                } else {
                    distanceFromCenter = (long) GeoHelper.getDistance(
                        position.getLongitude(),
                        position.getLatitude(),
                        centerPosition.getLongitude(),
                        centerPosition.getLatitude()
                    );
                }
            } else {
                distanceFromCenter = (long) GeoHelper.getDistance(
                        position.getLongitude(),
                        position.getLatitude(),
                        centerPosition.getLongitude(),
                        centerPosition.getLatitude()
                );
            }
        }
        return distanceFromCenter;
    }

    private void updateContext(
            @NotNull TrackingContext trackingContext,
            @NotNull DeviceSettings deviceSettings,
            @NotNull Position position,
            @Nullable Position centerPosition,
            @Nullable Position prevCenterPosition,
            long distanceFromCenter,
            boolean valid
    ) {
        if (valid) {
            final Integer odometerAccuracy = deviceSettings.getOdometerAccuracy();
            if (centerPosition == null
                    || odometerAccuracy == null
                    || distanceFromCenter > odometerAccuracy
            ) {
                trackingContext.setPrevCenterPosition(centerPosition);
                trackingContext.setCenterPosition(position);

                position.setOdometer(distanceFromCenter);
            } else if (distanceFromCenter < 0) {
                trackingContext.setPrevCenterPosition(null);
                trackingContext.setCenterPosition(prevCenterPosition);
                position.setOdometer(distanceFromCenter);
            } else {
                position.setOdometer(0L);
            }

            trackingContext.setLastValidPosition(position);
        } else {
            position.setOdometer(0L);
        }
    }

    private boolean isValid(
            @NotNull DeviceSettings deviceSettings,
            @NotNull Position position,
            @Nullable Position lastValidPosition,
            double actualSpeed,
            long timeDelta
    ) {
        return lastValidPosition == null || !position.getGpsTimestamp().equals(lastValidPosition.getGpsTimestamp());
    }

    private long calculateTimeDelta(@NotNull Position position, @Nullable Position lastValidPosition) {
        if (lastValidPosition == null) {
            return 0;
        }
        return position.getGpsTimestamp().minus(lastValidPosition.getGpsTimestamp().getMillis()).getMillis();
    }

}
