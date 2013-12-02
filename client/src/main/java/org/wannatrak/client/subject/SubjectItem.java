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
 * 08.01.2009 22:06:05
 */
package org.wannatrak.client.subject;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.wannatrak.client.demo.DemoSubjectsController;

import java.util.Date;

public class SubjectItem extends FocusPanel {
    private final SubjectsController subjectsController;

    private final Long id;
    private final CheckBox checkBox;
    private final Label name;
    private final Label updated;
    private final Image loading;

    private SubjectData subjectData;

    public SubjectItem(
            SubjectsController subjectsController,
            SubjectData subjectData,
            boolean checked
    ) {
        this.subjectsController = subjectsController;
        this.id = subjectData.getSubjectId();

        setStylePrimaryName("subjectItem");
        if (subjectsController instanceof DemoSubjectsController) {
            addStyleDependentName("demo");
        }

        checkBox = new CheckBox();
        setChecked(checked);
        checkBox.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                if (checkBox.isChecked()) {
                    SubjectItem.this.subjectsController.checkSubject(SubjectItem.this.id);
                } else {
                    SubjectItem.this.subjectsController.uncheckSubject(SubjectItem.this.id);
                    hideLoading();
                }
            }
        });

        final ClickListener clickListener = new ClickListener() {
            public void onClick(Widget sender) {
                if (checkBox.isChecked() || ! (SubjectItem.this.subjectsController instanceof DemoSubjectsController)) {
                    SubjectItem.this.subjectsController.showSubjectInfo(id);
                }
            }
        };

        name = new Label();
        name.setStylePrimaryName("subjectItemName");

        updated = new Label();
        updated.setStylePrimaryName("subjectItemUpdated");
        updated.setWordWrap(false);

        loading = new Image("img/loading.gif");
        loading.setStylePrimaryName("subjectLoading");
        loading.setVisible(false);

        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setVerticalAlignment(HorizontalPanel.ALIGN_BOTTOM);
        horizontalPanel.add(checkBox);
        horizontalPanel.add(name);
        horizontalPanel.add(updated);
        horizontalPanel.add(loading);

        add(horizontalPanel);

        addClickListener(clickListener);

        setSubjectData(subjectData);
    }

    public SubjectData getSubjectData() {
        return subjectData;
    }

    public void setChecked(boolean checked) {
        this.checkBox.setChecked(checked);
        if (subjectsController instanceof DemoSubjectsController) {
            if (checked) {
                addStyleDependentName("demoChecked");
            } else {
                removeStyleDependentName("demoChecked");
            }
        }
    }

    public void hideLoading() {
        loading.setVisible(false);
    }

    public void showLoading() {
        loading.setVisible(true);
    }

    private String formatDate(Date updated) {
        if (updated == null) {
            return "";
        }
        return DateTimeFormat.getShortDateTimeFormat().format(updated);
    }

    public void setSubjectData(SubjectData subjectData) {
        this.subjectData = subjectData;
        name.setText(subjectData.getName());
        updated.setText(formatDate(subjectData.getUpdated()));
        if (subjectData.isTracking()) {
            updated.addStyleDependentName("tracking");
        } else {
            updated.removeStyleDependentName("tracking");
        }
    }

    public void select() {
        addStyleDependentName("selected");
    }

    public void unselect() {
        removeStyleDependentName("selected");
    }
}
