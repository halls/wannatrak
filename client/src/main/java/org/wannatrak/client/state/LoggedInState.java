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
 * 30.12.2008 3:33:45
 */
package org.wannatrak.client.state;

import org.wannatrak.client.state.command.StateCommand;

import java.util.Map;

public class LoggedInState implements State {

    private static LoggedInState instance = new LoggedInState();

    public static LoggedInState getInstance() {
        return instance;
    }

    private LoggedInState() {
    }

    public void executeCommand(StateCommand stateCommand, Map<String, String> params) {
        stateCommand.executeForLoggedInState(params);
    }
}
