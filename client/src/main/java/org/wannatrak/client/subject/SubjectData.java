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
 * 14.07.2008 22:02:03
 */
package org.wannatrak.client.subject;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

public class SubjectData implements IsSerializable {
    public String name;
    public Long subjectId;
    public Date updated;
    public String odometer;
    public SubjectState subjectState;

    public SubjectData() {
    }

    public SubjectData(
            String name,
            Long subjectId,
            Date updated,
            String odometer,
            SubjectState subjectState
    ) {
        this.name = name;
        this.subjectId = subjectId;
        this.updated = updated;
        this.odometer = odometer;
        this.subjectState = subjectState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getOdometer() {
        return odometer;
    }

    public void setOdometer(String odometer) {
        this.odometer = odometer;
    }

    public SubjectState getSubjectState() {
        return subjectState;
    }

    public void setSubjectState(SubjectState subjectState) {
        this.subjectState = subjectState;
    }

    public boolean isTracking() {
        return SubjectState.TRACKING.equals(subjectState);
    }
}
