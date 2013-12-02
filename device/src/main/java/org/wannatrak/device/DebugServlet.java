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
 * 15.09.2008 21:57:20
 */
package org.wannatrak.device;

import org.wannatrak.middleware.ejb.SubjectWorkerLocal;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.util.ServiceLocator;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;

public class DebugServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	Logger.getLogger(getClass()).debug("DEBUG device J2ME");
    	
        final SubjectWorkerLocal subjectWorkerLocal = ServiceLocator.lookupLocal(SubjectWorkerLocal.JNDI_NAME);

        final String url = request.getRequestURI();
        String logs = "";
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(request.getInputStream());
            logs = dis.readUTF();
        } finally {
            if (dis != null) {
                dis.close();
            }
        }

        int deviceIdIndex = url.lastIndexOf("/") + 1;
        if (deviceIdIndex < 0) {
            Logger.getLogger(getClass()).debug(url + ":\n" + logs);
            response.sendError(404);
            return;
        } else if (deviceIdIndex == url.length()) {
            deviceIdIndex = url.lastIndexOf("/", deviceIdIndex - 2) + 1;
        }

        String deviceId = url.substring(deviceIdIndex, url.length());
        final Subject subject = subjectWorkerLocal.getSubject(deviceId);
        if (subject != null) {
            deviceId = subject.getId().toString();
        }

        Logger.getLogger(getClass()).debug(deviceId + ":\n" + logs);

        final byte[] resultBytes = "OK".getBytes("utf-8");
        response.setContentLength(resultBytes.length);
        response.getOutputStream().write(resultBytes);
    }
}
