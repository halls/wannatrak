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
 * 22.12.2008 21:11:52
 */
package org.wannatrak.client.state.command;

import org.wannatrak.client.Mediator;
import org.wannatrak.client.HistoryTokenParam;
import org.wannatrak.client.state.*;

import java.util.Map;
import java.util.Collections;

public abstract class AbstractStateCommand implements StateCommand {
    
    private transient Mediator mediator;

    public final void execute() {
        final Map<String,  String> emptyMap = Collections.emptyMap();
        mediator.getState().executeCommand(this, emptyMap);
    }

    public void execute(Map<String, String> params) {
        mediator.getState().executeCommand(this, params);
    }

    public void executeForStartState(Map<String, String> params) {
        failExecuteFor(StartState.getInstance());
    }

    public void executeForDemoState(Map<String, String> params) {
        failExecuteFor(DemoState.getInstance());
    }

    public void executeForRequestSetNewPassState(Map<String, String> params) {
        failExecuteFor(RequestChangePassState.getInstance());
    }

    public void executeForRegisterState(Map<String, String> params) {
        failExecuteFor(RegisterState.getInstance());
    }

    public void executeForLoggedInState(Map<String, String> params) {
        failExecuteFor(LoggedInState.getInstance());
    }

    public void executeForHowtoSetupState(Map<String, String> params) {
        failExecuteFor(HowtoSetupState.getInstance());
    }

    public void executeForHowtoSetupAfterRegisterState(Map<String, String> params) {
        failExecuteFor(HowtoSetupAfterRegisterState.getInstance());
    }

    public void executeForSetNewPassState(Map<String, String> params) {
        failExecuteFor(HowtoSetupState.getInstance());
    }

    public void executeForSetNewPassInfoState(Map<String, String> params) {
        failExecuteFor(SetNewPassInfoState.getInstance());
    }

    public void executeForHowtoSetupLoggedInState(Map<String, String> params) {
        failExecuteFor(HowtoSetupLoggedInState.getInstance());
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    protected Mediator getMediator() {
        return mediator;
    }

    private void failExecuteFor(State currentState) {
        throw new IllegalStateException(getClass().getName() + " for " + currentState.getClass().getName());
    }
}
