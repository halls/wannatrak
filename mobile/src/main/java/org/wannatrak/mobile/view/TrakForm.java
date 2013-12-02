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
import org.wannatrak.mobile.controller.exception.LoginException;
import org.wannatrak.mobile.MessagesBundle;
import org.wannatrak.mobile.ImageBundle;
import org.wannatrak.mobile.Executor;

import javax.microedition.lcdui.*;
import java.io.IOException;

public class TrakForm extends Form implements CommandListener {
    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.view.TrakForm");

    private final Controller controller;

    private final Command startCommand = new Command(messagesBundle.get("start"), Command.OK, 1);
    private final Command mapCommand = new Command(messagesBundle.get("map"), Command.ITEM, 2);
    private final Command positionInfoCommand = new Command(messagesBundle.get("coordinates"), Command.ITEM, 3);
    private final Command settingsCommand = new Command(messagesBundle.get("settings"), Command.ITEM, 4);
    private final Command changeSubjectCommand = new Command(messagesBundle.get("changeSubject"), Command.ITEM, 5);
    private final Command changeDeviceCommand = new Command(messagesBundle.get("changeDevice"), Command.ITEM, 6);
    private final Command changeUserCommand = new Command(messagesBundle.get("changeUser"), Command.ITEM, 7);
    private final Command stopCommand = new Command(messagesBundle.get("stop"), Command.ITEM, 8);
    private final Command hideCommand = new Command(messagesBundle.get("hide"), Command.ITEM, 9);
    private final Command exitCommand = new Command(messagesBundle.get("exit"), Command.ITEM, 10);

    private final StringItem subjectName;
    private final StringItem status;
    private final StringItem server;
    private final StringItem log;
    private final StringItem gpsReceiver;

    private boolean locationProviderDefined;
    private boolean serverFound;

    private long lastAttractAttentionTs;

    public TrakForm(Controller controller) {
        super(messagesBundle.get("title"));

        this.controller = controller;

        append(ImageBundle.getImage("logo", "png"));

        subjectName = new StringItem(messagesBundle.get("subject"), controller.getSubjectName().trim());
        status = new StringItem(messagesBundle.get("status"), "");
        server = new StringItem(messagesBundle.get("server"), "");
        gpsReceiver = new StringItem(messagesBundle.get("locationProvider"), "");
        log = new StringItem("", "");

        setServerFound(true);
        setLocationProviderDefined(true);
        updateStatus();

        append("\n");
        append(subjectName);
        append("\n");
        append(status);
        append(server);
        append(gpsReceiver);
        append(log);

        addCommand(changeSubjectCommand);
        addCommand(changeDeviceCommand);
        addCommand(changeUserCommand);
        addCommand(positionInfoCommand);
        addCommand(mapCommand);
        addCommand(settingsCommand);
        addCommand(hideCommand);
        addCommand(exitCommand);

        setCommandListener(this);
        controller.setTrakForm(this);
    }

    public void reinit() {
        setName(controller.getSubjectName());
        addCommand(changeSubjectCommand);
        addCommand(changeUserCommand);
        addCommand(exitCommand);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (startCommand.equals(command)) {
            removeCommand(startCommand);
            addCommand(stopCommand);

            controller.startTrak();
        } else if (stopCommand.equals(command)) {
            controller.stop();
            removeCommand(stopCommand);

            final Executor executor = new Executor() {
                public void execute() {
                    while (controller.isStarted()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                    addCommand(startCommand);
                    updateStatus();
                }

                public void stop() {
                }
            };
            controller.executeAsynch(executor);
        } else if (changeSubjectCommand.equals(command)) {
            controller.stop();

            removeCommand(changeSubjectCommand);
            removeCommand(changeUserCommand);
            removeCommand(exitCommand);

            final Executor executor = new Executor() {

                public void execute() {
                    final String deviceKey;
                    try {
                        deviceKey = controller.unlink();

                        controller.setDeviceKey(deviceKey);
                        controller.setDeviceId("");
                        controller.showCreateSubjectForm();
                    } catch (IOException e) {
                        controller.showLoginForm();
                    } catch (ServerException e) {
                        controller.showLoginForm();
                    } catch (LoginException e) {
                        controller.showLoginForm();
                    }
                }

                public void stop() {
                }
            };
            controller.showLoading(messagesBundle.get("waitingChangeSubject"));
            controller.executeAsynch(executor);
        } else if (changeDeviceCommand.equals(command)) {
            controller.stop();

            controller.showConnectExternalDeviceForm();
        } else if (changeUserCommand.equals(command)) {
            controller.stop();

            controller.showLoginForm();
        } else if (settingsCommand.equals(command)) {
            controller.showSettingsForm();
        } else if (positionInfoCommand.equals(command)) {
            controller.showPositionInfoForm();
        }  else if (mapCommand.equals(command)) {
            controller.loadAndShowMap();
        }  else if (hideCommand.equals(command)) {
            controller.hide();
        }  else if (exitCommand.equals(command)) {
            controller.exit();
        }
    }

    public void show() {
        reinit();
        if (controller.isStarted()) {
            addCommand(stopCommand);
        } else {
            addCommand(startCommand);
        }
    }

    public void setName(String name) {
        subjectName.setText(name.trim());
    }

    public void setLocationProviderDefined(boolean locationProviderDefined) {
        this.locationProviderDefined = locationProviderDefined;
        if (locationProviderDefined) {
            gpsReceiver.setText(controller.getDeviceName().trim());
        } else {
            gpsReceiver.setText(messagesBundle.get("noneLocationProvider"));
        }
        updateStatus();
    }

    public void setServerFound(boolean serverFound) {
        this.serverFound = serverFound;
        if (serverFound) {
            server.setText(messagesBundle.get("serverAvailable"));
        } else {
            server.setText(messagesBundle.get("serverNotAvailable"));
        }
        updateStatus();
    }
    
    public void showLog(String message) {
    	log.setText(message);
    }

    public synchronized void updateStatus() {
        if (controller.isStarted()) {
            if (locationProviderDefined && serverFound) {
                status.setText(messagesBundle.get("statusTracking"));
            } else {
                status.setText(messagesBundle.get("statusTrackingImpossible"));
                final long ts = System.currentTimeMillis();
                if (ts - lastAttractAttentionTs > 30000 && ts - controller.getStartedTs() > 5000) {
                    controller.attractAttention();
                    lastAttractAttentionTs = System.currentTimeMillis();
                }
            }
        } else {
            status.setText(messagesBundle.get("statusStopped"));
        }
    }
}
