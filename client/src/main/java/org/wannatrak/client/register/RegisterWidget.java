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
 * 25.12.2008 2:31:39
 */
package org.wannatrak.client.register;

import com.google.gwt.user.client.ui.*;
import org.wannatrak.client.*;

public class RegisterWidget extends AbstractInputWidget<RegisterController, Void> {

    public RegisterWidget(Mediator mediator, RegisterController registerController) {
        super(mediator, registerController);
    }

    protected HistoryToken getThisHistoryToken() {
        return HistoryToken.registration;
    }

    protected HistoryToken getSubmitHistoryToken() {
        return HistoryToken.how_to_setup_after_register;
    }

    protected int getNumOfRows() {
        return 14;
    }

    protected Label getHeaderLabel() {
        final Label header = new Label(stringConstants.registrationLabel());
        header.setStylePrimaryName("registrationLabel");
        return header;
    }

    protected Button createSubmitButton() {
        final Button registerButton = new Button(stringConstants.registerButton());
        registerButton.setStylePrimaryName("registerButton");
        return registerButton;
    }

    protected void fillInputGrid(InputGrid inputGrid) {
        final HTML checkLoginResultLabel = createCheckLoginResultLabel(inputController);
        final Button checkLoginButton = createCheckLoginButton();

        createLoginTextBox(inputGrid);
        inputGrid.addWidget(checkLoginButton);
        inputGrid.addWidget(checkLoginResultLabel);

        createPasswordTextBox(inputGrid);

        createRepeatPasswordTextBox(inputGrid);

        createEmailTextBox(inputGrid);
    }

    @Override
    protected String getCaptchaHeader() {
        return stringConstants.captchaRegister();
    }

    private HTML createCheckLoginResultLabel(RegisterController registerController) {
        final HTML checkLoginResultLabel = new HTML();
        checkLoginResultLabel.setStylePrimaryName("checkLoginResultLabel");
        checkLoginResultLabel.setVisible(false);
        registerController.setCheckLoginResultLabel(checkLoginResultLabel);
        return checkLoginResultLabel;
    }

    private Button createCheckLoginButton() {
        final Button checkLoginButton = new Button(stringConstants.checkLogin());
        checkLoginButton.setStylePrimaryName("checkLoginButton");
        checkLoginButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                RegisterWidget.this.inputController.checkLogin();
            }
        });
        return checkLoginButton;
    }

    private void createEmailTextBox(InputGrid inputGrid) {
        final TextBox emailTextBox = inputGrid.addLabeledTextBoxWithComment(
                stringConstants.emailLabel(),
                stringConstants.emailComment(),
                COMMENT_STYLE
        );
        emailTextBox.addKeyboardListener(getKeyboardListener());
        inputController.setEmailTextBox(emailTextBox);
        inputController.setEmailComment(inputGrid.getComment(emailTextBox));
    }

    private void createRepeatPasswordTextBox(InputGrid inputGrid) {
        final TextBox repeatPasswordTextBox = inputGrid.addLabeledTextBoxWithComment(
                stringConstants.repeatPasswordLabel(),
                true,
                stringConstants.repeatPasswordComment(),
                COMMENT_STYLE
        );
        repeatPasswordTextBox.addKeyboardListener(getKeyboardListener());
        inputController.setRepeatPasswordTextBox(repeatPasswordTextBox);
        inputController.setRepeatPasswordComment(inputGrid.getComment(repeatPasswordTextBox));
    }

    private void createPasswordTextBox(InputGrid inputGrid) {
        final TextBox passwordTextBox = inputGrid.addLabeledTextBoxWithComment(
                stringConstants.passwordLabel(),
                true,
                stringConstants.passwordComment(),
                COMMENT_STYLE
        );
        passwordTextBox.addKeyboardListener(getKeyboardListener());
        inputController.setPasswordTextBox(passwordTextBox);
        inputController.setPasswordComment(inputGrid.getComment(passwordTextBox));
    }

    private void createLoginTextBox(InputGrid inputGrid) {
        final TextBox loginTextBox = inputGrid.addLabeledTextBox(stringConstants.loginLabel());
        loginTextBox.addKeyboardListener(getKeyboardListener());
        inputController.setLoginTextBox(loginTextBox);
    }
}
