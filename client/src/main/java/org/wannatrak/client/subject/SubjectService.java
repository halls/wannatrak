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

package org.wannatrak.client.subject;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import org.wannatrak.client.exception.NotLoggedInException;
import org.wannatrak.client.exception.SubjectInfoNoAccessException;
import org.wannatrak.client.exception.SubjectAlreadyExistsException;

/**
 * Created by Andrey Khalzov
 * 14.07.2008 21:53:53
 */
@RemoteServiceRelativePath("subjects")
public interface SubjectService extends RemoteService {

    PositionData getLastPosition(Long subjectId, Boolean valid);
    
    SubjectData[] getSubjects();

    SubjectInfoData getSubjectInfo(Long subjectId) throws NotLoggedInException, SubjectInfoNoAccessException;

    void removeSubject(Long subjectId) throws NotLoggedInException;

    void saveSettings(Long subjectId, SubjectSettingsData subjectSettingsData) 
            throws SubjectAlreadyExistsException, NotLoggedInException;

    public static class App {
        private static SubjectServiceAsync instance = null;

        public static synchronized SubjectServiceAsync getInstance() {
            if (instance == null) {
                instance = (SubjectServiceAsync) GWT.create(SubjectService.class);
            }
            return instance;
        }
    }
}
