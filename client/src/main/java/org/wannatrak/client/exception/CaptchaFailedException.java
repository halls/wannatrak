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
 * 29.12.2008 4:30:02
 */
package org.wannatrak.client.exception;

import org.wannatrak.client.Mediator;
import org.wannatrak.client.StringConstants;
import org.wannatrak.client.CaptchaController;
import com.google.gwt.user.client.DeferredCommand;

public class CaptchaFailedException extends CheckedException {

    public void throwTo(Mediator mediator) {
        final CaptchaController captchaController = mediator.getCaptchaController();
        captchaController.stopExecuting();
        DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
            public void execute() {
                captchaController.showWarning(
                        StringConstants.StringConstantsSingleton.getInstance().captchaFailed()
                );
                captchaController.setFocusToCaptcha();
            }
        });
    }
}
