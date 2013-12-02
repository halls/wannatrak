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

import org.wannatrak.middleware.exception.LoginFailedException;
import org.wannatrak.middleware.exception.EntityNotFoundException;

import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;

/**
 * Created 11.09.2009 1:00:36
 *
 * @author Andrey Khalzov
 */
@Path("/")
public interface Api {

    @POST
    @Path("login")
    @Consumes("text/json")
    String login(Login login) throws LoginFailedException;

    @POST
    @Path("logout")
    @Consumes("text/json")
    void logout(String deviceKey) throws EntityNotFoundException;


    static class Login {
        public String login;
        public String password;

        public Login() {
        }

        public Login(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
