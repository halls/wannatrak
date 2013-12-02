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
 * 15.12.2008 21:49:29
 */
package org.wannatrak.client.login;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.History;
import org.wannatrak.client.Mediator;
import org.wannatrak.client.InputGrid;
import org.wannatrak.client.StringConstants;
import org.wannatrak.client.HistoryToken;

public class LoginWidget extends VerticalPanel {

    protected final StringConstants stringConstants = StringConstants.StringConstantsSingleton.getInstance();

    private final Mediator mediator;
    private final InputGrid loginInputGrid;
    private final HTML errorLabel;
    private final HTML waitingLabel;

    private final String loginLabel;
    private final String passwordLabel;

    private final CheckBox keepMyLoginCheckBox;

    public LoginWidget(Mediator mediator) {
        this.mediator = mediator;
        mediator.setLoginWidget(this);

        setHorizontalAlignment(ALIGN_CENTER);

        errorLabel = new HTML();
        errorLabel.setStylePrimaryName("errorLabel");
        errorLabel.setVisible(false);

        waitingLabel = new HTML();
        waitingLabel.setStylePrimaryName("waitingLabel");
        waitingLabel.setVisible(false);

        loginLabel = StringConstants.StringConstantsSingleton.getInstance().loginLabel();
        passwordLabel = StringConstants.StringConstantsSingleton.getInstance().passwordLabel();

        keepMyLoginCheckBox = new CheckBox("&nbsp;" + stringConstants.keepMyLogin(), true);
        keepMyLoginCheckBox.setStylePrimaryName("keepMyLoginCheckBox");


        loginInputGrid = new InputGrid(3, 2, "loginInputLabelCell", "loginInputBoxCell", "loginInputBox");
        loginInputGrid.setCellSpacing(7);
        loginInputGrid.addLabeledTextBox(loginLabel, false).addKeyboardListener(new LoginKeyboardListener());
        loginInputGrid.addLabeledTextBox(passwordLabel, true).addKeyboardListener(new LoginKeyboardListener());
        loginInputGrid.addWidget(keepMyLoginCheckBox);

        final Button loginButton = new Button(StringConstants.StringConstantsSingleton.getInstance().loginButton());
        loginButton.setStylePrimaryName("loginButton");
        loginButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                History.newItem(HistoryToken.logged_in.toString());
            }
        });

        final Hyperlink remindPassLink = new Hyperlink(
                StringConstants.StringConstantsSingleton.getInstance().remindPass(),
                HistoryToken.request_set_new_password.toString()
        );
        remindPassLink.setStylePrimaryName("remindPass");

        final VerticalPanel loginPanel = new VerticalPanel();
        loginPanel.setHorizontalAlignment(ALIGN_CENTER);
        loginPanel.add(loginInputGrid);
        loginPanel.add(loginButton);
        loginPanel.add(remindPassLink);
        loginPanel.add(errorLabel);
        loginPanel.add(waitingLabel);
        loginPanel.setStylePrimaryName("loginPanel");
        loginPanel.setSpacing(4);

        final Hyperlink registerHyperlink = new Hyperlink(
                StringConstants.StringConstantsSingleton.getInstance().register(),
                HistoryToken.registration.toString()
        );
        registerHyperlink.setStylePrimaryName("registerHyperlink");

        add(loginPanel);

        add(registerHyperlink);

        final HTML copyrightLabel = new HTML(
                StringConstants.StringConstantsSingleton.getInstance().copyrightLabel(),
                false
        );
        copyrightLabel.setStylePrimaryName("tm-small");
        add(copyrightLabel);
    }

    public String getLogin() {
        return loginInputGrid.getInputValue(loginLabel);
    }

    public String getPassword() {
        return loginInputGrid.getInputValue(passwordLabel);
    }

    public Boolean getKeepMyLogin() {
        return keepMyLoginCheckBox.isChecked();
    }

    public void clearInput() {
        loginInputGrid.setInputValue(loginLabel, "");
        loginInputGrid.setInputValue(passwordLabel, "");
    }

    public void showError(String msg) {
        hideWaiting();

        errorLabel.setHTML(msg);
        errorLabel.setVisible(true);
    }

    public void hideError() {
        errorLabel.setVisible(false);
    }

    public void showWaiting(String msg) {
        hideError();

        waitingLabel.setHTML(msg);
        waitingLabel.setVisible(true);
    }

    public void hideWaiting() {
        waitingLabel.setVisible(false);
    }

    public void setFocus() {
        ((TextBox) loginInputGrid.getWidget(0, 1)).setFocus(true);
    }

    class LoginKeyboardListener extends KeyboardListenerAdapter {
        public void onKeyPress(Widget sender, char keyCode, int modifiers) {
            if (KeyboardListener.KEY_ENTER == keyCode) {
                History.newItem(HistoryToken.logged_in.toString());
            }
        }
    }
}
