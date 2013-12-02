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
 * 21.12.2008 21:06:12
 */
package org.wannatrak.client.layout.command;

import org.wannatrak.client.Command;
import org.wannatrak.client.layout.*;

public interface MainWidgetLayoutCommand extends Command {

    void executeForCenterBottomLayout(CenterBottomLayout currentMainWidgetLayout);

    void executeForCenterBottomLayout(CenterBottomRightLayout currentMainWidgetLayout);

    void executeForLeftCenterBottomLayout(LeftCenterBottomLayout currentMainWidgetLayout);

    void executeForLeftCenterBottomRightLayout(LeftCenterBottomRightLayout currentMainWidgetLayout);

    void executeForSingleLayout(SingleLayout currentMainWidgetLayout);
}
