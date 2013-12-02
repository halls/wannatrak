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
 * 25.03.2009
 */
package org.wannatrak.client.subject;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.i18n.client.NumberFormat;
import org.wannatrak.client.Mediator;
import org.wannatrak.client.StringConstants;

public class SubjectInfoWidget extends VerticalPanel {

    private final StringConstants stringConstants = StringConstants.StringConstantsSingleton.getInstance();

    private final Mediator mediator;

    private final SubjectsController subjectsController;

    private final Button removeButton;

    private final Label subjectStateValue;
    private final Label speedLabel;
    private final Label speedValue;
    private final Label altitudeLabel;
    private final Label altitudeValue;
    private final Grid infoGrid;

    private final SubjectSettingsWidget settingsWidget;

    private final HTML errorLabel;
    private final HTML waitingLabel;

    private final Label repeatQueryHyperlink;

    private SubjectInfoData subjectInfoData;

    public SubjectInfoWidget(Mediator mediator, SubjectsController subjectsController) {
        this.mediator = mediator;
        mediator.setSubjectInfoWidget(this);

        this.subjectsController = subjectsController;
        subjectsController.setSubjectInfoWidget(this);

        setStylePrimaryName("subjectInfoWidget");
        setSpacing(7);
        setHorizontalAlignment(ALIGN_CENTER);

        removeButton = new Button(stringConstants.removeSubject());
        removeButton.setStylePrimaryName("removeButton");
        removeButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                if (Window.confirm(stringConstants.confirmToDeleteSubject())) {
                    SubjectInfoWidget.this.subjectsController.removeSubject();
                }
            }
        });

        final Label subjectStateLabel = new Label(stringConstants.subjectState() + stringConstants.labelColon());
        subjectStateLabel.setStylePrimaryName("subjectStateLabel");
        subjectStateValue = new Label();
        subjectStateValue.setStylePrimaryName("subjectStateValue");

        speedLabel = new Label(stringConstants.speed() + stringConstants.labelColon());
        speedLabel.setStylePrimaryName("speedLabel");
        speedValue = new Label();
        speedValue.setStylePrimaryName("speedValue");

        altitudeLabel = new Label(stringConstants.altitude() + stringConstants.labelColon());
        altitudeLabel.setStylePrimaryName("altitudeLabel");
        altitudeValue = new Label();
        altitudeValue.setStylePrimaryName("altitudeValue");

        infoGrid = createInfoGrid(subjectStateLabel);

        settingsWidget = new SubjectSettingsWidget(mediator, subjectsController);

        errorLabel = new HTML();
        errorLabel.setStylePrimaryName("subjectsErrorLabel");

        waitingLabel = new HTML();
        waitingLabel.setStylePrimaryName("subjectsWaitingLabel");

        repeatQueryHyperlink = new Label(stringConstants.repeatQuery());
        repeatQueryHyperlink.setStylePrimaryName("retryHyperlink");
        repeatQueryHyperlink.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                SubjectInfoWidget.this.subjectsController.showSubjectInfo();
                repeatQueryHyperlink.addStyleDependentName("clicked");
            }
        });
    }

    public void showError(String msg) {
        hideWaiting();
        hideInfo();

        setHorizontalAlignment(ALIGN_LEFT);

        errorLabel.setHTML(msg);
        add(errorLabel);
        add(repeatQueryHyperlink);
    }

    public void hideError() {
        remove(errorLabel);
        remove(repeatQueryHyperlink);

        setHorizontalAlignment(ALIGN_CENTER);
    }

    public void showWaiting(String msg) {
        hideError();
        hideInfo();

        waitingLabel.setHTML(msg);
        add(waitingLabel);
    }

    public void hideWaiting() {
        remove(waitingLabel);
    }

    public void hideInfo() {
        remove(removeButton);
        remove(infoGrid);
        remove(settingsWidget);
    }

    public void setSubjectInfoData(SubjectInfoData subjectInfoData) {

        final SubjectState subjectState = subjectInfoData.getSubjectState();
        subjectStateValue.setText(getDisplayName(subjectState));

        if (SubjectState.TRACKING.equals(subjectState)) {
            final Double speed = subjectInfoData.getSpeed();
            if (speed == null) {
                speedValue.setText(stringConstants.noData());
                speedValue.addStyleDependentName("noData");
            } else {
                speedValue.setText(NumberFormat.getFormat("##0.0").format(speed) + " " + stringConstants.speedUnits());
                speedValue.removeStyleDependentName("noData");
            }

            final Double altitude = subjectInfoData.getAltitude();
            if (altitude == null) {
                altitudeValue.setText(stringConstants.noData());
                altitudeValue.addStyleDependentName("noData");
            } else {
                altitudeValue.setText(altitude.intValue() + " " + stringConstants.altitudeUnits());
                altitudeValue.removeStyleDependentName("noData");
            }

            showPositionDetails();
        } else {
            hidePosotionDetails();
        }

        if (this.subjectInfoData == null
                || !(subjectInfoData.getId().equals(this.subjectInfoData.getId()) && settingsWidget.isDataModified())
        ) {
            settingsWidget.setSubjectInfoData(subjectInfoData);
        }
        enableSaveButton();

        this.subjectInfoData = subjectInfoData;
        showInfo();
    }

    public void enableSaveButton() {
        settingsWidget.enableSaveButton();
    }
    
    public void showNameWarning(String warning) {
        settingsWidget.showNameWarning(warning);
    }

    public SubjectInfoData getSubjectInfoData() {
        return subjectInfoData;
    }

    private void showPositionDetails() {
        infoGrid.resizeRows(3);

        infoGrid.setWidget(1, 0, speedLabel);
        infoGrid.setWidget(1, 1, speedValue);

        infoGrid.setWidget(2, 0, altitudeLabel);
        infoGrid.setWidget(2, 1, altitudeValue);
    }

    private void hidePosotionDetails() {
        infoGrid.resizeRows(1);
    }

    private void showInfo() {
        add(removeButton);
        setCellHorizontalAlignment(removeButton, ALIGN_RIGHT);
        
        add(infoGrid);
        add(settingsWidget);
    }

    private Grid createInfoGrid(Label subjectStateLabel) {
        final Grid infoGrid = new Grid(1, 2);
        infoGrid.setCellSpacing(12);
        infoGrid.getCellFormatter().setVerticalAlignment(0, 0, ALIGN_TOP);
        infoGrid.getCellFormatter().setVerticalAlignment(0, 1, ALIGN_TOP);
        infoGrid.setWidget(0, 0, subjectStateLabel);
        infoGrid.setWidget(0, 1, subjectStateValue);

        return infoGrid;
    }

    private String getDisplayName(SubjectState subjectState) {
        switch (subjectState) {
            case TRACKING:
                return stringConstants.subjectTracking();
            case CONNECTION_FAILED:
                return stringConstants.subjectConnectionFailed();
            case SWITCHED_OFF:
                return stringConstants.subjectSwitchedOff();
            case LOCATION_NOT_DEFINED:
                return stringConstants.subjectLocationNotDefined();
            default:
                return stringConstants.subjectConnectionFailed();
        }
    }
}
