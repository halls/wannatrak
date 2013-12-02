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
 * 12.04.2009
 */
package org.wannatrak.client.subject;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SubjectSettingsData implements IsSerializable {
    public String name;
    public Integer savePeriod;
    public Integer sendPeriod;

    public SubjectSettingsData() {
    }

    public SubjectSettingsData(String name, Integer savePeriod, Integer sendPeriod) {
        this.name = name;
        this.savePeriod = savePeriod;
        this.sendPeriod = sendPeriod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSavePeriod() {
        return savePeriod;
    }

    public void setSavePeriod(Integer savePeriod) {
        this.savePeriod = savePeriod;
    }

    public Integer getSendPeriod() {
        return sendPeriod;
    }

    public void setSendPeriod(Integer sendPeriod) {
        this.sendPeriod = sendPeriod;
    }
}
