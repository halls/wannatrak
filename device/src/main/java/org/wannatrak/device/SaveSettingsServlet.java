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
 * 17.03.2009
 */
package org.wannatrak.device;

import org.wannatrak.middleware.ejb.SubjectWorkerLocal;
import org.wannatrak.middleware.util.ServiceLocator;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.exception.SubjectAlreadyExistsException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.DataInputStream;

public class SaveSettingsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final SubjectWorkerLocal subjectWorkerLocal = ServiceLocator.lookupLocal(SubjectWorkerLocal.JNDI_NAME);

        DataInputStream dis = null;
        try {
            dis = new DataInputStream(request.getInputStream());
            final String deviceId = dis.readUTF();
            final String name = dis.readUTF();
            final int savePeriod = dis.readInt();
            final int sendPeriod = dis.readInt();

            final Subject subject = subjectWorkerLocal.getSubject(deviceId);
            if (subject == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            final User user = subject.getUser();
            if (user == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            subjectWorkerLocal.modifySubject(subject.getUser(), subject.getId(), name, sendPeriod, savePeriod, null);
        } catch (SubjectAlreadyExistsException e) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
        } finally {
            if (dis != null) {
                dis.close();
            }
        }
    }
}