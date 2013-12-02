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

package org.wannatrak.middleware.configuration.core;

import org.jetbrains.annotations.NotNull;
import org.wannatrak.middleware.configuration.core.exceptions.ValidationException;
import org.wannatrak.middleware.configuration.core.exceptions.StoreNotFoundException;
import org.wannatrak.middleware.configuration.core.exceptions.UnknownStoreException;

/**
 * Created 19.04.2009 15:58:54
 *
 * @author Sergey Grachev
 */
public abstract class AbstractAdapter<S, K, V> implements Adapter<S, K, V> {
    protected Configuration<S, K, V> configuration;

    public void load(@NotNull S store) throws ValidationException, StoreNotFoundException, UnknownStoreException {
        throw new UnsupportedOperationException();
    }

    public void setConfiguration(@NotNull Configuration<S, K, V> configuration) {
        this.configuration = configuration;
    }

    public void store(@NotNull Object store) {
        throw new UnsupportedOperationException();
    }
}
