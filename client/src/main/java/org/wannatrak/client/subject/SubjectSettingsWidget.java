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
 * 16.04.2009
 */
package org.wannatrak.client.subject;

import com.google.gwt.user.client.ui.*;
import org.wannatrak.client.StringConstants;
import org.wannatrak.client.Mediator;

public class SubjectSettingsWidget extends VerticalPanel {

    private final StringConstants stringConstants = StringConstants.StringConstantsSingleton.getInstance();

    private final Mediator mediator;

    private final SubjectsController subjectsController;

    private final TextBox nameTextBox;
    private final HTML nameWarning;

    private final ListBox sendPeriodHoursListBox;
    private final ListBox sendPeriodMinsListBox;
    private final HTML sendPeriodWarning;

    private final ListBox savePeriodHoursListBox;
    private final ListBox savePeriodMinsListBox;
    private final ListBox savePeriodSecsListBox;
    private final HTML savePeriodWarning;

    private final Button saveButton;

    private final HTML statusLabel;

    private SubjectSettingsData modifiedSettings;

    public SubjectSettingsWidget(Mediator mediator, SubjectsController subjectsController) {
        this.mediator = mediator;

        this.subjectsController = subjectsController;
        subjectsController.setSubjectSettingsWidget(this);

        setStylePrimaryName("settingsPanel");
        setSpacing(7);

        final Label nameLabel = new Label(stringConstants.subjectName() + stringConstants.labelColon());
        nameLabel.setStylePrimaryName("nameLabel");

        nameTextBox = new TextBox();
        nameTextBox.setStylePrimaryName("nameInputBox");
        nameWarning = new HTML();
        nameWarning.setStylePrimaryName("settingsWarning");

        final HorizontalPanel sendPeriodLabelPanel = createSendPeriodLabelPanel();

        sendPeriodHoursListBox = createHoursListBox();
        sendPeriodMinsListBox = createMinutesListBox();
        sendPeriodWarning = new HTML();
        sendPeriodWarning.setStylePrimaryName("settingsWarning");

        final HorizontalPanel sendPeriodInputPanel = createSendPeriodInputPanel();

        final HorizontalPanel savePeriodLabelPanel = createSavePeriodLabelPanel();

        savePeriodHoursListBox = createHoursListBox();
        savePeriodMinsListBox = createMinutesListBox();
        savePeriodSecsListBox = createSecondsListBox();
        savePeriodWarning = new HTML();
        savePeriodWarning.setStylePrimaryName("settingsWarning");

        final HorizontalPanel savePeriodInputPanel = createSavePeriodInputPanel();

        saveButton = createSaveButton();

        statusLabel = new HTML();
        statusLabel.setStylePrimaryName("statusLabel");
        statusLabel.setVisible(false);

        add(nameLabel);
        add(nameTextBox);
        add(nameWarning);

        add(savePeriodLabelPanel);
        add(savePeriodInputPanel);
        add(savePeriodWarning);

        add(sendPeriodLabelPanel);
        add(sendPeriodInputPanel);
        add(sendPeriodWarning);

        add(saveButton);
        setCellHorizontalAlignment(saveButton, ALIGN_CENTER);

        add(statusLabel);
    }

    public SubjectSettingsData getModifiedSettings() {
        return modifiedSettings;
    }

    public void setSubjectInfoData(SubjectInfoData subjectInfoData) {
        hideStatusLabel();
        nameWarning.setVisible(false);
        savePeriodWarning.setVisible(false);
        sendPeriodWarning.setVisible(false);

        nameTextBox.setText(subjectInfoData.getName());

        final int sendPeriod = subjectInfoData.getSendPeriod();
        final int sendPeriodHours = sendPeriod / 60;
        final int sendPeriodMins = sendPeriod - sendPeriodHours * 60;

        sendPeriodHoursListBox.setSelectedIndex(sendPeriodHours);
        sendPeriodMinsListBox.setSelectedIndex(sendPeriodMins);

        final int savePeriod = subjectInfoData.getSavePeriod();
        final int savePeriodHours = savePeriod / (60 * 60);
        final int savePeriodMins = (savePeriod - savePeriodHours * 60 * 60) / 60;
        final int savePeriodSecs = savePeriod - savePeriodHours * 60 * 60 - savePeriodMins * 60;

        savePeriodHoursListBox.setSelectedIndex(savePeriodHours);
        savePeriodMinsListBox.setSelectedIndex(savePeriodMins);
        savePeriodSecsListBox.setSelectedIndex(savePeriodSecs);
    }

