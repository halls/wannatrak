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

package org.wannatrak.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.History;

/**
 * Created by Andrey Khalzov
 * 14.07.2008 12:05:23
 */
public class Client implements EntryPoint {

    public void onModuleLoad() {

        final Mediator mediator = new Mediator();

        final Header header = new Header(mediator);

        final MainWidget mainWidget = new MainWidget(mediator);

        final Grid majorLayoutPanel = new Grid(2, 1);
        majorLayoutPanel.setCellSpacing(0);

        majorLayoutPanel.setWidget(0, 0, header);
        majorLayoutPanel.getCellFormatter().setStyleName(0, 0, "headerCell");

        majorLayoutPanel.setWidget(1, 0, mainWidget);
        majorLayoutPanel.getCellFormatter().setStyleName(1, 0, "mainWidgetCell");

        majorLayoutPanel.setStylePrimaryName("majorLayout");

        mediator.setMajorLayoutPanel(majorLayoutPanel);

        History.addHistoryListener(new HistoryListener(mediator));

        RootPanel.get("client").add(majorLayoutPanel);
    }

}
