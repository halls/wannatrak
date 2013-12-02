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

package org.wannatrak.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.wannatrak.client.register.RegisterService;
import org.wannatrak.client.exception.*;
import org.wannatrak.middleware.ejb.UserWorker;
import org.wannatrak.middleware.util.ServiceLocator;
import org.wannatrak.middleware.exception.IncorrectEmailException;
import org.wannatrak.middleware.exception.EntityNotFoundException;

import javax.servlet.http.HttpSession;
import javax.mail.MessagingException;

/**
 * Created by Andrey Khalzov
 * 26.12.2008 1:05:23
 */
public class RegisterServiceImpl extends RemoteServiceServlet implements RegisterService {

    public boolean isLoginAvailable(String login) {
        final UserWorker userWorker = ServiceLocator.lookup(UserWorker.JNDI_NAME);

        return userWorker.isLoginAvailable(login);
    }

    public void register(String login, String password, String email, String captcha)
            throws CaptchaFailedException, UserAlreadyExistsException
    {
        final HttpSession session = getThreadLocalRequest().getSession();
        checkCaptcha(session, captcha);

        final UserWorker userWorker = ServiceLocator.lookup(UserWorker.JNDI_NAME);
        try {
            userWorker.createUser(login, password, email, getThreadLocalRequest().getServerName());
            userWorker.loginOrContinueSession(session.getId(), login, password, false);
        } catch (org.wannatrak.middleware.exception.UserAlreadyExistsException e) {
            throw new UserAlreadyExistsException();
        } catch (org.wannatrak.middleware.exception.LoginFailedException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void requestSetNewPassword(String login, String email, String captcha)
            throws SendMailException, CaptchaFailedException, UserNotFoundOnRequestSetNewPassException
    {
        final HttpSession session = getThreadLocalRequest().getSession();
        checkCaptcha(session, captcha);

        final UserWorker userWorker = ServiceLocator.lookup(UserWorker.JNDI_NAME);
        try {
            userWorker.requestSetNewPassword(login, email);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SendMailException();
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundOnRequestSetNewPassException();
        } catch (IncorrectEmailException e) {
            e.printStackTrace();
            throw new SendMailException();
        } catch (IllegalAccessException e) {
            throw new SendMailException();
        }
    }


    public String setNewPassword(String userHash, String password, String captcha)
            throws WrongRequestSetNewPassIdException, CaptchaFailedException
    {
        final HttpSession session = getThreadLocalRequest().getSession();
        checkCaptcha(session, captcha);

        final UserWorker userWorker = ServiceLocator.lookup(UserWorker.JNDI_NAME);
        try {
            final String login = userWorker.setNewPassword(userHash, password);
            userWorker.loginOrContinueSession(session.getId(), login, password, false);
            return login;
        } catch (EntityNotFoundException e) {
            throw new WrongRequestSetNewPassIdException();
        } catch (org.wannatrak.middleware.exception.LoginFailedException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private void checkCaptcha(HttpSession session, String captcha) throws CaptchaFailedException {
        final String code = (String) session.getAttribute("verification.code");

        if (!captcha.equals(code)) {
            throw new CaptchaFailedException();
        }
    }
}