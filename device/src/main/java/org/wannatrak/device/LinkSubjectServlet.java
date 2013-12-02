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
 * 16.03.2009
 */
package org.wannatrak.device;

import org.wannatrak.middleware.ejb.SubjectWorkerLocal;
import org.wannatrak.middleware.ejb.DeviceSettingsWorker;
import org.wannatrak.middleware.util.ServiceLocator;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.wannatrak.middleware.entity.DeviceSettings;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.DataOutputStream;

public class LinkSubjectServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final SubjectWorkerLocal subjectWorkerLocal = ServiceLocator.lookupLocal(SubjectWorkerLocal.JNDI_NAME);
        final DeviceSettingsWorker deviceSettingsWorker = ServiceLocator.lookupLocal(DeviceSettingsWorker.JNDI_NAME);

        final String deviceId = request.getParameter("deviceId");
        final String linkSubjectIdS = request.getParameter("linkSubjectId");

        final Long linkSubjectId = Long.parseLong(linkSubjectIdS); 

        DataOutputStream dos = null;
        try {
            final String newDeviceId = subjectWorkerLocal.linkSubject(deviceId, linkSubjectId);

            dos = new DataOutputStream(response.getOutputStream());

            final DeviceSettings deviceSettings = deviceSettingsWorker.getDeviceSettings(newDeviceId);
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
            dos.writeUTF(newDeviceId);
        } catch (EntityNotFoundException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            if (dos != null) {
                dos.close();
            }
        }
    }
}
