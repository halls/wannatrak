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
 * 14.08.2008 23:28:35
 */
package org.wannatrak.mobile;

import org.wannatrak.mobile.controller.Controller;
import org.wannatrak.mobile.view.LoginForm;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class WannatrakMidlet extends MIDlet {
    public static final String URL = MessagesBundle.getGlobal("url");

    private Controller controller;

    private boolean startup = true;
    
    public WannatrakMidlet() {
        this.controller = new Controller(this);
        final Display display = Display.getDisplay(this);

        final Executor executor = new Executor() {
            public void execute() {
                controller.checkNewVersion();

                if (!controller.hasDeviceId()) {
                    display.setCurrent(new LoginForm(controller));
                } else if (!controller.hasDeviceAddress()) {
                    controller.showConnectExternalDeviceForm();
                } else {
                    controller.sendLogs();
                    controller.initLog();

                    controller.startTrak();
                    controller.showTrakForm();
                }
            }

            public void stop() {
                controller.exit();
            }
        };
        controller.showLoading(
                MessagesBundle.getGlobal("loading"),
                MessagesBundle.getGlobal("exit"),
                executor
        );
        controller.executeAsynch(executor);
    }

    protected void startApp() throws MIDletStateChangeException {
        if (!startup && controller != null) {
            if (!controller.hasDeviceId()) {
                 Display.getDisplay(this).setCurrent(new LoginForm(controller));
            } else if (!controller.hasDeviceAddress()) {
                controller.showConnectExternalDeviceForm();
            } else {
                controller.showTrakForm();
            }
        }
        startup = false;
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException {
        controller.saveLogs();
    }
}
