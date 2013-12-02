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
import org.wannatrak.mobile.model.Subject;
import org.wannatrak.mobile.WannatrakMidlet;
import org.wannatrak.mobile.MessagesBundle;
import org.wannatrak.mobile.Executor;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.*;
import java.util.Vector;
import java.io.IOException;
import java.io.DataInputStream;

public class UnlinkedSubjectsList extends List implements CommandListener {

    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.view.UnlinkedSubjectsList");

    private Controller controller;
    private Command refreshCommand = new Command(messagesBundle.get("refresh"), Command.ITEM, 2);
    private Command selectCommand = new Command(messagesBundle.get("select"), Command.ITEM, 1);
    private Command createCommand = new Command(messagesBundle.get("create"), Command.EXIT, 3);

    private Vector subjects;

    public UnlinkedSubjectsList(Controller controller) {
        super(messagesBundle.get("title"), Choice.IMPLICIT);

        this.controller = controller;

        addCommand(createCommand);

        setCommandListener(this);
        controller.setUnlinkedSubjectsList(this);

        append(messagesBundle.get("loading"), null);

        fillList();
    }

    public void reinit() {
        addCommand(selectCommand);
        addCommand(createCommand);
        addCommand(refreshCommand);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (refreshCommand.equals(command)) {
            refresh();
        } else if (createCommand.equals(command)) {
            controller.showCreateSubjectForm();
        } else if (selectCommand.equals(command)) {
            final Subject subject = (Subject) subjects.elementAt(getSelectedIndex());

            removeCommand(selectCommand);
            removeCommand(createCommand);
            removeCommand(refreshCommand);

            final Executor executor = new Executor() {
                private boolean stopped;

                public void execute() {
                    final String deviceId;
                    try {
                        deviceId = linkSubject(subject.getId());
                        if (!stopped) {
                            controller.setDeviceId(deviceId);
                            controller.setDeviceKey(deviceId);
                            controller.setSubjectName(subject.getName());
                            controller.showTrakForm();
                        } else {
                            showUnlinkedSubjectsList();
                        }
                    } catch (WrongDeviceKeyException e) {
                        controller.showLoginForm();
                    } catch (ServerException e) {
                        controller.showErrorAlert(e.getMessage(), UnlinkedSubjectsList.this);
                    } catch (IOException e) {
                        controller.showErrorAlert(e, UnlinkedSubjectsList.this);
                    }
                }

                public void stop() {
                    stopped = true;
                }
            };

            controller.showLoading(messagesBundle.get("waiting"), messagesBundle.get("cancel"), executor);
            controller.executeAsynch(executor);
        } else {
            showUnlinkedSubjectsList();
        }
    }

    private void showUnlinkedSubjectsList() {
        controller.showUnlinkedSubjectsList();
    }

    private void refresh() {
        deleteAll();
        append(messagesBundle.get("loading"), null);
        setSelectCommand(null);
        removeCommand(refreshCommand);
        fillList();
    }

    private void fillList() {
        controller.executeAsynch(new Executor() {
            public void execute() {
                try {
                    subjects = getUnlinkedSubjects();
                    deleteAll();
                    if (subjects != null && !subjects.isEmpty()) {
                        for (int i = 0; i < subjects.size(); i++) {
                            final Subject subject = (Subject) subjects.elementAt(i);
                            append(subject.getName(), null);
                        }
                        setSelectCommand(selectCommand);
                    } else {
                        append(messagesBundle.get("noResults"), null);
                    }
                    addCommand(refreshCommand);
                } catch (ServerException e) {
                    controller.showErrorAlert(e.getMessage());
                    revertControlsWithEmptyList();
                } catch (WrongDeviceKeyException e) {
                    controller.showLoginForm();
                } catch (IOException e) {
                    controller.showErrorAlert(e);
                    revertControlsWithEmptyList();
                }
            }

            public void stop() {
            }
        });
    }

    private Vector getUnlinkedSubjects() throws ServerException, WrongDeviceKeyException, IOException {
        HttpConnection conn = null;
        DataInputStream dis = null;
        try {
            conn = (HttpConnection) Connector.open(
                    WannatrakMidlet.URL + "subjects/list?deviceId=" + controller.getDeviceKey()
            );
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/plain");

            final int responseCode = conn.getResponseCode();
            if (responseCode == HttpConnection.HTTP_BAD_REQUEST) {
                throw new WrongDeviceKeyException();
            } else if (responseCode != HttpConnection.HTTP_OK) {
                throw new ServerException(responseCode);
            }

            dis = conn.openDataInputStream();
            int numOfSubjects = dis.readInt();
            if (numOfSubjects <= 0) {
                return new Vector(0);
            }

            final Vector subjects = new Vector(numOfSubjects);
            for (int i = 0; i < numOfSubjects; i++) {
                final long subjectId = dis.readLong();

                final Subject subject = new Subject(subjectId, dis.readUTF());
                subjects.addElement(subject);
            }
            return subjects;
        } finally {
            if(dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void revertControlsWithEmptyList() {
        deleteAll();
        append(messagesBundle.get("noResults"), null);
        addCommand(refreshCommand);
    }

    private String linkSubject(long linkSubjectId) throws WrongDeviceKeyException, ServerException, IOException {
        HttpConnection conn = null;
        DataInputStream dis = null;
        try {
            conn = (HttpConnection) Connector.open(
                    WannatrakMidlet.URL
                            + "subjects/link?deviceId="
                            + controller.getDeviceKey()
                            + "&linkSubjectId="
                            + linkSubjectId
            );
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/plain");

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
            if(dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                }
            }
            try {
                if (conn!=null) {
                    conn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
