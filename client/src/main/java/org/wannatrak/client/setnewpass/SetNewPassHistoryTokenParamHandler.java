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

package org.wannatrak.client.setnewpass;

import org.wannatrak.client.HistoryTokenParamHandler;
import org.wannatrak.client.HistoryTokenParam;
import org.wannatrak.client.state.param.SetNewPassHistoryTokenParam;

/**
 * Created 01.06.2009 0:07:10
 *
 * @author Andrey Khalzov
 */
public class SetNewPassHistoryTokenParamHandler extends HistoryTokenParamHandler {

    private SetNewPassController setNewPassController;

    public SetNewPassHistoryTokenParamHandler(SetNewPassController setNewPassController) {
        this.setNewPassController = setNewPassController;
    }

    public HistoryTokenParam getHistoryTokenParam() {
        return SetNewPassHistoryTokenParam.request_id;
    }

    public void applyParams(String params) {
        setNewPassController.setUserHash(params);
    }
}
