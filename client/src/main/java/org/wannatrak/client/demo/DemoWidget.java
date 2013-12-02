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
 * 05.03.2009 0:52:51
 */
package org.wannatrak.client.demo;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Hyperlink;
import org.wannatrak.client.Mediator;
import org.wannatrak.client.StringConstants;
import org.wannatrak.client.HistoryToken;

public class DemoWidget extends VerticalPanel {

    public DemoWidget(Mediator mediator) {
        mediator.setDemoWidget(this);

        final DemoSubjectsController demoSubjectsController = new DemoSubjectsController(mediator);
        final DemoSubjectsWidget demoSubjectsWidget = new DemoSubjectsWidget(mediator, demoSubjectsController);

        add(demoSubjectsWidget);

        final Label introLabel = new HTML(StringConstants.StringConstantsSingleton.getInstance().intro());

        final Hyperlink registerHyperlink = new Hyperlink(
                StringConstants.StringConstantsSingleton.getInstance().registerRightNow(),
                HistoryToken.registration.toString()
        );
        registerHyperlink.setStylePrimaryName("registerHyperlink");

        final VerticalPanel introPanel = new VerticalPanel();
        introPanel.add(introLabel);
        introPanel.add(registerHyperlink);
        introPanel.setStylePrimaryName("borderedIntro");
        introPanel.setSpacing(10);

        add(demoSubjectsWidget);
        add(introPanel);
    }
}
