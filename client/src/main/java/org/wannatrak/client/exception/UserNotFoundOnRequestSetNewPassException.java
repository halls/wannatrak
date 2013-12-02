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

package org.wannatrak.client.exception;

import org.wannatrak.client.Mediator;
import org.wannatrak.client.StringConstants;
import org.wannatrak.client.setnewpass.RequestSetNewPassController;
import com.google.gwt.user.client.DeferredCommand;

/**
 * Created 09.04.2009 20:54:42
 *
 * @author Sergey Grachev
 */
public class UserNotFoundOnRequestSetNewPassException extends CheckedException{
    public void throwTo(Mediator mediator) {
        final RequestSetNewPassController requestSetNewPassController = mediator.getRequestSetNewPassController();
        requestSetNewPassController.stopExecuting();
        DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
            public void execute() {
                requestSetNewPassController.showWarning(
                    StringConstants.StringConstantsSingleton.getInstance().userNotFoundByLoginAndEmail()
                );
            }
        });
    }
}
