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
 * 30.03.2009
 */
package org.wannatrak.client.subject;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SubjectInfoData implements IsSerializable {
    public Long id;
    public Double speed;
    public Double altitude;
    public SubjectState subjectState;
    public SubjectSettingsData subjectSettingsData;

    public SubjectInfoData() {
    }

    public SubjectInfoData(
            Long id,
            String name,
            Double speed,
            Double altitude,
            Integer savePeriod,
            Integer sendPeriod,
            SubjectState subjectState
    ) {
        this.id = id;
        this.speed = speed;
        this.altitude = altitude;
        this.subjectState = subjectState;
        this.subjectSettingsData = new SubjectSettingsData(
                name,
                savePeriod == null ? 5 : savePeriod,
                sendPeriod == null ? 5 : sendPeriod
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return subjectSettingsData.getName();
    }

    public void setName(String name) {
        subjectSettingsData.setName(name);
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Integer getSavePeriod() {
        return subjectSettingsData.getSavePeriod();
    }

    public void setSavePeriod(Integer savePeriod) {
        subjectSettingsData.setSavePeriod(savePeriod);
    }

    public Integer getSendPeriod() {
        return subjectSettingsData.getSendPeriod();
    }

    public void setSendPeriod(Integer sendPeriod) {
        subjectSettingsData.setSendPeriod(sendPeriod);
    }

    public SubjectState getSubjectState() {
        return subjectState;
    }

    public void setSubjectState(SubjectState subjectState) {
        this.subjectState = subjectState;
    }
}
