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
 * 08.03.2009
 */
package org.wannatrak.client.device;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import org.wannatrak.client.StringConstants;

public class HowtoSetupWidget extends VerticalPanel {

    public HowtoSetupWidget(String closeLabel, ClickListener clickCloseListener) {
        final Label header = new Label(StringConstants.StringConstantsSingleton.getInstance().setupLabel());
        header.setStylePrimaryName("setupLabel");

        final Button readyButton = new Button(closeLabel);
        readyButton.setStylePrimaryName("closeButton");
        readyButton.addClickListener(clickCloseListener);

        final Label howto = new HTML(StringConstants.StringConstantsSingleton.getInstance().howtoSetupText());

        add(header);
        add(howto);
        add(readyButton);

        setCellHorizontalAlignment(readyButton, ALIGN_CENTER);

        setStylePrimaryName("setupPanel");
        setSpacing(14);

        getElement().setAttribute("align", "center");
    }
}
