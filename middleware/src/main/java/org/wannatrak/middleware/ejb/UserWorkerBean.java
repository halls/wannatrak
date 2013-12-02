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
 * 13.07.2008 0:30:06
 */
package org.wannatrak.middleware.ejb;

import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.entity.RecoverPassword;
import org.wannatrak.middleware.exception.*;
import org.wannatrak.middleware.util.PasswordHelper;
import org.wannatrak.middleware.configuration.configurations.MailConfiguration;
import org.wannatrak.middleware.configuration.ConfigurationContainer;
import org.wannatrak.middleware.localization.Messages;
import org.jetbrains.annotations.NotNull;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.Locale;

@Stateless(name = UserWorker.JNDI_NAME)
public class UserWorkerBean extends AbstractWorkerBean implements UserWorkerLocal, UserWorkerRemote {

    @EJB
    private SessionWorkerLocal sessionWorker;

    @EJB
    private MailService mailService;

    @NotNull
    public User createUser(
            @NotNull String login,
            @NotNull String password,
            @NotNull String email,
            @NotNull String hostName
    )
            throws UserAlreadyExistsException
    {
        try {
            dao.findSingleResult(User.class, "e.login = ?", login.toLowerCase());
            throw new UserAlreadyExistsException();
        } catch (EntityNotFoundException e) {
            final User user = new User();
            user.setLogin(login.toLowerCase());
            user.setDisplayLogin(login);
            user.setPassword(PasswordHelper.getHashCode(password));
            user.setEmail(email);
            user.setHostName(hostName);
            dao.persist(user);
            return user;
        }
    }

    public void loginOrContinueSession(
            @NotNull String sessionId,
            @NotNull String login,
            @NotNull String password,
            @NotNull Boolean keepInCookie
    ) throws LoginFailedException {
        try {
            final User user;
            if (login.isEmpty()) {
                user = sessionWorker.getUser(sessionId);
                if (user == null) {
                    throw new LoginFailedException();
                }
            } else {
                user = dao.findSingleResult(
                        User.class,
                        "e.login = ? and password = ?",
                        login.toLowerCase(),
                        PasswordHelper.getHashCode(password)
                );
                sessionWorker.createUserSession(sessionId, user, keepInCookie);
            }
        } catch (EntityNotFoundException e) {
            throw new LoginFailedException();
        }
    }

    public User login(@NotNull String login, @NotNull String password) throws LoginFailedException {
        try {
            return dao.findSingleResult(
                    User.class,
                    "e.login = ? and password = ?",
                    login.toLowerCase(),
                    PasswordHelper.getHashCode(password)
            );
        } catch (EntityNotFoundException e) {
            throw new LoginFailedException();
        }
    }

    public boolean isLoginAvailable(String login) {
        try {
            dao.findSingleResult(User.class, "e.login = ?", login.toLowerCase());
            return false;
        } catch (EntityNotFoundException e) {
            return true;
        }
    }

    public void updateUser(@NotNull String login, @NotNull String password, @NotNull User user) {
    }

    public void deleteUser(@NotNull String login, @NotNull String password, @NotNull User user) {
    }

    public void requestSetNewPassword(@NotNull String login, @NotNull String email) throws MessagingException, IncorrectEmailException, IllegalAccessException, EntityNotFoundException {
        final User user = dao.findSingleResult(
                User.class,
                "LOWER(e.login) = ?1 AND LOWER(e.email) = ?2",
                login.toLowerCase(), email.toLowerCase()
        );

        // remove old if exists
        final List<RecoverPassword> recoverPasswords = dao.find(RecoverPassword.class, "e.user.id = ?", user.getId());
        for (final RecoverPassword item : recoverPasswords) {
            dao.remove(item);
        }

        try {
            new InternetAddress(email).validate();
        } catch (AddressException e) {
            throw new IncorrectEmailException(e.getMessage());
        }

        final RecoverPassword recoverPassword = new RecoverPassword();
        recoverPassword.setUser(user);
        recoverPassword.setLinkHash(PasswordHelper.getHashCodeAsHex(
                new StringBuffer(login).append(email).append(user.getPassword()).toString())
        );

        final StringBuilder message = new StringBuilder("http://").append(Messages.get("host"))
                .append("/#set_new_password?request_id=").append(recoverPassword.getLinkHash());

        mailService.send(
                user.getEmail(),
                Messages.get("register.remindPasswordMailSubject"),
                Messages.get("register.remindPasswordMailBody", login, message.toString())
        );

        dao.persist(recoverPassword);
    }

    public String setNewPassword(String userHash, String password) throws EntityNotFoundException {
        final RecoverPassword passwordRequest = dao.findSingleResult(RecoverPassword.class, "e.linkHash = ?1", userHash);
        User user = passwordRequest.getUser();
        user.setPassword(PasswordHelper.getHashCode(password));
        user = dao.merge(user);
        dao.remove(passwordRequest);
        return user.getDisplayLogin();
    }

    @Override
    public List<User> getUsersByHostNameForMailing(String hostName) {
        return dao.find(User.class, "e.email is not null and e.hostName like ?", hostName);
    }
}
