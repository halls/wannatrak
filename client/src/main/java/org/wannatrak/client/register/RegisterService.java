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

package org.wannatrak.client.register;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import org.wannatrak.client.exception.*;

/**
 * Created by Andrey Khalzov
 * 26.12.2008 1:05:23
 */
@RemoteServiceRelativePath("register")
public interface RegisterService extends RemoteService {

    boolean isLoginAvailable(String login);

    void register(String login, String password, String email, String captcha)
            throws CaptchaFailedException, UserAlreadyExistsException;

    void requestSetNewPassword(String login, String email, String captcha)
            throws CaptchaFailedException, SendMailException, UserNotFoundOnRequestSetNewPassException;

    String setNewPassword(String userHash, String password, String captcha) 
            throws CaptchaFailedException, WrongRequestSetNewPassIdException;

    public static class App {
        private static final RegisterServiceAsync instance = (RegisterServiceAsync) GWT.create(RegisterService.class);

        public static RegisterServiceAsync getInstance() {
            return instance;
        }
    }
}
