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

package org.wannatrak.client.setnewpass;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.History;
import org.wannatrak.client.StringConstants;
import org.wannatrak.client.HistoryToken;
import org.wannatrak.client.Messages;

/**
 * Created 29.05.2009 0:34:06
 *
 * @author Andrey Khalzov
 */
public class SetNewPassInfoWidget extends VerticalPanel {

    private StringConstants stringConstants = StringConstants.StringConstantsSingleton.getInstance();

    public SetNewPassInfoWidget(String email) {
        final Label header = new Label(stringConstants.setNewPassHeader());
        header.setStylePrimaryName("requestSetNewPassLabel");

        final Label info = new HTML(Messages.MessagesSingleton.getInstance().info(email));

        final Button closeButton = new Button(stringConstants.close());
        closeButton.setStylePrimaryName("closeButton");
        closeButton.addClickListener(
                new ClickListener() {
                    public void onClick(Widget sender) {
                        History.newItem(HistoryToken.demo.toString());
                    }
                }
        );

        add(header);
        add(info);
        add(closeButton);

        setCellHorizontalAlignment(closeButton, ALIGN_CENTER);

        setStylePrimaryName("setupPanel");
        setSpacing(14);

        getElement().setAttribute("align", "center");
    }
}