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
package org.wannatrak.middleware.ejb;

import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.exception.LoginFailedException;
import org.wannatrak.middleware.exception.UserAlreadyExistsException;
import org.wannatrak.middleware.exception.IncorrectEmailException;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.wannatrak.middleware.util.PasswordHelper;
import org.jetbrains.annotations.NotNull;

import javax.mail.MessagingException;

/**
 * Created 12.09.2009 23:23:13
 *
 * @author Andrey Khalzov
 */
public class MockUserWorker implements UserWorker {

    public static final String PASSWORD = "PASSWORD";

    @Override
    public User login(@NotNull String login, @NotNull String password) throws LoginFailedException {
        if (!PASSWORD.equals(password)) {
            throw new LoginFailedException();
        }

        final User user = new User();
        user.setLogin(login);
        user.setPassword(PasswordHelper.getHashCode(password));
        return user;
    }

    @Override
    public void loginOrContinueSession(@NotNull String sessionId, @NotNull String login, @NotNull String password, @NotNull Boolean keepInCookie) throws LoginFailedException {
    }

    @Override
    public boolean isLoginAvailable(String login) {
        return false;
    }

    @Override
    @NotNull
    public User createUser(@NotNull String login, @NotNull String password, @NotNull String email, @NotNull String hostName) throws UserAlreadyExistsException {
        return new User();
    }

    @Override
    public void requestSetNewPassword(@NotNull String login, @NotNull String email) throws MessagingException, IncorrectEmailException, IllegalAccessException, EntityNotFoundException {
    }

    @Override
    public String setNewPassword(String userHash, String password) throws EntityNotFoundException {
        return null;
    }
}
