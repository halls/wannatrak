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
 * 05.08.2008 0:32:08
 */
package org.wannatrak.middleware.ejb;

import org.joda.time.DateTime;
import org.wannatrak.middleware.entity.Position;
import org.wannatrak.middleware.entity.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PositionWorker {

    final String JNDI_NAME = AbstractWorkerBean.JNDI_PREFIX + "PositionWorker";

    @Nullable
    Position getLastPosition(User user, @NotNull Long subjectId, Boolean valid);

    @NotNull
    List<Position> getPositions(
            @NotNull User user,
            @NotNull Long subjectId,
            @NotNull DateTime from,
            @NotNull DateTime to,
            @NotNull Boolean valid
    );
}
