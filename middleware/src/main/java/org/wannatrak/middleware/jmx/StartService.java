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

package org.wannatrak.middleware.jmx;

import org.jboss.annotation.ejb.Management;
import org.wannatrak.middleware.configuration.core.exceptions.StoreNotFoundException;
import org.wannatrak.middleware.configuration.core.exceptions.UnknownStoreException;
import org.wannatrak.middleware.configuration.core.exceptions.ValidationException;

/**
 * Created 21.04.2009 22:58:44
 *
 * @author Sergey Grachev
 */
@Management
public interface StartService {
    final String OBJECT_NAME = "wannatrak:service=StartService";

    void create();

    void start() throws StoreNotFoundException, UnknownStoreException, ValidationException;

    void stop();
}
