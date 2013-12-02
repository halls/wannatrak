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
 * 28.12.2008 23:26:35
 */
package org.wannatrak.client.register;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.rpc.StatusCodeException;
import org.wannatrak.client.*;

public class RegisterController extends AbstractInputController<RegisterWidget, Void> implements CaptchaController {
    private final ServerRequest<Boolean> checkLoginRequest;

    private TextBox loginTextBox;
    
    private TextBox passwordTextBox;
    private HTML passwordComment;

    private TextBox repeatPasswordTextBox;
    private HTML repeatPasswordComment;

    private TextBox emailTextBox;
    private HTML emailComment;

    private HTML checkLoginResultLabel;
    private String checkingLogin;

    private boolean executing = false;

    public RegisterController(Mediator mediator) {
        super(mediator);

        checkingLogin = "";

        checkLoginRequest = createCheckLoginRequest();
    }

    public boolean isInitialized() {
        return loginTextBox != null
                && passwordTextBox != null
                && passwordComment != null
                && repeatPasswordTextBox != null
                && repeatPasswordComment != null
                && emailTextBox != null
                && emailComment != null
                && checkLoginResultLabel != null;
    }

    public String getCheckingLogin() {
        return checkingLogin;
    }

    public void setLoginTextBox(TextBox loginTextBox) {
        this.loginTextBox = loginTextBox;
    }

    public void setPasswordTextBox(TextBox passwordTextBox) {
        this.passwordTextBox = passwordTextBox;
    }

    public void setPasswordComment(HTML passwordComment) {
        this.passwordComment = passwordComment;
    }

    public void setRepeatPasswordTextBox(TextBox repeatPasswordTextBox) {
        this.repeatPasswordTextBox = repeatPasswordTextBox;
    }

    public void setRepeatPasswordComment(HTML repeatPasswordComment) {
        this.repeatPasswordComment = repeatPasswordComment;
    }

    public void setEmailTextBox(TextBox emailTextBox) {
        this.emailTextBox = emailTextBox;
    }

    public void setEmailComment(HTML emailComment) {
        this.emailComment = emailComment;
    }

    public void setCheckLoginResultLabel(HTML checkLoginResultLabel) {
        this.checkLoginResultLabel = checkLoginResultLabel;
    }

    public void execute() {
        executing = true;
        checkLogin();
    }

    public void checkLogin() {
        checkIsInitialized();

        if ("".equals(loginTextBox.getText().trim())) {
            showWarningCheckLoginResult(stringConstants.toBeFilledWarning());
            return;
        }

        checkLoginRequest.execute();
    }

    public void stopExecuting() {
        if (executing) {
            executing = false;
            super.stopExecuting();
        }
    }

    public void showWaitingCheckLoginStatus() {
        checkIsInitialized();

        hideCheckLoginResult();

        checkLoginResultLabel.setVisible(true);
        checkLoginResultLabel.addStyleDependentName("waiting");

        checkLoginResultLabel.setHTML(stringConstants.checkLoginWaiting());
    }

    public void showWarningCheckLoginResult(String msg) {
        checkIsInitialized();

        hideCheckLoginResult();

        checkLoginResultLabel.setVisible(true);
        checkLoginResultLabel.addStyleDependentName(WARNING_COMMENT_STYLE);

        checkLoginResultLabel.setHTML(msg);
    }

    public void showOkCheckLoginResult() {
        checkIsInitialized();

        hideCheckLoginResult();
        checkLoginResultLabel.setVisible(true);

        checkLoginResultLabel.setHTML(Messages.MessagesSingleton.getInstance().loginAvailable(checkingLogin));
    }

    public void hideCheckLoginResult() {
        checkIsInitialized();

        checkLoginResultLabel.removeStyleDependentName(WARNING_COMMENT_STYLE);
        checkLoginResultLabel.removeStyleDependentName("waiting");
        checkLoginResultLabel.setVisible(false);
    }

    protected boolean validateInput(boolean valid) {
        valid = validateEmailInput(valid);

        valid = validateRepeatPasswordInput(valid);

        valid = validatePasswordInput(valid);

        valid = validateLoginInput(valid);

        return valid;
    }

    protected void _clearInput() {
        loginTextBox.setText("");
        passwordTextBox.setText("");
        repeatPasswordTextBox.setText("");
        emailTextBox.setText("");

        hideCheckLoginResult();
    }

    protected void callServer() {
        RegisterService.App.getInstance().register(
                loginTextBox.getText(),
                passwordTextBox.getText(),
                emailTextBox.getText(),
                captchaTextBox.getText(),
                serverRequest.getAsyncCallback()
        );
    }

    protected void handleSuccess(Void result) {
        executing = false;

        mediator.showHowtoSetupAfterRegister();
    }

    private boolean validateEmailInput(boolean valid) {
        final String email = emailTextBox.getText().trim().toLowerCase();
        if ("".equals(email)) {
            valid = false;
            emailComment.setHTML(stringConstants.toBeFilledWarning());
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

    private boolean validateLoginInput(boolean valid) {
        if ("".equals(loginTextBox.getText().trim())) {
            valid = false;
            showWarningCheckLoginResult(stringConstants.toBeFilledWarning());
            loginTextBox.setFocus(true);
        } else {
            hideCheckLoginResult();
        }
        return valid;
    }

    private void showPasswordWarning(String msg) {
        passwordComment.setHTML(msg);
        passwordComment.addStyleDependentName(WARNING_COMMENT_STYLE);
        passwordComment.setVisible(true);
        passwordTextBox.setFocus(true);
    }

    private ServerRequest<Boolean> createCheckLoginRequest() {
        return new ServerRequest<Boolean>(mediator) {
            @Override
            protected void request() {
                showWaitingCheckLoginStatus();

                checkingLogin = loginTextBox.getText().trim();

                RegisterService.App.getInstance().isLoginAvailable(checkingLogin, getAsyncCallback());
            }

            @Override
            protected void handleSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    if (executing) {
                        serverRequest.execute();
                    }
                    showOkCheckLoginResult();
                } else {
                    stopExecuting();
                    showWarningCheckLoginResult(
                            Messages.MessagesSingleton.getInstance().loginNotAvailable(checkingLogin)
                    );
                    loginTextBox.setFocus(true);
                }
            }

            @Override
            protected void handleTimeout() {
                stopExecuting();

                showWarningCheckLoginResult(stringConstants.timeoutExceeded());
            }

            @Override
            protected void handleStatusCodeException(Throwable caught) {
                stopExecuting();

                showWarningCheckLoginResult(
                        Messages.MessagesSingleton.getInstance().serverErrorWithCode(
                                String.valueOf(((StatusCodeException) caught).getStatusCode())
                        )
                );
            }

            @Override
            protected void handleUncheckedException(Throwable caught) {
                stopExecuting();

                showWarningCheckLoginResult(stringConstants.serverError());
            }

            @Override
            protected void handleUnknownException(Throwable caught) {
                stopExecuting();

                showWarningCheckLoginResult(stringConstants.unknownError());
            }
        };
    }
}
