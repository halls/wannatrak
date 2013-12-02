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
 * 18.12.2008 20:35:32
 */
package org.wannatrak.client.layout;

import org.wannatrak.client.MainWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PushButton;

public abstract class AbstractMainWidgetLayout implements MainWidgetLayout {
    public static final int LABEL_ROW = 0;
    public static final int WIDGET_ROW = 1;
    public static final int BOTTOM_ROW = 2;

    public void setLeftWidgetLabel(MainWidget mainWidget, Label leftWidgetLabel, String cellStyleName) {
        setLeftWidgetLabel(mainWidget, leftWidgetLabel);
        mainWidget.getCellFormatter().setStyleName(getLeftWidgetLabelRow(), getLeftWidgetLabelColumn(), cellStyleName);
    }

    public void setLeftWidgetLabel(MainWidget mainWidget, Label leftWidgetLabel) {
        mainWidget.setWidget(getLeftWidgetLabelRow(), getLeftWidgetLabelColumn(), leftWidgetLabel);
    }


    public void setRightWidgetLabel(MainWidget mainWidget, Label rightWidgetLabel, String cellStyleName) {
        setRightWidgetLabel(mainWidget, rightWidgetLabel);
        mainWidget.getCellFormatter().setStyleName(getRightWidgetLabelRow(), getRightWidgetLabelColumn(), cellStyleName);
    }

    public void setRightWidgetLabel(MainWidget mainWidget, Label rightWidgetLabel) {
        mainWidget.setWidget(getRightWidgetLabelRow(), getRightWidgetLabelColumn(), rightWidgetLabel);
    }


    public void setCenterWidgetHeader(MainWidget mainWidget, Widget centerWidgetHeader, String cellStyleName) {
        setCenterWidgetHeader(mainWidget, centerWidgetHeader);
        mainWidget.getCellFormatter().setStyleName(getCenterWidgetHeaderRow(), getCenterWidgetHeaderColumn(), cellStyleName);
    }

    public void setCenterWidgetHeader(MainWidget mainWidget, Widget centerWidgetHeader) {
        mainWidget.setWidget(getCenterWidgetHeaderRow(), getCenterWidgetHeaderColumn(), centerWidgetHeader);
    }


    public void setLeftWidget(MainWidget mainWidget, Widget leftWidget, String cellStyleName) {
        setLeftWidget(mainWidget, leftWidget);
        mainWidget.getCellFormatter().setStyleName(getLeftWidgetRow(), getLeftWidgetColumn(), cellStyleName);
    }

    public void setLeftWidget(MainWidget mainWidget, Widget leftWidget) {
        mainWidget.setWidget(getLeftWidgetRow(), getLeftWidgetColumn(), leftWidget);
    }


    public void setCenterWidget(MainWidget mainWidget, Widget centerWidget, String cellStyleName) {
        setCenterWidget(mainWidget, centerWidget);
        mainWidget.getCellFormatter().setStyleName(getCenterWidgetRow(), getCenterWidgetColumn(), cellStyleName);
    }

    public void setCenterWidget(MainWidget mainWidget, Widget centerWidget) {
        mainWidget.setWidget(getCenterWidgetRow(), getCenterWidgetColumn(), centerWidget);
    }


    public void setRightWidget(MainWidget mainWidget, Widget rightWidget, String cellStyleName) {
        setRightWidget(mainWidget, rightWidget);
        mainWidget.getCellFormatter().setStyleName(getRightWidgetRow(), getRightWidgetColumn(), cellStyleName);
    }

    public void setRightWidget(MainWidget mainWidget, Widget rightWidget) {
        mainWidget.setWidget(getRightWidgetRow(), getRightWidgetColumn(), rightWidget);
    }


    public void setBottomWidget(MainWidget mainWidget, Widget bottomWidget, String cellStyleName) {
        setBottomWidget(mainWidget, bottomWidget);
        mainWidget.getCellFormatter().setStyleName(getBottomWidgetRow(), getBottomWidgetColumn(), cellStyleName);
    }

    public void setBottomWidget(MainWidget mainWidget, Widget bottomWidget) {
        mainWidget.setWidget(getBottomWidgetRow(), getBottomWidgetColumn(), bottomWidget);
    }


