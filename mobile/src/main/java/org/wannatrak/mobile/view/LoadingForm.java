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

package org.wannatrak.mobile.view;

import org.wannatrak.mobile.MessagesBundle;
import org.wannatrak.mobile.Executor;

import javax.microedition.lcdui.*;

/**
 * Created 30.06.2009 22:24:09
 *
 * @author Andrey Khalzov
 */
public class LoadingForm extends Form implements CommandListener {
    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.view.LoadingForm");

    private Command stopCommand;
    private Executor executor;

    public LoadingForm(String name, String stopName, Executor executor) {
        this(name);

        this.executor = executor;

        stopCommand = new Command(stopName, Command.CANCEL, 1);
        addCommand(stopCommand);

        setCommandListener(this);
    }

    public LoadingForm(String name) {
        super(messagesBundle.get("title"));

        append(new Gauge(name, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
    }

    public void commandAction(Command command, Displayable displayable) {
        if (stopCommand.equals(command)) {
            executor.stop();
        }
    }
}
