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
 * 05.08.2008 0:26:42
 */
package org.wannatrak.device;

import org.wannatrak.middleware.ejb.DeviceSettingsWorker;
import org.wannatrak.middleware.ejb.TrackingHandler;
import org.wannatrak.middleware.ejb.SubjectWorkerLocal;
import org.wannatrak.middleware.entity.DeviceSettings;
import org.wannatrak.middleware.entity.Position;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.util.ServiceLocator;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PostServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {

    	Logger.getLogger(getClass()).debug("POST device J2ME");
    	
        final TrackingHandler trackingHandler = ServiceLocator.lookupLocal(TrackingHandler.JNDI_NAME);
        final DeviceSettingsWorker deviceSettingsWorker = ServiceLocator.lookupLocal(DeviceSettingsWorker.JNDI_NAME);
        final SubjectWorkerLocal subjectWorkerLocal = ServiceLocator.lookupLocal(SubjectWorkerLocal.JNDI_NAME);

        final String url = request.getRequestURI();
        int deviceIdIndex = url.lastIndexOf("/") + 1;
        if (deviceIdIndex < 0) {
            response.sendError(404);
        } else if (deviceIdIndex == url.length()) {
            deviceIdIndex = url.lastIndexOf("/", deviceIdIndex - 2) + 1;
        }

        final String deviceId = url.substring(deviceIdIndex, url.length());

        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(request.getInputStream());

            final int positionsNumber = dis.readInt();

            final Position[] positions = new Position[positionsNumber];
            for (int i = 0; i < positionsNumber; i++) {
                final Position position = new Position();
                position.setLongitude(dis.readDouble());
                position.setLatitude(dis.readDouble());
                position.setSpeed(dis.readDouble());
                position.setCourse((int) dis.readShort());
                position.setAltitude(dis.readDouble());
                position.setGpsTimestamp(new DateTime(dis.readLong()));

                positions[i] = position;
            }

            trackingHandler.handlePositions(deviceId, positions);

            final Subject subject = subjectWorkerLocal.getSubject(deviceId);
            if (subject == null) {
                throw new EntityNotFoundException();
            }
            
            dos = new DataOutputStream(response.getOutputStream());

            final DeviceSettings deviceSettings = deviceSettingsWorker.getDeviceSettings(deviceId);
            if (deviceSettings != null) {
                Integer sendPeriod = deviceSettings.getSendPeriod();
                if (sendPeriod == null) {
                    sendPeriod = 0;
                }

                Integer savePeriod = deviceSettings.getSavePeriod();
                if (savePeriod == null) {
                    savePeriod = 0;
                }
                dos.writeInt(sendPeriod);
                dos.writeInt(savePeriod);
            } else {
                dos.writeInt(0);
                dos.writeInt(0);
            }

            dos.writeUTF(subject.getName());
        } catch (EntityNotFoundException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            if (dis != null) {
                dis.close();
            }
            if (dos != null) {
                dos.close();
            }
        }
    }
}
