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
 * 21.08.2008 0:22:13
 */
package org.wannatrak.mobile.view;

import org.wannatrak.mobile.controller.BluetoothDevice;
import org.wannatrak.mobile.controller.Controller;
import org.wannatrak.mobile.controller.exception.BluetoothException;
import org.wannatrak.mobile.MessagesBundle;
import org.wannatrak.mobile.Executor;

import javax.microedition.lcdui.*;
import java.util.Vector;

public class GpsDevicesList extends List implements CommandListener {
    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.view.GpsDevicesList");
    
    private Controller controller;
    private Vector devices;

    private Command refreshCommand = new Command(messagesBundle.get("search"), Command.ITEM, 2);
    private Command selectCommand = new Command(messagesBundle.get("select"), Command.ITEM, 1);
    private Command cancelCommand = new Command(messagesBundle.get("cancel"), Command.EXIT, 3);

    public GpsDevicesList(Controller controller) {
        super(messagesBundle.get("title"), Choice.IMPLICIT);
        this.controller = controller;

        addCommand(cancelCommand);

        setCommandListener(this);

        append(messagesBundle.get("waiting"), null);

        fillList();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (refreshCommand.equals(command)) {
            refresh();
        } else if (cancelCommand.equals(command)) {
            controller.showConnectExternalDeviceForm();
        } else if (selectCommand.equals(command)) {
            final BluetoothDevice bluetoothDevice = (BluetoothDevice) devices.elementAt(getSelectedIndex());
            controller.selectDevice(bluetoothDevice.getName(), bluetoothDevice.getAddress());
        }
    }

    private void refresh() {
        deleteAll();
        append(messagesBundle.get("waiting"), null);
        setSelectCommand(null);
        removeCommand(refreshCommand);
        fillList();
    }

    private void fillList() {
        controller.executeAsynch(new Executor() {
            public void execute() {
                try {
                    devices = controller.getGpsDevices();
                } catch (BluetoothException e) {
                    deleteAll();
                    append(messagesBundle.get("error") + " ", null);
                    append(e.getMessage(), null);
                    append(messagesBundle.get("checkBluetoothAlert"), null);
                    setSelectCommand(refreshCommand);
                    return;
                }

                deleteAll();
                if (devices != null && !devices.isEmpty()) {
                    for (int i = 0; i < devices.size(); i++) {
                        final Object device = devices.elementAt(i);
                        append(((BluetoothDevice) device).getName(), null);
                    }
                    setSelectCommand(selectCommand);
                } else {
                    append(messagesBundle.get("noResults"), null);
                    setSelectCommand(refreshCommand);
                }
                addCommand(refreshCommand);
            }

            public void stop() {
            }
        });
    }
}
