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
 * 22.12.2008 18:29:57
 */
package org.wannatrak.client.setnewpass;

import com.google.gwt.user.client.ui.*;
import org.wannatrak.client.*;

public class RequestSetNewPassWidget extends AbstractInputWidget<RequestSetNewPassController, Void> {

    public RequestSetNewPassWidget(Mediator mediator, RequestSetNewPassController requestSetNewPassController) {
        super(mediator, requestSetNewPassController);
    }

    protected HistoryToken getThisHistoryToken() {
        return HistoryToken.request_set_new_password;
    }

    protected HistoryToken getSubmitHistoryToken() {
        return HistoryToken.set_new_password_info;
    }

    protected int getNumOfRows() {
        return 9;
    }

    protected Label getHeaderLabel() {
        final Label header = new Label(stringConstants.requestSetNewPassLabel(), false);
        header.setStylePrimaryName("requestSetNewPassLabel");
        return header;
    }

    protected Button createSubmitButton() {
        final Button continueButton = new Button(stringConstants.continueSetNewPass());
        continueButton.setStylePrimaryName("continueButton");
        return continueButton;
    }

    protected void fillInputGrid(InputGrid inputGrid) {
        createLoginTextBox(inputGrid);
        createEmailTextBox(inputGrid);
    }

    private void createLoginTextBox(InputGrid inputGrid) {
        final TextBox loginTextBox
                = inputGrid.addLabeledTextBoxWithComment(stringConstants.loginLabel(), "", COMMENT_STYLE);
        loginTextBox.addKeyboardListener(getKeyboardListener());
        inputController.setLoginTextBox(loginTextBox);
        inputController.setLoginComment(inputGrid.getComment(loginTextBox));
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
}
