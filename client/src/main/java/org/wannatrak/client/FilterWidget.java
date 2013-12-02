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
 * 12.01.2009 23:23:53
 */
package org.wannatrak.client;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

import java.util.Date;

public class FilterWidget extends VerticalPanel {

    private final Mediator mediator;

    private ListBox fromDayListBox;
    private ListBox fromHourListBox;
    private ListBox fromMinuteListBox;

    private ListBox toDayListBox;
    private ListBox toHourListBox;
    private ListBox toMinuteListBox;

    private CheckBox showErrorsCheckBox;

    public FilterWidget(Mediator mediator) {
        this.mediator = mediator;
        mediator.setFilterWidget(this);

        setSpacing(7);

        final StringConstants stringConstants = StringConstants.StringConstantsSingleton.getInstance();
        final Label label = new Label(stringConstants.timeInterval());
        label.setStylePrimaryName("filterHeader");

        final ChangeHandler changeHandler = new ChangeHandler() {
            public void onChange(ChangeEvent event) {

                FilterWidget.this.mediator.showSubjects();
            }
        };

        final Label from = new Label(stringConstants.fromTimeInterval());
        from.setStylePrimaryName("filterLabel");

        fromDayListBox = createDayListBox();
        fromDayListBox.setItemSelected(0, true);

        final DateBox fromDateBox = new DateBox();
        fromDateBox.addValueChangeHandler(new ListBoxValueChangeHandler(fromDateBox, fromDayListBox));
        fromDateBox.setWidth("15em");
        fromDateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFullDateFormat()));
        fromDateBox.getDatePicker().setWidth("12.5em");
        fromDateBox.setValue(new Date());

        final ListBoxChangeHandler fromListBoxChangeHandler = new ListBoxChangeHandler(fromDateBox, fromDayListBox);
        fromDayListBox.addChangeHandler(fromListBoxChangeHandler);

        fromHourListBox = createHourListBox();
        fromHourListBox.addChangeHandler(changeHandler);
        fromHourListBox.setItemSelected(0, true);

        fromMinuteListBox = createMinuteListBox();
        fromMinuteListBox.addChangeHandler(changeHandler);
        fromMinuteListBox.setItemSelected(0, true);

        final Label to = new Label(stringConstants.toTimeInterval());
        to.setStylePrimaryName("filterLabel");

        toDayListBox = createDayListBox();
        toDayListBox.setItemSelected(0, true);

        final DateBox toDateBox = new DateBox();
        toDateBox.setWidth("15em");
        toDateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFullDateFormat()));
        toDateBox.getDatePicker().setWidth("12.5em");
        toDateBox.setValue(new Date());
        toDateBox.addValueChangeHandler(new ListBoxValueChangeHandler(toDateBox, toDayListBox));

        final ListBoxChangeHandler toListBoxChangeHandler = new ListBoxChangeHandler(toDateBox, toDayListBox);
        toDayListBox.addChangeHandler(toListBoxChangeHandler);

        toHourListBox = createHourListBox();
        toHourListBox.addChangeHandler(changeHandler);
        toHourListBox.setItemSelected(23, true);

        toMinuteListBox = createMinuteListBox();
        toMinuteListBox.addChangeHandler(changeHandler);
        toMinuteListBox.setItemSelected(59, true);

        final Grid grid = new Grid(2, 5);
        grid.setCellPadding(7);
        grid.setCellSpacing(7);

        grid.setWidget(0, 0, from);
        grid.setWidget(0, 1, fromDateBox);
        grid.setWidget(0, 2, fromDayListBox);
        grid.setWidget(0, 3, fromHourListBox);
        grid.setWidget(0, 4, fromMinuteListBox);

        grid.setWidget(1, 0, to);
        grid.setWidget(1, 1, toDateBox);
        grid.setWidget(1, 2, toDayListBox);
        grid.setWidget(1, 3, toHourListBox);
        grid.setWidget(1, 4, toMinuteListBox);

        showErrorsCheckBox = new CheckBox("&nbsp;" + stringConstants.showWithErrors(), true);
        showErrorsCheckBox.setStylePrimaryName("filterLabel");
        showErrorsCheckBox.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                FilterWidget.this.mediator.showSubjects();
            }
        });

        add(label);
        add(grid);
        add(showErrorsCheckBox);
    }

    public String getFromDaysAgo() {
        return fromDayListBox.getValue(fromDayListBox.getSelectedIndex());
    }

    public String getFromHour() {
        return fromHourListBox.getValue(fromHourListBox.getSelectedIndex());
    }

    public String getFromMinute() {
        return fromMinuteListBox.getValue(fromMinuteListBox.getSelectedIndex());
    }

    public String getToDaysAgo() {
        return toDayListBox.getValue(toDayListBox.getSelectedIndex());
    }

    public String getToHour() {
        return toHourListBox.getValue(toHourListBox.getSelectedIndex());
    }

    public String getToMinute() {
        return toMinuteListBox.getValue(toMinuteListBox.getSelectedIndex());
    }

    public boolean isShowWithErrors() {
        return showErrorsCheckBox.getValue();
    }

    private ListBox createMinuteListBox() {
        final ListBox minuteListBox = new ListBox();
        for (int i = 0; i < 60; i++) {
            final String minute = ":" + (i < 10 ? "0" + i : i);
            minuteListBox.addItem(minute, Integer.toString(i));
        }

        return minuteListBox;
    }

    private ListBox createHourListBox() {
        final ListBox hourListBox = new ListBox();
        for (int i = 0; i < 24; i++) {
            final String hour = i < 10 ? "0" + i : String.valueOf(i);
            hourListBox.addItem(hour, Integer.toString(i));
        }
        return hourListBox;
    }

    private ListBox createDayListBox() {
        final StringConstants stringConstants = StringConstants.StringConstantsSingleton.getInstance();

        final ListBox dayListBox = new ListBox();
        dayListBox.addItem(stringConstants.today(), Integer.toString(0));
        dayListBox.addItem(stringConstants.yesterday(), Integer.toString(1));
        dayListBox.addItem(stringConstants.twoDaysAgo(), Integer.toString(2));
        dayListBox.addItem(stringConstants.threeDaysAgo(), Integer.toString(3));
        dayListBox.addItem(stringConstants.fourDaysAgo(), Integer.toString(4));
        dayListBox.addItem(stringConstants.fiveDaysAgo(), Integer.toString(5));
        dayListBox.addItem(stringConstants.sixDaysAgo(), Integer.toString(6));
        dayListBox.addItem(stringConstants.weekAgo(), Integer.toString(7));
        dayListBox.addItem(stringConstants.eightDaysAgo(), Integer.toString(8));
        dayListBox.addItem(stringConstants.nineDaysAgo(), Integer.toString(9));
        dayListBox.addItem(stringConstants.tenDaysAgo(), Integer.toString(10));
        dayListBox.addItem(stringConstants.elevenDaysAgo(), Integer.toString(11));
        dayListBox.addItem(stringConstants.twelveDaysAgo(), Integer.toString(12));
        dayListBox.addItem(stringConstants.thirteenDaysAgo(), Integer.toString(13));
        dayListBox.addItem(stringConstants.twoWeeksAgo(), Integer.toString(14));
        dayListBox.addItem(stringConstants.monthAgo(), Integer.toString(28));
        dayListBox.addItem(stringConstants.twoMonthesAgo(), Integer.toString(56));
        dayListBox.addItem(stringConstants.halfYearAgo(), Integer.toString(180));
        dayListBox.addItem(stringConstants.yearAgo(), Integer.toString(365));
        return dayListBox;
    }


    private int listBoxValueCount(ListBox listBox, String value) {
        int i = 0;
        while ((i < listBox.getItemCount()) && (!listBox.getValue(i).equals(value))) {
            i++;
        }
        return i;
    }

    class ListBoxChangeHandler implements ChangeHandler {
        private final DateBox dateBox;
        private final ListBox listBox;

        private ListBoxChangeHandler(DateBox dateBox, ListBox listBox) {
            this.dateBox = dateBox;
            this.listBox = listBox;
        }

        public void onChange(ChangeEvent event) {
            Date date = new Date(new Date().getTime() -
                    Long.parseLong(listBox.getValue(listBox.getSelectedIndex())) * 86400000);
            dateBox.setValue(date);
            listBox.getElement().getElementsByTagName("option").
                    getItem(listBox.getSelectedIndex()).setClassName(null);
            FilterWidget.this.mediator.showSubjects();
        }
    }

    class ListBoxValueChangeHandler implements ValueChangeHandler<Date> {
        private final DateBox dateBox;
        private final ListBox listBox;

        private ListBoxValueChangeHandler(DateBox dateBox, ListBox listBox) {
            this.dateBox = dateBox;
            this.listBox = listBox;
        }

        public void onValueChange(ValueChangeEvent valueChangeEvent) {
            Date now = new Date();
            Long days = (now.getTime() -
                    dateBox.getDatePicker().getValue().getTime()) / 86400000;

            if (days <= 0) {
                dateBox.setValue(now);
                listBox.setItemSelected(0, true);
                FilterWidget.this.mediator.showSubjects();
                return;
            }

            int index = listBoxValueCount(listBox, Long.toString(days));
            if (index < listBox.getItemCount()) {
                listBox.setItemSelected(index, true);

            } else {
                int i = 0;
                while (
                        (i < listBox.getItemCount()) &&
                        (Integer.valueOf(listBox.getValue(i)) < days)) {
                    i++;
                }
                listBox.insertItem(
                        Messages.MessagesSingleton.getInstance().multipleDays(days.intValue()),
                        Long.toString(days),
                        i
                );
                listBox.setItemSelected(i, true);
            }

            FilterWidget.this.mediator.showSubjects();
        }
    }
}
