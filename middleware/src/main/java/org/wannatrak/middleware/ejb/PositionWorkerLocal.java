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

package org.wannatrak.middleware.ejb;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import javax.ejb.Local;

import org.wannatrak.middleware.entity.Position;
import org.wannatrak.middleware.entity.User;

import java.util.List;

/**
 * Created by Andrey Khalzov
 * 01.04.2009
 */
@Local
public interface PositionWorkerLocal extends PositionWorker {

    @Nullable
    public Position getLastPosition(User user, @NotNull Long subjectId, Boolean valid);

    @NotNull
    List<Position> getPositions(
            @NotNull User user,
            @NotNull Long subjectId,
            @NotNull DateTime from,
            @NotNull DateTime to,
            @NotNull Boolean valid
    );

    @NotNull
    List<Position> getDemoPositions(
            @NotNull Long subjectId,
            @NotNull DateTime from,
            @NotNull DateTime to,
            @NotNull Boolean valid
    );
}
