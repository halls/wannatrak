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
 * 23.12.2008 21:46:11
 */
package org.wannatrak.middleware.ejb;

import org.jetbrains.annotations.NotNull;

import javax.ejb.Remote;

import org.wannatrak.middleware.exception.LoginFailedException;
import org.wannatrak.middleware.exception.UserAlreadyExistsException;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.wannatrak.middleware.entity.User;

@Remote
public interface UserWorkerRemote extends UserWorker {

    User login(@NotNull String login, @NotNull String password) throws LoginFailedException;

    void loginOrContinueSession(
            @NotNull String sessionId,
            @NotNull String login,
            @NotNull String password,
            @NotNull Boolean keepInCookie
    ) throws LoginFailedException;

    boolean isLoginAvailable(String login);

    @NotNull
    User createUser(@NotNull String login, @NotNull String password, @NotNull String email, @NotNull String hostName)
            throws UserAlreadyExistsException;

    String setNewPassword(String userHash, String password) throws EntityNotFoundException;
}