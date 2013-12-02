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

import org.apache.log4j.Logger;
import org.wannatrak.middleware.ejb.UserWorkerLocal;
import org.wannatrak.middleware.ejb.SubjectWorkerLocal;
import org.wannatrak.middleware.util.ServiceLocator;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.exception.LoginFailedException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.DataInputStream;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Logger.getLogger(getClass()).debug("LOGIN device J2ME");
    	
        final UserWorkerLocal userWorkerLocal = ServiceLocator.lookupLocal(UserWorkerLocal.JNDI_NAME);
        final SubjectWorkerLocal subjectWorkerLocal = ServiceLocator.lookupLocal(SubjectWorkerLocal.JNDI_NAME);

        DataInputStream dis = null;
        try {
            dis = new DataInputStream(request.getInputStream());
            final String login = dis.readUTF();
            final String password = dis.readUTF();

            final User user = userWorkerLocal.login(login, password);

            final String deviceId = subjectWorkerLocal.loginDevice(user);
            final byte[] resultBytes = deviceId.getBytes("utf-8");
            response.setContentLength(resultBytes.length);
            response.getOutputStream().write(resultBytes);
        } catch (LoginFailedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } finally {
            if (dis != null) {
                dis.close();
            }
        }
    }
}