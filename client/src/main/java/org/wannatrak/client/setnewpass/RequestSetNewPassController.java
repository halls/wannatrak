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
 * Created 14.04.2009 20:36:04
 *
 * @author Sergey Grachev
 */
public class RequestSetNewPassController
        extends AbstractInputController<RequestSetNewPassWidget, Void>
        implements CaptchaController
{

    private TextBox loginTextBox;
    private HTML loginComment;

    private TextBox emailTextBox;
    private HTML emailComment;

    public RequestSetNewPassController(Mediator mediator) {
        super(mediator);
    }

    public boolean isInitialized() {
        return loginTextBox != null
                && loginComment != null
                && emailTextBox != null
                && emailComment != null;
    }

    public void setLogin(String login) {
        loginTextBox.setText(login);
    }

    public void setLoginTextBox(TextBox loginTextBox) {
        this.loginTextBox = loginTextBox;
    }

    public void setEmailTextBox(TextBox passwordTextBox) {
        this.emailTextBox = passwordTextBox;
    }

    public TextBox getCaptchaTextBox() {
        return captchaTextBox;
    }

    public void setCaptchaTextBox(TextBox captchaTextBox) {
        this.captchaTextBox = captchaTextBox;
    }

    public void setLoginComment(HTML loginComment) {
        this.loginComment = loginComment;
    }

    public void setEmailComment(HTML emailComment) {
        this.emailComment = emailComment;
    }

    protected boolean validateInput(boolean valid) {
        valid = validateEmailInput(valid);

        valid = validateLoginInput(valid);

        return valid;
    }

    protected void _clearInput() {
        loginTextBox.setText("");
        emailTextBox.setText("");
    }

    protected void callServer() {
        RegisterService.App.getInstance().requestSetNewPassword(
                loginTextBox.getText(),
                emailTextBox.getText(),
                captchaTextBox.getText(),
                serverRequest.getAsyncCallback()
        );
    }

    protected void handleSuccess(Void result) {
        mediator.showSetNewPassInfo(emailTextBox.getText());
    }

    private boolean validateEmailInput(boolean valid) {
        final String email = emailTextBox.getText().trim().toLowerCase();
        if ("".equals(email)) {
            valid = false;
            emailComment.setHTML(StringConstants.StringConstantsSingleton.getInstance().toBeFilledWarning());
            emailComment.addStyleDependentName(WARNING_COMMENT_STYLE);
            emailComment.setVisible(true);
            emailTextBox.setFocus(true);
        } else {
            if (!email.matches(EMAIL_REGEX)) {
                valid = false;
                emailComment.setHTML(stringConstants.wrongEmail());
                emailComment.addStyleDependentName(WARNING_COMMENT_STYLE);
                emailComment.setVisible(true);
                emailTextBox.setFocus(true);
            } else {
                emailComment.setVisible(false);
            }
        }
        return valid;
    }

    private boolean validateLoginInput(boolean valid) {
        if ("".equals(loginTextBox.getText().trim())) {
            valid = false;
            loginComment.setHTML(stringConstants.toBeFilledWarning());
            loginComment.addStyleDependentName(WARNING_COMMENT_STYLE);
            loginComment.setVisible(true);
            loginTextBox.setFocus(true);
        } else {
            loginComment.setVisible(false);
        }
        return valid;
    }
}
