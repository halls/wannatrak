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
 * 18.12.2008 22:27:09
 */
package org.wannatrak.client.layout;

import org.wannatrak.client.MainWidget;
import org.wannatrak.client.layout.command.MainWidgetLayoutCommand;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Label;

public class LeftCenterBottomLayout extends AbstractMainWidgetLayout {

    private static final LeftCenterBottomLayout instance = new LeftCenterBottomLayout();

    public static LeftCenterBottomLayout getInstance() {
        return instance;
    }

    private LeftCenterBottomLayout() {
    }

    public void executeCommand(MainWidgetLayoutCommand mainWidgetLayoutCommand) {
        mainWidgetLayoutCommand.executeForLeftCenterBottomLayout(this);
    }

    @Override
    public void setRightWidgetLabel(MainWidget mainWidget, Label rightWidgetLabel, String cellStyleName) {
    }

    @Override
    public void setRightWidgetLabel(MainWidget mainWidget, Label rightWidgetLabel) {
    }

    @Override
    public void setRightWidget(MainWidget mainWidget, Widget rightWidget, String cellStyleName) {
    }

    @Override
    public void setRightWidget(MainWidget mainWidget, Widget rightWidget) {
    }

    @Override
    public void setRightHideButton(MainWidget mainWidget, PushButton rightHideButton, String cellStyleName) {
    }

    @Override
    public void setRightHideButton(MainWidget mainWidget, PushButton rightHideButton) {
    }

    @Override
    public void setLeftShowButton(MainWidget mainWidget, PushButton leftShowButton, String cellStyleName) {
    }

    @Override
    public void setLeftShowButton(MainWidget mainWidget, PushButton leftShowButton) {
    }
}
