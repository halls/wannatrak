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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import org.wannatrak.client.*;
import org.wannatrak.client.register.RegisterService;

/**
 * Created 26.04.2009 15:22:47
 *
 * @author Sergey Grachev
 */
public class SetNewPassController extends AbstractInputController<SetNewPassWidget, String> implements CaptchaController {

    private TextBox passwordTextBox;
    private HTML passwordComment;

    private TextBox repeatPasswordTextBox;
    private HTML repeatPasswordComment;

    private String userHash;

    public SetNewPassController(Mediator mediator) {
        super(mediator);

        new SetNewPassHistoryTokenParamHandler(this);
    }

    public boolean isInitialized() {
        return passwordTextBox != null
                && passwordComment != null
                && repeatPasswordTextBox != null
                && repeatPasswordComment != null
                && userHash != null;
    }

    public void setPasswordTextBox(TextBox passwordTextBox) {
        this.passwordTextBox = passwordTextBox;
    }

    public void setPasswordComment(HTML passwordComment) {
        this.passwordComment = passwordComment;
    }

    public void setRepeatPasswordTextBox(TextBox passwordRepeatTextBox) {
        this.repeatPasswordTextBox = passwordRepeatTextBox;
    }

    public void setRepeatPasswordComment(HTML repeatPasswordComment) {
        this.repeatPasswordComment = repeatPasswordComment;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }

    protected boolean validateInput(boolean valid) {
        valid = validateRepeatPasswordInput(valid);

        valid = validatePasswordInput(valid);

        return valid;
    }

    protected void _clearInput() {
        passwordTextBox.setText("");
        repeatPasswordTextBox.setText("");
    }

    protected void callServer() {
        RegisterService.App.getInstance().setNewPassword(
                userHash,
                passwordTextBox.getText(),
                captchaTextBox.getText(),
                serverRequest.getAsyncCallback()
        );
    }

    protected void handleSuccess(String result) {
        mediator.loginAfterRegister(result);
    }

    private boolean validateRepeatPasswordInput(boolean valid) {
        if ("".equals(repeatPasswordTextBox.getText().trim())) {
            valid = false;
            repeatPasswordComment.setHTML(stringConstants.toBeFilledWarning());
            repeatPasswordComment.addStyleDependentName(WARNING_COMMENT_STYLE);
            repeatPasswordComment.setVisible(true);
            repeatPasswordTextBox.setFocus(true);
        } else {
            repeatPasswordComment.setVisible(false);
        }
        return valid;
    }

    private boolean validatePasswordInput(boolean valid) {
        if ("".equals(passwordTextBox.getText().trim())) {
            valid = false;
            showPasswordWarning(stringConstants.toBeFilledWarning());
        } else {
            if (!passwordTextBox.getText().equals(repeatPasswordTextBox.getText())) {
                valid = false;
                showPasswordWarning(stringConstants.passwordsMismatch());
            } else {
                passwordComment.setVisible(false);
            }
        }
        return valid;
    }

    private void showPasswordWarning(String msg) {
        passwordComment.setHTML(msg);
        passwordComment.addStyleDependentName(WARNING_COMMENT_STYLE);
        passwordComment.setVisible(true);
        passwordTextBox.setFocus(true);
    }
}
