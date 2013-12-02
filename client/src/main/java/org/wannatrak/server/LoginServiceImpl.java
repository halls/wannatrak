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
import org.wannatrak.client.login.LoginService;
import org.wannatrak.middleware.ejb.UserWorker;
import org.wannatrak.middleware.ejb.SessionWorker;
import org.wannatrak.middleware.util.ServiceLocator;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.entity.Session;
import org.wannatrak.client.exception.LoginFailedException;
import org.joda.time.DateTimeConstants;

import javax.servlet.http.Cookie;

/**
 * Created by Andrey Khalzov
 * 23.12.2008 18:55:53
 */
public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

    public String login(String login, String password, Boolean keepMyLogin) throws LoginFailedException {
        final UserWorker userWorker = ServiceLocator.lookup(UserWorker.JNDI_NAME);
        final SessionWorker sessionWorker = ServiceLocator.lookup(SessionWorker.JNDI_NAME);

        final String sessionId = getThreadLocalRequest().getSession().getId();

        try {
            userWorker.loginOrContinueSession(sessionId, login, password, keepMyLogin);
            final User user = sessionWorker.getUser(sessionId);
            if (user == null) {
                throw new LoginFailedException();
            }

            if (keepMyLogin) {
                final Cookie sessionCookie = getSessionCookie();
                if (sessionCookie != null) {
                    sessionCookie.setMaxAge(DateTimeConstants.SECONDS_PER_WEEK * 2);
                    getThreadLocalResponse().addCookie(sessionCookie);
                }
            }
            return createLoginResult(user);
        } catch (org.wannatrak.middleware.exception.LoginFailedException e) {
            throw new LoginFailedException();
        }
    }

    public String tryToLogin() throws LoginFailedException {
        final SessionWorker sessionWorker = ServiceLocator.lookup(SessionWorker.JNDI_NAME);

        final String sessionId = getThreadLocalRequest().getSession().getId();

        final Session session = sessionWorker.getSession(sessionId);
        if (session == null) {
            throw new LoginFailedException();
        }
        final User user = session.getUser();
        if (user == null) {
            throw new LoginFailedException();
        }
        if (session.isKeepInCookie()) {
            final Cookie sessionCookie = getSessionCookie();
            if (sessionCookie != null) {
                sessionCookie.setMaxAge(DateTimeConstants.SECONDS_PER_WEEK * 2);
                getThreadLocalResponse().addCookie(sessionCookie);
            }
        }

        return createLoginResult(user);
    }

    public void logout() {
        final SessionWorker sessionWorker = ServiceLocator.lookup(SessionWorker.JNDI_NAME);
        final String sessionId = getThreadLocalRequest().getSession().getId();

        final Cookie sessionCookie = getSessionCookie();
        if (sessionCookie != null) {
            sessionCookie.setMaxAge(0);
            getThreadLocalResponse().addCookie(sessionCookie);
        }

        sessionWorker.removeSession(sessionId);
    }

    private Cookie getSessionCookie() {
        final Cookie[] cookies = getThreadLocalRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private String createLoginResult(User user) {
        return user.getDisplayLogin();
    }
}