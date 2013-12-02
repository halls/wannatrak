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
 * 17.08.2008 1:29:50
 */
package org.wannatrak.device;

import org.apache.log4j.Logger;
import org.wannatrak.middleware.ejb.SubjectWorkerLocal;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.util.ServiceLocator;
import org.wannatrak.middleware.util.StringHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.List;

public class ListSubjectsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Logger.getLogger(getClass()).debug("LIST device J2ME");
    	
        final SubjectWorkerLocal subjectWorkerLocal = ServiceLocator.lookupLocal(SubjectWorkerLocal.JNDI_NAME);

        final String deviceId = request.getParameter("deviceId");
        final Subject subject = subjectWorkerLocal.getSubject(deviceId);
        if (subject == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final User user = subject.getUser();
        final List<Subject> subjects = subjectWorkerLocal.getSubjects(user);

        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(response.getOutputStream());
            dos.writeInt(subjects.size());

            for (Subject unlinkedSubject : subjects) {
                String subjectName = unlinkedSubject.getName();
                if (StringHelper.isAlmostEmpty(subjectName)) {
                    continue;
                }
                dos.writeLong(unlinkedSubject.getId());
                dos.writeUTF(subjectName);
            }
        } finally {
            if (dos != null) {
                dos.close();
            }
        }
    }
}
