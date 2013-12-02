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
import org.wannatrak.mobile.controller.exception.ServerException;
import org.wannatrak.mobile.controller.exception.WrongDeviceKeyException;
import org.wannatrak.mobile.WannatrakMidlet;
import org.wannatrak.mobile.MessagesBundle;
import org.wannatrak.mobile.Executor;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.*;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public class CreateSubjectForm extends Form implements CommandListener {
    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.view.CreateSubjectForm");

    private Controller controller;

    private TextField nameField = new TextField(messagesBundle.get("name") + " ", "", 255, TextField.ANY);
    private Command createCommand = new Command(messagesBundle.get("create"), Command.OK, 1);
    private Command selectCommand = new Command(messagesBundle.get("select"), Command.EXIT, 2);

    public CreateSubjectForm(Controller controller) {
        super(messagesBundle.get("title"));

        this.controller = controller;
        revertControls();

        nameField.setString(controller.getLogin());

        append(nameField);
        setCommandListener(this);

        controller.setCreateSubjectForm(this);
    }

    public void reinit() {
        revertControls();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (selectCommand.equals(command)) {
            controller.showUnlinkedSubjectsList();
        } else if (createCommand.equals(command)) {
            final String name = nameField.getString();
            if (name.trim().length() == 0) {
                controller.showErrorAlert(messagesBundle.get("emptyNameAlert"));
                return;
            }

            removeCommand(createCommand);
            removeCommand(selectCommand);

            final Executor executor = new Executor() {

                public void execute() {
                    try {
                        final String deviceId = createSubject(name);
                        controller.setDeviceId(deviceId);
                        controller.setSubjectName(name);
                        controller.showTrakForm();
                    } catch (IOException e) {
                        controller.showErrorAlert(e, CreateSubjectForm.this);
                    } catch (ServerException e) {
                        controller.showErrorAlert(e.getMessage(), CreateSubjectForm.this);
                    } catch (WrongDeviceKeyException e) {
                        controller.showLoginForm();
                    }
                }

                public void stop() {
                }
            };

            controller.showLoading(messagesBundle.get("waiting"));
            controller.executeAsynch(executor);
        } else {
            showCreateSubjectForm();
        }
    }

    private void showCreateSubjectForm() {
        controller.showCreateSubjectForm();
    }

    private void revertControls() {
        addCommand(createCommand);
        addCommand(selectCommand);
    }

    private String createSubject(String name) throws IOException, WrongDeviceKeyException, ServerException {
        HttpConnection conn = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            conn = (HttpConnection) Connector.open(
                    WannatrakMidlet.URL
                            + "subjects/create?deviceId="
                            + controller.getDeviceKey()
            );
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");
            dos = conn.openDataOutputStream();
            dos.writeUTF(name);

            final int responseCode = conn.getResponseCode();
            if (responseCode == HttpConnection.HTTP_BAD_REQUEST) {
                throw new WrongDeviceKeyException();
            } else if (responseCode != HttpConnection.HTTP_OK) {
                throw new ServerException(responseCode);
            }

            dis = conn.openDataInputStream();
            controller.setSendPause(dis.readInt());
            controller.setSavePause(dis.readInt());

            return dis.readUTF();
        } finally {
            try {
                if(dis != null) {
                    dis.close();
                }
                if(dos != null) {
                    dos.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }   

}
