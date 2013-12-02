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
 * 18.12.2008 23:08:25
 */
package org.wannatrak.client.layout;

import org.wannatrak.client.MainWidget;
import org.wannatrak.client.layout.command.MainWidgetLayoutCommand;
import com.google.gwt.user.client.ui.PushButton;

public class LeftCenterBottomRightLayout extends AbstractMainWidgetLayout {
    private static final LeftCenterBottomRightLayout instance = new LeftCenterBottomRightLayout();

    public static LeftCenterBottomRightLayout getInstance() {
        return instance;
    }

    protected LeftCenterBottomRightLayout() {
    }

    public void executeCommand(MainWidgetLayoutCommand mainWidgetLayoutCommand) {
        mainWidgetLayoutCommand.executeForLeftCenterBottomRightLayout(this);
    }

    @Override
    public void setLeftShowButton(MainWidget mainWidget, PushButton leftShowButton, String cellStyleName) {
    }

    @Override
    public void setLeftShowButton(MainWidget mainWidget, PushButton leftShowButton) {
    }

    @Override
    public void setRightShowButton(MainWidget mainWidget, PushButton rightShowButton, String cellStyleName) {
    }

    @Override
    public void setRightShowButton(MainWidget mainWidget, PushButton rightShowButton) {
    }
}
