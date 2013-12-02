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
import org.jetbrains.annotations.Nullable;
import org.wannatrak.middleware.configuration.core.exceptions.ValidationException;
import org.wannatrak.middleware.configuration.core.exceptions.UnknownStoreException;
import org.wannatrak.middleware.configuration.core.exceptions.StoreNotFoundException;

import java.util.Map;

/**
 * Created 19.04.2009 15:49:07
 *
 * @author Sergey Grachev
 */
public interface Configuration<S, K, V> {
    @Nullable
    V get(K key);

    @NotNull
    Map<K, V> get();

    @NotNull
    Configuration<S, K, V> set(K key, V value) throws ValidationException;

    boolean setSafe(K key, V value);

    void setValidator(Validator<K, V> validator);

    void setAdapter(Adapter<S, K, V> adapter);

    void load() throws ValidationException, UnknownStoreException, StoreNotFoundException;

    void load(@NotNull S store) throws ValidationException, UnknownStoreException, StoreNotFoundException;

    void store() throws UnknownStoreException;

    void store(@NotNull S store) throws UnknownStoreException;

    void setStore(@NotNull S store);

    boolean isLoaded();
}