    public void hideStatusLabel() {
        statusLabel.setVisible(false);
    }

    public boolean isDataModified() {
        final SubjectInfoData subjectInfoData = subjectsController.getSubjectInfoData();

        return !nameTextBox.getText().equals(subjectInfoData.getName())
                || !getSavePeriod().equals(subjectInfoData.getSavePeriod())
                || !getSendPeriod().equals(subjectInfoData.getSendPeriod());
    }

    public void enableSaveButton() {
        saveButton.setEnabled(true);
    }

    public void showNameWarning(String warning) {
        hideStatusLabel();

        nameWarning.setHTML(warning);
        nameWarning.setVisible(true);
        nameTextBox.setFocus(true);
    }

    private HorizontalPanel createSavePeriodInputPanel() {
        final HorizontalPanel savePeriodInputPanel = new HorizontalPanel();
        savePeriodInputPanel.setSpacing(7);
        savePeriodInputPanel.add(savePeriodHoursListBox);
        savePeriodInputPanel.add(savePeriodMinsListBox);
        savePeriodInputPanel.add(savePeriodSecsListBox);
        return savePeriodInputPanel;
    }

    private HorizontalPanel createSavePeriodLabelPanel() {
        final PopupPanel savePeriodInfo = new PopupPanel(true);
        savePeriodInfo.setStylePrimaryName("savePeriodInfo");
        savePeriodInfo.setAnimationEnabled(true);

        final Label savePeriodInfoCloseLabel = new Label(stringConstants.close());
        savePeriodInfoCloseLabel.setStylePrimaryName("subjectInfoCloseLabel");
        savePeriodInfoCloseLabel.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                savePeriodInfo.hide();
            }
        });
        
        final Widget savePeriodInfoWidget = new HTML(stringConstants.savePeriodInfo());
        savePeriodInfoWidget.setStylePrimaryName("savePeriodInfoWidget");

        final VerticalPanel savePeriodInfoContainer = new VerticalPanel();
        savePeriodInfoContainer.setHorizontalAlignment(ALIGN_RIGHT);
        savePeriodInfoContainer.add(savePeriodInfoCloseLabel);
        savePeriodInfoContainer.add(savePeriodInfoWidget);


        savePeriodInfo.setWidget(savePeriodInfoContainer);


        final Label savePeriodLabel = new Label(stringConstants.savePeriod() + stringConstants.labelColon());
        savePeriodLabel.setStylePrimaryName("subjectInfoInputLabel");
        final Label savePeriodQues = new Label(stringConstants.ques());
        savePeriodQues.setStylePrimaryName("subjectInfoQues");
        savePeriodQues.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                savePeriodInfo.setPopupPosition(
                        SubjectSettingsWidget.this.getAbsoluteLeft() - 30,
                        savePeriodHoursListBox.getAbsoluteTop() + 30
                );
                savePeriodInfo.show();
                savePeriodQues.addStyleDependentName("clicked");
            }
        });

        final HorizontalPanel savePeriodLabelPanel = new HorizontalPanel();
        savePeriodLabelPanel.setVerticalAlignment(ALIGN_BOTTOM);
        savePeriodLabelPanel.add(savePeriodLabel);
        savePeriodLabelPanel.add(savePeriodQues);
        return savePeriodLabelPanel;
    }

    private HorizontalPanel createSendPeriodInputPanel() {
        final HorizontalPanel sendPeriodInputPanel = new HorizontalPanel();
        sendPeriodInputPanel.setSpacing(7);
        sendPeriodInputPanel.add(sendPeriodHoursListBox);
        sendPeriodInputPanel.add(sendPeriodMinsListBox);
        return sendPeriodInputPanel;
    }

    private HorizontalPanel createSendPeriodLabelPanel() {
        final PopupPanel sendPeriodInfo = new PopupPanel(true);
        sendPeriodInfo.setStylePrimaryName("sendPeriodInfo");
        sendPeriodInfo.setAnimationEnabled(true);

        final Label sendPeriodInfoCloseLabel = new Label(stringConstants.close());
        sendPeriodInfoCloseLabel.setStylePrimaryName("subjectInfoCloseLabel");
        sendPeriodInfoCloseLabel.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                sendPeriodInfo.hide();
            }
        });

        final Widget sendPeriodInfoWidget = new HTML(stringConstants.sendPeriodInfo());
        sendPeriodInfoWidget.setStylePrimaryName("sendPeriodInfoWidget");

        final VerticalPanel sendPeriodInfoContainer = new VerticalPanel();
        sendPeriodInfoContainer.setHorizontalAlignment(ALIGN_RIGHT);
        sendPeriodInfoContainer.add(sendPeriodInfoCloseLabel);
        sendPeriodInfoContainer.add(sendPeriodInfoWidget);


        sendPeriodInfo.setWidget(sendPeriodInfoContainer);



        final Label sendPeriodLabel = new Label(stringConstants.sendPeriod() + stringConstants.labelColon());
        sendPeriodLabel.setStylePrimaryName("subjectInfoInputLabel");
        final Label sendPeriodQues = new Label(stringConstants.ques());
        sendPeriodQues.setStylePrimaryName("subjectInfoQues");
        sendPeriodQues.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                sendPeriodInfo.setPopupPosition(
                        SubjectSettingsWidget.this.getAbsoluteLeft() - 30,
                        sendPeriodHoursListBox.getAbsoluteTop() + 30
                );
                sendPeriodInfo.show();
                sendPeriodQues.addStyleDependentName("clicked");
            }
        });

        final HorizontalPanel sendPeriodLabelPanel = new HorizontalPanel();
        sendPeriodLabelPanel.setVerticalAlignment(ALIGN_BOTTOM);
        sendPeriodLabelPanel.add(sendPeriodLabel);
        sendPeriodLabelPanel.add(sendPeriodQues);
        return sendPeriodLabelPanel;
    }

    private Button createSaveButton() {
        final Button saveButton = new Button(stringConstants.save());
        saveButton.setStylePrimaryName("saveButton");
        saveButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                nameWarning.setVisible(false);

                boolean valid = true;
                final int savePeriod = getSavePeriod();
                if (savePeriod == 0) {
                    valid = false;
                    savePeriodWarning.setHTML(stringConstants.zeroSavePeriod());
                    savePeriodWarning.setVisible(true);
                } else {
                    savePeriodWarning.setVisible(false);
                }

                final int sendPeriod = getSendPeriod();
                if (sendPeriod == 0) {
                    valid = false;
                    sendPeriodWarning.setHTML(stringConstants.zeroSendPeriod());
                    sendPeriodWarning.setVisible(true);
                } else {
                    sendPeriodWarning.setVisible(false);
                }

                if (valid && isDataModified()) {
                    modifiedSettings = new SubjectSettingsData(
                            nameTextBox.getText(),
                            getSavePeriod(),
                            getSendPeriod()
                    );
                    saveButton.setEnabled(false);

                    statusLabel.setHTML(stringConstants.saveSettingsWaiting());
                    statusLabel.setVisible(true);

                    SubjectSettingsWidget.this.subjectsController.saveSubjectSettings();
                }
            }
        });
        return saveButton;
    }

    private ListBox createSecondsListBox() {
        final ListBox listBox = new ListBox();
        for (int i = 0; i < 60; i++) {
            final String minute = ":" + (i < 10 ? "0" + i : i);
            listBox.addItem(minute, Integer.toString(i));
        }
        return listBox;
    }


    private ListBox createMinutesListBox() {
        final ListBox listBox = new ListBox();
        for (int i = 0; i < 60; i++) {
            final String minute = ":" + (i < 10 ? "0" + i : i);
            listBox.addItem(minute, Integer.toString(i));
        }
        return listBox;
    }

    private ListBox createHoursListBox() {
        final ListBox listBox = new ListBox();
        for (int i = 0; i < 24; i++) {
            final String hour = i < 10 ? "0" + i : String.valueOf(i);
            listBox.addItem(hour, Integer.toString(i));
        }
        return listBox;
    }

    private Integer getSavePeriod() {
        int secsInHours = Integer.parseInt(
                savePeriodHoursListBox.getValue(savePeriodHoursListBox.getSelectedIndex())
        ) * 60 * 60;
        int secsInMins = Integer.parseInt(
                savePeriodMinsListBox.getValue(savePeriodMinsListBox.getSelectedIndex())
        ) * 60;
        int secs = Integer.parseInt(
                savePeriodSecsListBox.getValue(savePeriodSecsListBox.getSelectedIndex())
        );
        return secsInHours + secsInMins + secs;
    }

    private Integer getSendPeriod() {
        int minsInHours = Integer.parseInt(
                sendPeriodHoursListBox.getValue(sendPeriodHoursListBox.getSelectedIndex())
        ) * 60;
        int mins = Integer.parseInt(
                sendPeriodMinsListBox.getValue(sendPeriodMinsListBox.getSelectedIndex())
        );
        return minsInHours + mins;
    }
}
