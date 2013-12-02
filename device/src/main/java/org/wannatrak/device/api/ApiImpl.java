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
package org.wannatrak.device.api;

import com.google.inject.Inject;
import org.wannatrak.middleware.ejb.UserWorker;
import org.wannatrak.middleware.ejb.SubjectWorker;
import org.wannatrak.middleware.exception.LoginFailedException;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.wannatrak.middleware.entity.User;

/**
 * Created 13.09.2009 14:01:45
 *
 * @author Andrey Khalzov
 */
public class ApiImpl implements Api {

    @Inject
    UserWorker userWorker;

    @Inject
    SubjectWorker subjectWorker;

    @Override
    public String login(Login login) throws LoginFailedException {
        final User user = userWorker.login(login.login, login.password);
        return subjectWorker.loginDeviceAsLastUsedOrCreateNew(user);
    }

    @Override
    public void logout(String deviceCode) throws EntityNotFoundException {
        subjectWorker.unlinkSubject(deviceCode);
    }
}
