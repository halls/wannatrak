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
import org.wannatrak.middleware.configuration.core.exceptions.UnknownStoreException;
import org.wannatrak.middleware.configuration.core.exceptions.StoreNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created 19.04.2009 16:20:03
 *
 * @author Sergey Grachev
 */
@SuppressWarnings("unchecked")
public abstract class AbstractConfiguration<S, K, V> implements Configuration<S, K, V> {
    protected final HashMap<K, V> data = new HashMap<K, V>();
    protected Validator validator;
    protected Adapter adapter;
    protected Object store;
    protected boolean loaded = false;

    protected AbstractConfiguration(Adapter<S, K, V> adapter, Validator<K, V> validator){
        this.adapter = adapter;
        this.adapter.setConfiguration(this);
        this.validator = validator;
    }

    public V get(K key) {
        return data.get(key);
    }

    @NotNull
    public Map<K, V> get() {
        return (Map<K, V>) data.clone();
    }

    @NotNull
    public Configuration<S, K, V> set(final K key, final V value) throws ValidationException {
        if (validator != null) {
            validator.validate(key, value);
        }
        data.put(key, value);
        return this;
    }

    public boolean setSafe(final K key, final V value) {
        try {
            set(key, value);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    public void setValidator(Validator<K, V> validator) {
        this.validator = validator;
    }

    public void setAdapter(Adapter<S, K, V> adapter) {
        this.adapter = adapter;
    }

    public void load() throws ValidationException, UnknownStoreException, StoreNotFoundException {
        if (store == null) {
            throw new UnknownStoreException();
        }
        adapter.load(store);
        loaded = true;
    }

    public void load(@NotNull S store) throws ValidationException, UnknownStoreException, StoreNotFoundException {
        adapter.load(store);
        loaded = true;
    }

    public void store() throws UnknownStoreException {
        if (store == null) {
            throw new UnknownStoreException();
        }
        adapter.store(store);
    }

    public void store(@NotNull S store) throws UnknownStoreException {
        adapter.store(store);
    }

    public void setStore(@NotNull S store) {
        if (!store.equals(this.store)) {
            this.store = store;
            loaded = false;
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

}
