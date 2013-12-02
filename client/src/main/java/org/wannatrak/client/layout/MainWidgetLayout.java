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
 * 18.12.2008 20:14:53
 */
package org.wannatrak.client.layout;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PushButton;
import org.wannatrak.client.MainWidget;
import org.wannatrak.client.layout.command.MainWidgetLayoutCommand;

public interface MainWidgetLayout {

    void executeCommand(MainWidgetLayoutCommand mainWidgetLayoutCommand);

    void setLeftWidgetLabel(MainWidget mainWidget, Label leftWidgetLabel);

    void setLeftWidgetLabel(MainWidget mainWidget, Label leftWidgetLabel, String cellStyleName);


    void setRightWidgetLabel(MainWidget mainWidget, Label rightWidgetLabel);

    void setRightWidgetLabel(MainWidget mainWidget, Label rightWidgetLabel, String cellStyleName);


    void setCenterWidgetHeader(MainWidget mainWidget, Widget centerWidgetHeader);

    void setCenterWidgetHeader(MainWidget mainWidget, Widget centerWidgetHeader, String cellStyleName);


    void setLeftWidget(MainWidget mainWidget, Widget leftWidget);

    void setLeftWidget(MainWidget mainWidget, Widget leftWidget, String cellStyleName);


    void setCenterWidget(MainWidget mainWidget, Widget centerWidget);

    void setCenterWidget(MainWidget mainWidget, Widget centerWidget, String cellStyleName);


    void setRightWidget(MainWidget mainWidget, Widget rightWidget);

    void setRightWidget(MainWidget mainWidget, Widget rightWidget, String cellStyleName);


    void setBottomWidget(MainWidget mainWidget, Widget bottomWidget);

    void setBottomWidget(MainWidget mainWidget, Widget bottomWidget, String cellStyleName);


    void setLeftHideButton(MainWidget mainWidget, PushButton leftHideButton);

    void setLeftHideButton(MainWidget mainWidget, PushButton leftHideButton, String cellStyleName);


    void setRightHideButton(MainWidget mainWidget, PushButton rightHideButton);

    void setRightHideButton(MainWidget mainWidget, PushButton rightHideButton, String cellStyleName);


    void setLeftShowButton(MainWidget mainWidget, PushButton leftShowButton);

    void setLeftShowButton(MainWidget mainWidget, PushButton leftShowButton, String cellStyleName);


    void setRightShowButton(MainWidget mainWidget, PushButton rightShowButton);

    void setRightShowButton(MainWidget mainWidget, PushButton rightShowButton, String cellStyleName);    
}
