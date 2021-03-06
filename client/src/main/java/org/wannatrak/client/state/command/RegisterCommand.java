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
 * 23.12.2008 15:39:45
 */
package org.wannatrak.client.state.command;

import java.util.Map;

public class RegisterCommand extends AbstractStateCommand {

    @Override
    public void executeForStartState(Map<String, String> params) {
        getMediator().register();
    }

    @Override
    public void executeForDemoState(Map<String, String> params) {
        getMediator().register();
    }

    @Override
    public void executeForRequestSetNewPassState(Map<String, String> params) {
        getMediator().register();
    }

    @Override
    public void executeForLoggedInState(Map<String, String> params) {
        getMediator().logout();
        getMediator().register();
    }

    @Override
    public void executeForHowtoSetupAfterRegisterState(Map<String, String> params) {
        getMediator().logout();
        getMediator().register();
    }

    @Override
    public void executeForRegisterState(Map<String, String> params) {
    }
}