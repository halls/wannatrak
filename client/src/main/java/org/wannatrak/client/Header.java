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
 * 12.12.2008 23:19:12
 */
package org.wannatrak.client;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.DeferredCommand;
import org.wannatrak.client.img.ImageBundle;

public class Header extends FlexTable {

    private final Mediator mediator;

    public Header(Mediator mediator) {
        this.mediator = mediator;
        mediator.setHeader(this);

        this.setCellSpacing(0);

        this.setStylePrimaryName("header");

        final Image logo = ImageBundle.ImageBundleSingleton.getInstance().logo().createImage();
        logo.setStylePrimaryName("logo");
        logo.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
                    public void execute() {
                        Window.Location.assign("http://" + Window.Location.getHost());
                    }
                }
            );
            }
        });

        this.setWidget(0, 0, logo);

        final SimplePanel lines = new SimplePanel();
        lines.setStylePrimaryName("lines");
        this.setWidget(1, 0, lines);
    }

    public void login(String login) {
        final Label loginLabel = new Label(login, false);
        loginLabel.addStyleName("loginInfoItem");
        final Label separatorLabel = new Label("|");
        final Hyperlink logoutLink = new Hyperlink(
                StringConstants.StringConstantsSingleton.getInstance().logout(),
                true,
                HistoryToken.demo.toString()
        );
        logoutLink.addStyleName("loginInfoItem");

        final HorizontalPanel loginInfoPanel = new HorizontalPanel();
        loginInfoPanel.setStylePrimaryName("loginInfo");

        loginInfoPanel.add(loginLabel);
        loginInfoPanel.setCellHorizontalAlignment(loginLabel, HasHorizontalAlignment.ALIGN_CENTER);

        loginInfoPanel.add(separatorLabel);

        loginInfoPanel.add(logoutLink);
        loginInfoPanel.setCellHorizontalAlignment(logoutLink, HasHorizontalAlignment.ALIGN_CENTER);

        loginInfoPanel.setStylePrimaryName("loginInfo");

        this.setWidget(1, 1, loginInfoPanel);
        this.getCellFormatter().setStyleName(1, 1, "loginInfoCell");
    }

    public void logout() {
        if (getCellCount(1) > 1) {
            removeCell(1, 1);
        }
    }
}
