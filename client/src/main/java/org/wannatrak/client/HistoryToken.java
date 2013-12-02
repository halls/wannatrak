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
 * 22.12.2008 20:50:19
 */
package org.wannatrak.client;

import org.wannatrak.client.state.command.*;

public enum HistoryToken {
    start(new StartStateCommand()),
    request_set_new_password(new RequestSetNewPassCommand()),
    set_new_password_info(new SetNewPassInfoCommand()),
    registration(new RegisterCommand()),
    logged_in(new LoginCommand()),
    demo(new DemoCommand()),
    how_to_setup(new HowtoSetupCommand()),
    how_to_setup_logged_in(new HowtoSetupLoggedInCommand()),
    how_to_setup_after_register(new HowtoSetupAfterRegisterCommand()),
    set_new_password(new SetNewPassCommand());

    private StateCommand state;

    HistoryToken(StateCommand state) {
        this.state = state;
    }

    public StateCommand getStateCommand() {
        return state;
    }
}
