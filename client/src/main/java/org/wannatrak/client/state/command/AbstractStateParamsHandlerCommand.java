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

package org.wannatrak.client.state.command;

import org.wannatrak.client.HistoryTokenParam;

import java.util.Map;

/**
 * Created 31.05.2009 23:33:16
 *
 * @author Andrey Khalzov
 */
public abstract class AbstractStateParamsHandlerCommand extends AbstractStateCommand {

    protected abstract HistoryTokenParam getHistoryTokenParam(String key);

    protected void handleParams(Map<String, String> params) {
        for (String key : params.keySet()) {
            try {
                final HistoryTokenParam historyTokenParam = getHistoryTokenParam(key);
                historyTokenParam.getHistoryTokenParamHandler().applyParams(params.get(key));
            } catch (IllegalArgumentException e) {
            }
        }
    }
}
