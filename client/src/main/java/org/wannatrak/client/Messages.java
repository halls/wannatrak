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
 * 10.12.2008 13:40:50
 */
package org.wannatrak.client;

import com.google.gwt.core.client.GWT;

public interface Messages extends com.google.gwt.i18n.client.Messages {

    String serverErrorWithCode(String code);

    String loginAvailable(String login);

    String loginNotAvailable(String login);

    String deleteSubjectFailed(String subjectName);

    String saveSubjectSettingsFailed(String subjectName);

    String subjectAlreadyExists(String subjectName);

    String info(String email);

    String multipleDays(@PluralCount int count);  

    public static class MessagesSingleton {
        private static final Messages instance = GWT.create(Messages.class);

        public static Messages getInstance() {
            return instance;
        }
    }
}
