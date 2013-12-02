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
 * 21.12.2008 21:12:52
 */
package org.wannatrak.client.layout.command;

import org.wannatrak.client.Mediator;
import org.wannatrak.client.layout.*;

public abstract class AbstractMainWidgetLayoutCommand implements MainWidgetLayoutCommand {

    private Mediator mediator;

    public AbstractMainWidgetLayoutCommand(Mediator mediator) {
        this.mediator = mediator;
    }

    public final void execute() {
        final MainWidgetLayout mainWidgetLayout = mediator.getMainWidgetLayout();
        if (mainWidgetLayout != null) {
            mainWidgetLayout.executeCommand(this);
        } else {
            applyLayout(getDefaultLayout());
        }
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void executeForCenterBottomLayout(CenterBottomLayout currentMainWidgetLayout) {
        failExecuteFor(currentMainWidgetLayout);
    }

    public void executeForCenterBottomLayout(CenterBottomRightLayout currentMainWidgetLayout) {
        failExecuteFor(currentMainWidgetLayout);
    }

    public void executeForLeftCenterBottomLayout(LeftCenterBottomLayout currentMainWidgetLayout) {
        failExecuteFor(currentMainWidgetLayout);
    }

    public void executeForLeftCenterBottomRightLayout(LeftCenterBottomRightLayout currentMainWidgetLayout) {
        failExecuteFor(currentMainWidgetLayout);
    }

    public void executeForSingleLayout(SingleLayout currentMainWidgetLayout) {
        failExecuteFor(currentMainWidgetLayout);
    }

    protected abstract MainWidgetLayout getDefaultLayout();

    protected void applyLayout(MainWidgetLayout newMainWidgetLayout) {
        mediator.applyLayout(newMainWidgetLayout);
    }

    private void failExecuteFor(MainWidgetLayout currentMainWidgetLayout) {
        throw new IllegalStateException(getClass().getName() + " for " + currentMainWidgetLayout.getClass().getName());
    }
}
