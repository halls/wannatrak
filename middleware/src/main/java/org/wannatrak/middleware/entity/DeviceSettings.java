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
 * 01.08.2008 0:15:31
 */
package org.wannatrak.middleware.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = DeviceSettings.TABLE_NAME)
public class DeviceSettings extends UndeletableEntity {
    public static final String TABLE_NAME = "wt_device_settings";

    public static final String SMS_RECIPIENT = "SmsRecipient";

    @Basic
    @Column(nullable = false)
    private Integer sendPeriod = 5;

    @Basic
    @Column(nullable = false)
    private Integer savePeriod = 5;

    @Basic
    @Column(name = SMS_RECIPIENT)
    private Long smsRecipient;

    @Basic
    @Column(nullable = false)
    private Integer odometerAccuracy = 150;

    public Integer getSavePeriod() {
        return savePeriod;
    }

    public void setSavePeriod(@NotNull Integer savePeriod) {
        this.savePeriod = savePeriod;
    }

    public Integer getSendPeriod() {
        return sendPeriod;
    }

    public void setSendPeriod(@NotNull Integer sendPeriod) {
        this.sendPeriod = sendPeriod;
    }

    @Nullable
    public Long getSmsRecipient() {
        return smsRecipient;
    }

    public void setSmsRecipient(Long smsRecipient) {
        this.smsRecipient = smsRecipient;
    }

    @Nullable
    public Integer getOdometerAccuracy() {
        return odometerAccuracy;
    }

    public void setOdometerAccuracy(Integer odometerAccuracy) {
        this.odometerAccuracy = odometerAccuracy;
    }
}
