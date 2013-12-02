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
 * 16.02.2009 23:44:52
 */
package org.wannatrak.mobile.controller;

import org.wannatrak.mobile.model.Position;

import javax.microedition.location.*;
import java.util.Vector;

public class InternalLocationReceiver implements Runnable {

    private static final String JSR179MIMETYPE = "application/X-jsr179-location-nmea";

    private final Controller controller;

    private LocationProvider locationProvider;

    private boolean updated;

    private String extraInfo;

    private final LocationListener locationListener = new LocationListener() {
        public void locationUpdated(LocationProvider provider, Location location) {
            if (location != null && location.isValid()) {
                synchronized (InternalLocationReceiver.this) {
                    extraInfo = location.getExtraInfo(JSR179MIMETYPE);

                    if (extraInfo != null) {
                        updated = true;
                    }

                    float course = location.getCourse();
                    float speed = location.getSpeed();

                    final QualifiedCoordinates qc = location.getQualifiedCoordinates();
                    if (qc != null) {                    	
                        float altitude = qc.getAltitude();
                        float hdop = qc.getHorizontalAccuracy();

                        double latitude = qc.getLatitude();
                        double longitude = qc.getLongitude();
                        float vdop = qc.getVerticalAccuracy();
                        final long timestamp = location.getTimestamp();
                        final Position position
                                = new Position(longitude, latitude, speed, (short) course, (double) altitude, timestamp);
                        controller.putGPSPosition(position);
                        controller.setGPSReceiverFound(true);
                    }
                    //gpgsa=new GpsGPGSA(0.0f,hdop,vdop,0);
                }
            }
        }

        public void providerStateChanged(final javax.microedition.location.LocationProvider provider, final int newState) {
/*            String state="";
            switch (newState) {
                case javax.microedition.location.LocationProvider.AVAILABLE:
                    state="Available";
                    break;
                case javax.microedition.location.LocationProvider.OUT_OF_SERVICE:
                    state="Unavailable";
                    break;
                case javax.microedition.location.LocationProvider.TEMPORARILY_UNAVAILABLE:
                    state="Temporarily Unavailable";
                    break;

            }  */
        }
    };

    public InternalLocationReceiver(Controller controller) {
        this.controller = controller;
        init();
    }

    public void init() {
        try {
            if (locationProvider == null) {
                final Criteria criteria = new Criteria();
                criteria.setSpeedAndCourseRequired(true);
                criteria.setAltitudeRequired(true);
                locationProvider = LocationProvider.getInstance(criteria);
            }
            if (locationProvider != null) {            	
                locationProvider.setLocationListener(locationListener, -1, -1, -1);
            }
        } catch (LocationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (SecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public void run() {

        Vector nmeaStrings = new Vector();

        while (controller.started) {
            if (!updated) {
                controller.putCellPositionIfTimeToUpdate();              
                pause(1);
                continue;
            }

            synchronized (this) {
                updated = false;
                try {
                    if (extraInfo != null) {
                        StringBuffer output = new StringBuffer();
                        if (nmeaStrings.size() == 0 && extraInfo != null
                                && extraInfo.length() > 0) {
                            nmeaStrings = Parser.splitToNMEAVector(extraInfo);
                        }

                        if (nmeaStrings.size() > 0) {

                            output.append((String) nmeaStrings.firstElement());
                            nmeaStrings.removeElementAt(0);

                            controller.btLog("readNmeaString");
                            if (!Parser.trimNmeaString(output)) {
                                continue;
                            }
                            controller.btLog("trimNmeaString");
                            controller.parser.handleNmeaString(output.toString());
                            controller.btLog("handleNmeaString");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (locationProvider != null) {
            locationProvider.setLocationListener(null, -1, -1, -1);
        }
    }

    private void pause(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getClass().getName());
        }
    }

}
