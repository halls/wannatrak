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
 * 22.12.2008 20:36:07
 */
package org.wannatrak.client;

import org.wannatrak.client.state.command.StateCommand;
import com.google.gwt.user.client.History;

import java.util.Map;
import java.util.HashMap;

public class HistoryListener implements com.google.gwt.user.client.HistoryListener {
    public static final String PARAMS_BEGINNING = "?";
    public static final String PARAMS_SEPARATOR = "&";

    public HistoryListener(Mediator mediator) {
        for (HistoryToken historyToken : HistoryToken.values()) {
            historyToken.getStateCommand().setMediator(mediator);
        }
        onHistoryChanged(History.getToken());
    }

    public void onHistoryChanged(String historyToken) {
        Map<String, String> params = new HashMap<String, String>();
        int paramsIndex = historyToken.indexOf(PARAMS_BEGINNING);
        if (paramsIndex >= 0 && paramsIndex < historyToken.length() - 1) {
            String paramsString = historyToken.substring(paramsIndex + 1);
            for (String param : paramsString.split(PARAMS_SEPARATOR)) {
                String[] keyValue = param.split("=");
                if (keyValue.length > 0) {
                    params.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : null);
                }
            }

            historyToken = historyToken.substring(0, paramsIndex);
        }
        HistoryToken historyTokenEnum;
        try {
            historyTokenEnum = HistoryToken.valueOf(historyToken.toLowerCase().trim());
        } catch (IllegalArgumentException e) {
            historyTokenEnum = HistoryToken.start;
        }

        final StateCommand stateCommand = historyTokenEnum.getStateCommand();
        stateCommand.execute(params);
    }
}
