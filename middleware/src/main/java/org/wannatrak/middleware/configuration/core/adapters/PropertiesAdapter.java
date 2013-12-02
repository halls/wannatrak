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

package org.wannatrak.middleware.configuration.core.adapters;

import org.jetbrains.annotations.NotNull;
import org.wannatrak.middleware.configuration.core.AbstractAdapter;
import org.wannatrak.middleware.configuration.core.Configuration;
import org.wannatrak.middleware.configuration.core.exceptions.ValidationException;
import org.wannatrak.middleware.configuration.core.exceptions.StoreNotFoundException;
import org.wannatrak.middleware.configuration.core.exceptions.UnknownStoreException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created 19.04.2009 16:10:43
 *
 * @author Sergey Grachev
 */
public class PropertiesAdapter extends AbstractAdapter<Object, String, String> {
    @Override
    public void load(@NotNull Object store) throws ValidationException, StoreNotFoundException, UnknownStoreException {
        final Properties properties = new Properties();
        if (store instanceof String) {
            final InputStream inputStream = Configuration.class.getResourceAsStream((String) store);
            if (inputStream != null) {
                try {
                    properties.load(inputStream);
                    for (Object key : properties.keySet()) {
                        configuration.set((String) key, properties.getProperty((String) key));
                    }
                } catch (IOException e) {
                    //
                }
            } else {
                throw new StoreNotFoundException((String) store);
            }
        } else {
            throw new UnknownStoreException((String) store);
        }
    }
}
