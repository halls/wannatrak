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
 * 22.12.2008 18:21:33
 */
package org.wannatrak.client.layout.command;

import org.wannatrak.client.Mediator;
import org.wannatrak.client.layout.*;

public class ShowSingleWidgetCommand extends AbstractMainWidgetLayoutCommand {

    public ShowSingleWidgetCommand(Mediator mediator) {
        super(mediator);
    }

    @Override
    public void executeForCenterBottomLayout(CenterBottomLayout currentMainWidgetLayout) {
        applyLayout(SingleLayout.getInstance());
    }

    @Override
    public void executeForCenterBottomLayout(CenterBottomRightLayout currentMainWidgetLayout) {
        applyLayout(SingleLayout.getInstance());
    }

    @Override
    public void executeForLeftCenterBottomLayout(LeftCenterBottomLayout currentMainWidgetLayout) {
        applyLayout(SingleLayout.getInstance());
    }

    @Override
    public void executeForLeftCenterBottomRightLayout(LeftCenterBottomRightLayout currentMainWidgetLayout) {
        applyLayout(SingleLayout.getInstance());
    }

    @Override
    public void executeForSingleLayout(SingleLayout currentMainWidgetLayout) {
        applyLayout(SingleLayout.getInstance());
    }

    protected MainWidgetLayout getDefaultLayout() {
        return SingleLayout.getInstance();
    }
}
