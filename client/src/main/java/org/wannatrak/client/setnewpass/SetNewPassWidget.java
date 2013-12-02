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

import com.google.gwt.user.client.ui.*;
import org.wannatrak.client.*;

/**
 * Created 26.04.2009 15:22:23
 *
 * @author Sergey Grachev
 */
public class SetNewPassWidget extends AbstractInputWidget<SetNewPassController, String> {

    public SetNewPassWidget(Mediator mediator, SetNewPassController setNewPassController) {
        super(mediator, setNewPassController);
    }

    protected HistoryToken getThisHistoryToken() {
        return HistoryToken.set_new_password;
    }

    protected HistoryToken getSubmitHistoryToken() {
        return HistoryToken.logged_in;
    }

    protected int getNumOfRows() {
        return 9;
    }

    protected Label getHeaderLabel() {
        final Label header = new Label(stringConstants.setNewPassHeader(), false);
        header.setStylePrimaryName("setNewPassLabel");
        return header;
    }

    protected Button createSubmitButton() {
        final Button submitButton = new Button(stringConstants.setNewPassSumbit());
        submitButton.setStylePrimaryName("setNewPassButton");
        return submitButton;
    }

    protected void fillInputGrid(InputGrid inputGrid) {
        createPasswordTextBox(inputGrid);
        createRepeatPasswordTextBox(inputGrid);
    }

    private void createRepeatPasswordTextBox(InputGrid inputGrid) {
        final TextBox repeatPasswordTextBox = inputGrid.addLabeledTextBoxWithComment(
                stringConstants.repeatNewPasswordLabel(),
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
                stringConstants.newPasswordLabel(),
                true,
                stringConstants.passwordComment(),
                COMMENT_STYLE
        );
        passwordTextBox.addKeyboardListener(getKeyboardListener());
        inputController.setPasswordTextBox(passwordTextBox);
        inputController.setPasswordComment(inputGrid.getComment(passwordTextBox));
    }
}
