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
 * 21.12.2008 15:08:44
 */
package org.wannatrak.client.layout;

import org.wannatrak.client.MainWidget;
import org.wannatrak.client.layout.command.MainWidgetLayoutCommand;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SingleLayout extends AbstractMainWidgetLayout {

    private static final SingleLayout instance = new SingleLayout();

    public static SingleLayout getInstance() {
        return instance;
    }

    private SingleLayout() {
    }

    public void executeCommand(MainWidgetLayoutCommand mainWidgetLayoutCommand) {
        mainWidgetLayoutCommand.executeForSingleLayout(this);
    }

    protected int getCenterWidgetRow() {
        return LABEL_ROW;
    }

    @Override
    public void setCenterWidgetHeader(MainWidget mainWidget, Widget centerWidgetHeader, String cellStyleName) {
    }

    @Override
    public void setCenterWidgetHeader(MainWidget mainWidget, Widget centerWidgetHeader) {
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
    public void setBottomWidget(MainWidget mainWidget, Widget bottomWidget, String cellStyleName) {
    }

    @Override
    public void setBottomWidget(MainWidget mainWidget, Widget bottomWidget) {
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
