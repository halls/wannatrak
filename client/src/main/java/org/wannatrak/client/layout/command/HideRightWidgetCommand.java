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
 * 21.12.2008 22:30:23
 */
package org.wannatrak.client.layout.command;

import org.wannatrak.client.layout.*;
import org.wannatrak.client.Mediator;

public class HideRightWidgetCommand extends AbstractMainWidgetLayoutCommand {

    public HideRightWidgetCommand(Mediator mediator) {
        super(mediator);
    }

    public void executeForCenterBottomLayout(CenterBottomRightLayout currentMainWidgetLayout) {
        getMediator().pinMapToRight();
        applyLayout(CenterBottomLayout.getInstance());
    }

    public void executeForLeftCenterBottomRightLayout(LeftCenterBottomRightLayout currentMainWidgetLayout) {
        getMediator().pinMapToRight();
        applyLayout(LeftCenterBottomLayout.getInstance());
    }

    @Override
    public void executeForCenterBottomLayout(CenterBottomLayout currentMainWidgetLayout) {
    }

    @Override
    public void executeForLeftCenterBottomLayout(LeftCenterBottomLayout currentMainWidgetLayout) {
    }

    protected MainWidgetLayout getDefaultLayout() {
        return LeftCenterBottomLayout.getInstance();
    }
}
