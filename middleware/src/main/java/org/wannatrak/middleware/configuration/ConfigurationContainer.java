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

package org.wannatrak.middleware.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

import org.wannatrak.middleware.configuration.core.Configuration;
import org.wannatrak.middleware.configuration.core.adapters.PropertiesAdapter;
import org.wannatrak.middleware.configuration.core.exceptions.StoreNotFoundException;
import org.wannatrak.middleware.configuration.core.exceptions.UnknownStoreException;
import org.wannatrak.middleware.configuration.core.exceptions.ValidationException;
import org.wannatrak.middleware.configuration.configurations.MailConfiguration;
import org.wannatrak.middleware.configuration.configurations.MailValidator;
import org.wannatrak.middleware.util.UserSession;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Created 19.04.2009 19:34:15
 *
 * @author Sergey Grachev
 */
public enum ConfigurationContainer {
    MAIL {
        public void init() throws StoreNotFoundException, UnknownStoreException, ValidationException {
            if (!configurations.containsKey(MAIL)) {
                final MailConfiguration configuration = new MailConfiguration(new PropertiesAdapter(), new MailValidator());
                configuration.setStore("/META-INF/configuration/mail.properties");
                configuration.load();

                final MailConfiguration configuration_ru = new MailConfiguration(new PropertiesAdapter(), new MailValidator());
                configuration_ru.setStore("/META-INF/configuration/mail_ru.properties");
                configuration_ru.load();

                final Map<Locale, Configuration> configurationMap = new HashMap<Locale, Configuration>();
                configurationMap.put(Locale.ENGLISH, configuration);
                configurationMap.put(new Locale("ru", "RU"), configuration_ru);

                configurations.put(MAIL, configurationMap);
            }
        }};

    private static final Map<ConfigurationContainer, Map<Locale, Configuration>> configurations
            = new HashMap<ConfigurationContainer, Map<Locale, Configuration>>();

    public abstract void init() throws StoreNotFoundException, UnknownStoreException, ValidationException;

    @NotNull
    public Configuration get() {
        Configuration configuration = configurations.get(MAIL).get(UserSession.getLocale());
        if (configuration == null) {
            configuration = configurations.get(MAIL).get(Locale.ENGLISH);
        }
        return configuration;
    }
}