    public void setLeftHideButton(MainWidget mainWidget, PushButton leftHideButton, String cellStyleName) {
        setLeftHideButton(mainWidget, leftHideButton);
        mainWidget.getCellFormatter().setStyleName(getLeftHideButtonRow(), getLeftHideButtonColumn(), cellStyleName);
    }

    public void setLeftHideButton(MainWidget mainWidget, PushButton leftHideButton) {
        mainWidget.setWidget(getLeftHideButtonRow(), getLeftHideButtonColumn(), leftHideButton);
    }


    public void setRightHideButton(MainWidget mainWidget, PushButton rightHideButton, String cellStyleName) {
        setRightHideButton(mainWidget, rightHideButton);
        mainWidget.getCellFormatter().setStyleName(getRightHideButtonRow(), getRightHideButtonColumn(), cellStyleName);
    }

    public void setRightHideButton(MainWidget mainWidget, PushButton rightHideButton) {
        mainWidget.setWidget(getRightHideButtonRow(), getRightHideButtonColumn(), rightHideButton);
    }


    public void setLeftShowButton(MainWidget mainWidget, PushButton leftShowButton, String cellStyleName) {
        setLeftShowButton(mainWidget, leftShowButton);
        mainWidget.getCellFormatter().setStyleName(getLeftShowButtonRow(), getLeftShowButtonColumn(), cellStyleName);
    }

    public void setLeftShowButton(MainWidget mainWidget, PushButton leftShowButton) {
        mainWidget.setWidget(getLeftShowButtonRow(), getLeftShowButtonColumn(), leftShowButton);
    }


    public void setRightShowButton(MainWidget mainWidget, PushButton rightShowButton, String cellStyleName) {
        setRightShowButton(mainWidget, rightShowButton);
        mainWidget.getCellFormatter().setStyleName(getRightShowButtonRow(), getRightShowButtonColumn(), cellStyleName);
    }

    public void setRightShowButton(MainWidget mainWidget, PushButton rightShowButton) {
        mainWidget.setWidget(getRightShowButtonRow(), getRightShowButtonColumn(), rightShowButton);
    }

    protected int getLeftWidgetLabelRow() {
        return LABEL_ROW;
    }

    protected int getLeftWidgetLabelColumn() {
        return getLeftWidgetColumn();
    }

    protected int getLeftWidgetRow() {
        return WIDGET_ROW;
    }

    protected int getLeftWidgetColumn() {
        return 0;
    }

    protected int getCenterWidgetHeaderRow() {
        return LABEL_ROW;
    }

    protected int getCenterWidgetHeaderColumn() {
        return getCenterWidgetColumn();
    }

    protected int getCenterWidgetRow() {
        return WIDGET_ROW;
    }

    protected int getCenterWidgetColumn() {
        return getLeftWidgetColumn() + 2;
    }

    protected int getRightWidgetLabelRow() {
        return LABEL_ROW;
    }

    protected int getRightWidgetLabelColumn() {
        return getRightWidgetColumn();
    }

    protected int getRightWidgetRow() {
        return WIDGET_ROW;
    }

    protected int getRightWidgetColumn() {
        return getCenterWidgetColumn() + 2;
    }

    protected int getBottomWidgetRow() {
        return BOTTOM_ROW;
    }

    protected int getBottomWidgetColumn() {
        return getCenterWidgetColumn();
    }

    protected int getLeftHideButtonRow() {
        return LABEL_ROW;
    }

    protected int getLeftHideButtonColumn() {
        return getLeftWidgetColumn() + 1;
    }

    protected int getLeftShowButtonRow() {
        return LABEL_ROW;
    }

    protected int getLeftShowButtonColumn() {
        return getLeftWidgetColumn();
    }

    protected int getRightHideButtonRow() {
        return LABEL_ROW;
    }

    protected int getRightHideButtonColumn() {
        return getRightWidgetColumn() - 1;
    }

    protected int getRightShowButtonRow() {
        return LABEL_ROW;
    }

    protected int getRightShowButtonColumn() {
        return getRightHideButtonColumn();
    }
}
