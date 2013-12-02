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
 * 16.02.2009 23:08:06
 */
package org.wannatrak.mobile.view;

import org.wannatrak.mobile.controller.Controller;
import org.wannatrak.mobile.MessagesBundle;
import org.wannatrak.mobile.ImageBundle;

import javax.microedition.lcdui.*;

public class ConnectExternalDeviceForm extends Form implements CommandListener {
    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.view.ConnectExternalDeviceForm");

    private Controller controller;

    private StringItem queryMessage = new StringItem(messagesBundle.get("query"), null);
    private Command noCommand = new Command(messagesBundle.get("no"), Command.OK, 1);
    private Command yesCommand = new Command(messagesBundle.get("yes"), Command.CANCEL, 2);

    public ConnectExternalDeviceForm(Controller controller) {
        super(messagesBundle.get("title"));

        this.controller = controller;
        addCommand(noCommand);
        addCommand(yesCommand);
        append(ImageBundle.getImage("icon", "png"));

        append(queryMessage);
        setCommandListener(this);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (noCommand.equals(command)) {
            if (controller.hasInternalGPS()) {
                controller.selectDevice(messagesBundle.get("internal"), Controller.INTERNAL_DEVICE_ADDRESS);
                controller.setGPSReceiverFound(true);
            } else {
                if (controller.isCellIDAvailable()) {
                    controller.selectDevice(messagesBundle.get("cellid"), null);
                    controller.setGPSReceiverFound(true);
                } else {
                    if (!controller.hasDeviceId()) {
                        controller.showCreateSubjectForm();
                    } else {
                        controller.showTrakForm();
                    }
                }
            }
        } else if (yesCommand.equals(command)) {
            controller.showExternalDevicesList();
            controller.setGPSReceiverFound(false);
        }
    }
}
