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
 * 29.07.2008 23:12:35
 */
package org.wannatrak.middleware.ejb;

import org.wannatrak.middleware.entity.Session;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import javax.ejb.Stateless;

@Stateless(name = SessionWorker.JNDI_NAME)
public class SessionWorkerBean extends AbstractWorkerBean implements SessionWorkerLocal, SessionWorkerRemote {

    public void createUserSession(@NotNull String sessionId, @NotNull User user, @NotNull Boolean keepInCookie) {
        try {
            final Session session = dao.find(Session.class, sessionId);
            session.setLastAccess(new DateTime());
            session.setUser(user);
            session.setKeepInCookie(keepInCookie);
            dao.merge(session);
        } catch (EntityNotFoundException e) {
            final Session session = new Session();
            session.setId(sessionId);
            session.setLastAccess(new DateTime());
            session.setUser(user);
            session.setKeepInCookie(keepInCookie);
            dao.persist(session);
        }
    }

    public void removeSession(@NotNull String sessionId) {
        try {
            final Session session = dao.find(Session.class, sessionId);
            dao.remove(session);
        } catch (EntityNotFoundException e) {
        }
    }

    @Nullable
    public User getUser(@NotNull String sessionId) {
        final Session session = getSession(sessionId);
        if (session == null) {
            return null;
        }
        return session.getUser();
    }

    @Nullable
    public Session getSession(@NotNull String sessionId) {
        try {
            final Session session = dao.find(Session.class, sessionId);
            session.setLastAccess(new DateTime());
            return dao.merge(session);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }
}
