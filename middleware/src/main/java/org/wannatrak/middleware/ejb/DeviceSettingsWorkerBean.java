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

import org.wannatrak.middleware.entity.DeviceSettings;
import org.wannatrak.middleware.entity.Subject;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ejb.Stateless;

@Stateless(name = DeviceSettingsWorker.JNDI_NAME)
public class DeviceSettingsWorkerBean extends AbstractWorkerBean implements DeviceSettingsWorker {

    @Nullable
    public DeviceSettings getDeviceSettings(@NotNull String deviceId) {
        try {
            final Subject subject = dao.findSingleResult(Subject.class, "e.deviceId = ?", deviceId);
            return subject.getDeviceSettings();
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
