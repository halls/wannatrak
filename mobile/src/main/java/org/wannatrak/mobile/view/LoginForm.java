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
 * 16.08.2008 16:20:36
 */
package org.wannatrak.mobile.view;

import org.wannatrak.mobile.controller.Controller;
import org.wannatrak.mobile.controller.exception.LoginException;
import org.wannatrak.mobile.controller.exception.ServerException;
import org.wannatrak.mobile.MessagesBundle;
import org.wannatrak.mobile.Executor;

import javax.microedition.lcdui.*;
import java.io.IOException;

public class LoginForm extends Form implements CommandListener {
    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.view.LoginForm");

    private Controller controller;

    private TextField loginField = new TextField(messagesBundle.get("username") + " ", "", 255, TextField.NON_PREDICTIVE);
    private TextField passField = new TextField(messagesBundle.get("password") + " ", "", 255, TextField.PASSWORD);
    private Command authCommand = new Command(messagesBundle.get("login"), Command.OK, 1);
    private Command exitCommand = new Command(messagesBundle.get("exit"), Command.EXIT, 2);

    public LoginForm(Controller controller) {
        super(messagesBundle.get("title"));

        this.controller = controller;
        addCommand(authCommand);
        addCommand(exitCommand);

        loginField.setString(controller.getLogin());
        passField.setString(controller.getPassword());

        append(loginField);
        append(passField);
        setCommandListener(this);

        controller.setLoginForm(this);
    }

    public void reinit() {
        addCommand(authCommand);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (exitCommand.equals(command)) {
            controller.logoutAndExit();
        } else if (authCommand.equals(command)) {
            removeCommand(authCommand);

            final Executor executor = new Executor() {
                private boolean stopped;

                public void execute() {
                    final String login = loginField.getString();
                    final String password = passField.getString();

                    try {
                        final String key;
                        if (controller.hasDeviceId()) {
                            controller.setLogin(login);
                            controller.setPassword(password);
                            key = controller.unlink();
                            controller.setDeviceId("");
                        } else {
                            key = controller.login(login, password);
                            controller.setLogin(login);
                            controller.setPassword(password);
                        }

                        if (!stopped) {
                            controller.setDeviceKey(key);
                            if (!controller.hasDeviceAddress()) {
                                controller.showConnectExternalDeviceForm();
                            } else {
                                controller.showCreateSubjectForm();
                            }
                        } else {
                            showLoginForm();
                        }
                    } catch (LoginException e) {
                        controller.showErrorAlert(messagesBundle.get("wrongUsernameOrPassAlert"), LoginForm.this);
                    } catch (IOException e) {
                        controller.showErrorAlert(e, LoginForm.this);
                    } catch (ServerException e) {
                        controller.showErrorAlert(e.getMessage(), LoginForm.this);
                    }
                }

                public void stop() {
                    stopped = true;
                }
            };

            controller.showLoading(messagesBundle.get("waiting"), messagesBundle.get("cancel"), executor);
            controller.executeAsynch(executor);
        } else {
            showLoginForm();
        }
    }

    private void showLoginForm() {
        controller.showLoginForm();
    }
}
