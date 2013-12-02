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

package org.wannatrak.client.state;

import org.wannatrak.client.state.command.StateCommand;

import java.util.Map;

/**
 * Created 24.05.2009 23:28:59
 *
 * @author Andrey Khalzov
 */
public class SetNewPassInfoState implements State {
    private static SetNewPassInfoState instance = new SetNewPassInfoState();

    public static SetNewPassInfoState getInstance() {
        return instance;
    }

    private SetNewPassInfoState() {
    }

    public void executeCommand(StateCommand stateCommand, Map<String, String> params) {
        stateCommand.executeForSetNewPassInfoState(params);
    }
}
