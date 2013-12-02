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

import org.wannatrak.middleware.configuration.core.exceptions.ValidationException;

/**
 * Created 19.04.2009 15:57:48
 *
 * @author Sergey Grachev
 */
public interface Validator<K, V> {
    void validate(K key, V value) throws ValidationException;

    boolean validateSafe(K key, V value);

    void validateConfiguration(Configuration configuration) throws ValidationException;

    boolean validateConfigurationSafe(Configuration configuration);
}
