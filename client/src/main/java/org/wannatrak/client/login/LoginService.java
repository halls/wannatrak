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

package org.wannatrak.client.login;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import org.wannatrak.client.exception.LoginFailedException;

/**
 * Created by Andrey Khalzov
 * 23.12.2008 18:55:53
 */
@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {

    String login(String login, String password, Boolean keepMyLogin) throws LoginFailedException;

    void logout();

    String tryToLogin() throws LoginFailedException;

    public static class App {
        private static final LoginServiceAsync instance = (LoginServiceAsync) GWT.create(LoginService.class);

        public static LoginServiceAsync getInstance() {
            return instance;
        }
    }
}
