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

import java.util.Map;

import org.wannatrak.client.state.command.StateCommand;

/**
 * Created 26.04.2009 16:06:53
 *
 * @author Sergey Grachev
 */
public class SetNewPassState implements State {
    private static SetNewPassState instance = new SetNewPassState();

    public static SetNewPassState getInstance() {
        return instance;
    }

    private SetNewPassState() {
    }

    public void executeCommand(StateCommand stateCommand, Map<String, String> params) {
        stateCommand.executeForSetNewPassState(params);
    }
}
