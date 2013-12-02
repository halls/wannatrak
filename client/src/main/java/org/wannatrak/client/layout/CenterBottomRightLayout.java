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
 * 18.12.2008 23:05:43
 */
package org.wannatrak.client.layout;

import org.wannatrak.client.MainWidget;
import org.wannatrak.client.layout.command.MainWidgetLayoutCommand;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PushButton;

public class CenterBottomRightLayout extends AbstractMainWidgetLayout {

    private static final CenterBottomRightLayout instance = new CenterBottomRightLayout();

    public static CenterBottomRightLayout getInstance() {
        return instance;
    }

    private CenterBottomRightLayout() {
    }

    public void executeCommand(MainWidgetLayoutCommand mainWidgetLayoutCommand) {
        mainWidgetLayoutCommand.executeForCenterBottomLayout(this);
    }

    @Override
    public void setLeftWidgetLabel(MainWidget mainWidget, Label leftWidgetLabel, String cellStyleName) {
    }

    @Override
    public void setLeftWidgetLabel(MainWidget mainWidget, Label leftWidgetLabel) {
    }

    @Override
    public void setLeftWidget(MainWidget mainWidget, Widget leftWidget, String cellStyleName) {
    }

    @Override
    public void setLeftWidget(MainWidget mainWidget, Widget leftWidget) {
    }

    @Override
    public void setLeftHideButton(MainWidget mainWidget, PushButton leftHideButton) {
    }

    @Override
    public void setLeftHideButton(MainWidget mainWidget, PushButton leftHideButton, String cellStyleName) {
    }

    @Override
    public void setRightShowButton(MainWidget mainWidget, PushButton rightShowButton, String cellStyleName) {
    }

    @Override
    public void setRightShowButton(MainWidget mainWidget, PushButton rightShowButton) {
    }
}
