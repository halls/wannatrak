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
 * 30.12.2008 3:37:34
 */
package org.wannatrak.client.state.command;

import org.wannatrak.client.HistoryTokenParam;
import org.wannatrak.client.state.param.LoggedInHistoryTokenParam;

import java.util.Map;

public class LoginCommand extends AbstractStateParamsHandlerCommand {

    protected HistoryTokenParam getHistoryTokenParam(String key) {
        return LoggedInHistoryTokenParam.valueOf(key);
    }

    @Override
    public void executeForStartState(Map<String, String> params) {
        getMediator().tryToLogin(params);
    }

    @Override
    public void executeForDemoState(Map<String, String> params) {
        getMediator().requestLogin();
        handleParams(params);
    }

    @Override
    public void executeForLoggedInState(Map<String, String> params) {
        handleParams(params);
    }

    @Override
    public void executeForHowtoSetupAfterRegisterState(Map<String, String> params) {
        getMediator().loginAfterRegister(getMediator().getRegisterController().getCheckingLogin());
    }

    @Override
    public void executeForHowtoSetupLoggedInState(Map<String, String> params) {
        getMediator().returnToLoggedIn();
        handleParams(params);
    }

    @Override
    public void executeForSetNewPassState(Map<String, String> params) {
        getMediator().getSetNewPassController().submit();
    }
}
