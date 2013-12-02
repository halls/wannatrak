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

package org.wannatrak.middleware.configuration.configurations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wannatrak.middleware.configuration.core.AbstractConfiguration;
import org.wannatrak.middleware.configuration.core.Adapter;
import org.wannatrak.middleware.configuration.core.Validator;

import java.util.Properties;

/**
 * Created 19.04.2009 20:23:35
 *
 * @author Sergey Grachev
 */
public class MailConfiguration extends AbstractConfiguration<Object, String, String> {

    public MailConfiguration(Adapter<Object, String, String> objectStringStringAdapter, Validator<String, String> stringStringValidator) {
        super(objectStringStringAdapter, stringStringValidator);
    }

    @Nullable
    public String getHost() {
        return data.get("config.host");
    }

    @NotNull
    public boolean useJBossService() {
        return data.get("config.useJBossService") != null && data.get("config.useJBossService").equals("true");
    }

    @Nullable
    public String getJBossJNDIName() {
        return data.get("config.JBossJNDIName");
    }

    @NotNull
    public Properties getMailProperties() {
        final Properties properties = new Properties();
        for (final String item: data.keySet()) {
            if (item.indexOf("mail.") == 0) {
                properties.put(item, data.get(item));
            }
        }
        return properties;
    }

    @Nullable
    public String getUserName() {
        return data.get("mail.user");
    }

    @Nullable
    public String getPassword() {
        return data.get("config.password");
    }

    @Nullable
    public String getFromAddress() {
        return data.get("mail.from");
    }
}
