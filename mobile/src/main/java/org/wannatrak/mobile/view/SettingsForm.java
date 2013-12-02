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
 * 17.03.2009
 */
package org.wannatrak.mobile.view;

import org.wannatrak.mobile.controller.Controller;
import org.wannatrak.mobile.controller.exception.WrongDeviceKeyException;
import org.wannatrak.mobile.controller.exception.ServerException;
import org.wannatrak.mobile.controller.exception.SubjectExistsException;
import org.wannatrak.mobile.WannatrakMidlet;
import org.wannatrak.mobile.MessagesBundle;
import org.wannatrak.mobile.Executor;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.*;
import java.io.IOException;
import java.io.DataOutputStream;

public class SettingsForm extends Form implements CommandListener {
    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.view.SettingsForm");

    private Controller controller;

    private TextField nameField = new TextField(messagesBundle.get("subjectName") + " ", "", 255, TextField.ANY);
    private TextField sendPeriodField = new TextField(messagesBundle.get("sendPeriod") + " ", "5", 5, TextField.NUMERIC);
    private TextField savePeriodField = new TextField(messagesBundle.get("savePeriod") + " ", "5", 6, TextField.NUMERIC);
    private TextField pointsInPathField = new TextField(messagesBundle.get("pointsInPath") + " ", "50", 7, TextField.NUMERIC);
    private Command saveCommand = new Command(messagesBundle.get("save"), Command.OK, 1);
    private Command cancelCommand = new Command(messagesBundle.get("cancel"), Command.CANCEL, 2);

    public SettingsForm(Controller controller) {
        super(messagesBundle.get("title"));

        this.controller = controller;

        append(nameField);

        append(sendPeriodField);
        append(messagesBundle.get("inMins"));

        append(savePeriodField);
        append(messagesBundle.get("inSecs"));

        append(pointsInPathField);
        append(messagesBundle.get("points"));

        init();

        setCommandListener(this);
        controller.setSettingsForm(this);
    }

    public void init() {
        nameField.setString(controller.getSubjectName());
        sendPeriodField.setString(Integer.toString(controller.getSendPause()));
        savePeriodField.setString(Integer.toString(controller.getSavePause()));
        pointsInPathField.setString(Integer.toString(controller.getNumOfPointsInPath()));
        addControls();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (cancelCommand.equals(command)) {
            controller.showTrakForm();
        } else if (saveCommand.equals(command)) {
            final String name = nameField.getString();
            if (name.trim().length() == 0) {
                controller.showErrorAlert(messagesBundle.get("emptyNameAlert"));
                return;
            }
            
            final int savePeriod;
            try {
                savePeriod = Integer.parseInt(savePeriodField.getString());
                if (savePeriod < 1 || savePeriod > Controller.MAX_SAVE_PAUSE) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                controller.showErrorAlert(messagesBundle.get("savePeriodAlert") + " " + Controller.MAX_SAVE_PAUSE);
                return;
            }

            final int sendPeriod;
            try {
                sendPeriod = Integer.parseInt(sendPeriodField.getString());
                if (sendPeriod < 1 || sendPeriod > Controller.MAX_SEND_PAUSE) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                controller.showErrorAlert(messagesBundle.get("sendPeriodAlert") + " " + Controller.MAX_SEND_PAUSE);
                return;
            }

            final int pointsInPath;
            try {
                pointsInPath = Integer.parseInt(pointsInPathField.getString());
                if (pointsInPath < 1 || pointsInPath > Controller.MAX_POINTS_IN_PATH) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                controller.showErrorAlert(messagesBundle.get("pointsInPathAlert") + " " + Controller.MAX_POINTS_IN_PATH);
                return;
            }

            removeCommand(saveCommand);
            removeCommand(cancelCommand);
            final boolean wasStarted = controller.isStarted();
            controller.stop();

            final Executor executor = new Executor() {
                public void execute() {
                    try {
                        saveSettings(name, savePeriod, sendPeriod);
                        controller.setSubjectName(name);
                        controller.setSavePause(savePeriod);
                        controller.setSendPause(sendPeriod);
                        controller.setNumOfPointsInPath(pointsInPath);

                        if (wasStarted) {
                            controller.startTrak();
                        }
                        controller.showTrakForm();
                    } catch (WrongDeviceKeyException e) {
                        controller.showLoginForm();
                    } catch (ServerException e) {
                        controller.showErrorAlert(e.getMessage(), SettingsForm.this);
                    } catch (IOException e) {
                        controller.showErrorAlert(e, SettingsForm.this);
                    } catch (SubjectExistsException e) {
                        controller.showErrorAlert(messagesBundle.get("subjectAlreadyExistsAlert"), SettingsForm.this);
                    }
                }

                public void stop() {
                }
            };
            controller.showLoading(messagesBundle.get("waiting"));
            controller.executeAsynch(executor);
        } else {
            showSettingsForm();
        }
    }

    private void showSettingsForm() {
        controller.showSettingsForm();
    }

    private void addControls() {
        addCommand(saveCommand);
        addCommand(cancelCommand);
    }

    private void saveSettings(
            String name,
            int savePeriod,
            int sendPeriod
    ) throws WrongDeviceKeyException, ServerException, IOException, SubjectExistsException {
        HttpConnection conn = null;
        DataOutputStream dos = null;
        try {
            conn = (HttpConnection) Connector.open(WannatrakMidlet.URL + "settings/save");

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");

            dos = conn.openDataOutputStream();
            dos.writeUTF(controller.getDeviceId());
            dos.writeUTF(name);
            dos.writeInt(savePeriod);
            dos.writeInt(sendPeriod);

            final int responseCode = conn.getResponseCode();
            if (responseCode == HttpConnection.HTTP_BAD_REQUEST) {
                throw new WrongDeviceKeyException();
            } else if (responseCode == HttpConnection.HTTP_EXPECT_FAILED) {
                throw new SubjectExistsException();
            } else if (responseCode != HttpConnection.HTTP_OK) {
                throw new ServerException(responseCode);
            }
        } finally {
            try {
                if(dos != null) {
                    dos.close();
                }
                if (conn!=null) {
                    conn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
