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

import com.google.gwt.user.client.History;

import java.util.Map;

import org.wannatrak.client.HistoryToken;
import org.wannatrak.client.HistoryTokenParam;
import org.wannatrak.client.state.param.SetNewPassHistoryTokenParam;

/**
 * Created 26.04.2009 16:08:02
 *
 * @author Sergey Grachev
 */
public class SetNewPassCommand extends AbstractStateParamsHandlerCommand {

    @Override
    public void executeForStartState(Map<String, String> params) {
        getMediator().showSetNewPass();
        handleParams(params);
    }

    @Override
    public void executeForSetNewPassState(Map<String, String> params) {
        History.newItem(HistoryToken.set_new_password.toString());
    }

    protected HistoryTokenParam getHistoryTokenParam(String key) {
        return SetNewPassHistoryTokenParam.valueOf(key);
    }
}
