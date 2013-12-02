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
 * 08.01.2009 20:56:54
 */
package org.wannatrak.middleware.ejb;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.entity.Session;

public interface SessionWorker {

    final String JNDI_NAME = AbstractWorkerBean.JNDI_PREFIX + "SessionWorker";

    void removeSession(@NotNull String sessionId);

    @Nullable
    User getUser(@NotNull String sessionId);

    @Nullable
    Session getSession(@NotNull String sessionId);
}
