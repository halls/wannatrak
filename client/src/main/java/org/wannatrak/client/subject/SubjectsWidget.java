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
 * 16.12.2008 13:41:00
 */
package org.wannatrak.client.subject;

import com.google.gwt.user.client.ui.*;
import org.wannatrak.client.Mediator;
import org.wannatrak.client.StringConstants;

import java.util.Map;
import java.util.HashMap;

public class SubjectsWidget extends VerticalPanel {

    private final Mediator mediator;
    private final SubjectsController subjectsController;

    private final HTML errorLabel;
    private final HTML waitingLabel;
    private final Label repeatQueryHyperlink;

    private final Map<Long, SubjectItem> subjectItems;

    public SubjectsWidget(Mediator mediator, SubjectsController subjectsController) {
        this.mediator = mediator;
        mediator.setSubjectsWidget(this);

        this.subjectsController = subjectsController;
        subjectsController.setSubjectsWidget(this);

        setStylePrimaryName("subjectsWidget");
        setSpacing(12);

        errorLabel = new HTML();
        errorLabel.setStylePrimaryName("subjectsErrorLabel");

        waitingLabel = new HTML();
        waitingLabel.setStylePrimaryName("subjectsWaitingLabel");

        repeatQueryHyperlink = new Label(StringConstants.StringConstantsSingleton.getInstance().repeatQuery());
        repeatQueryHyperlink.setStylePrimaryName("retryHyperlink");
        repeatQueryHyperlink.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                SubjectsWidget.this.subjectsController.requestSubjects();
                repeatQueryHyperlink.addStyleDependentName("clicked");
            }
        });

        subjectItems = new HashMap<Long, SubjectItem>();
    }

    public void clearSubjects() {
        for (SubjectItem subjectItem : subjectItems.values()) {
            remove(subjectItem);
        }
        subjectItems.clear();
    }

    public void addSubject(SubjectData subjectData, boolean checked) {
        SubjectItem subjectItem = subjectItems.get(subjectData.getSubjectId());
        if (subjectItem == null) {
            subjectItem = new SubjectItem(
                    subjectsController,
                    subjectData,
                    checked
            );
            subjectItems.put(
                    subjectData.getSubjectId(),
                    subjectItem
            );

            add(subjectItem);
        } else {
            subjectItem.setSubjectData(subjectData);
            subjectItem.setChecked(checked);
        }
    }

    public void resetSubjects() {
        for (SubjectItem subjectItem : subjectItems.values()) {
            subjectItem.setChecked(false);
        }
    }

    public void checkSubject(Long id) {
        final SubjectItem subjectItem = subjectItems.get(id);
        if (subjectItem == null) {
            return;
        }

        subjectItem.setChecked(true);
    }

    public void showError(String msg) {
        hideWaiting();

        errorLabel.setHTML(msg);
        add(errorLabel);
        add(repeatQueryHyperlink);
    }

    public void hideError() {
        remove(errorLabel);
        remove(repeatQueryHyperlink);
    }

    public void showWaiting(String msg) {
        hideError();

        waitingLabel.setHTML(msg);
        add(waitingLabel);
    }

    public void hideWaiting() {
        remove(waitingLabel);
    }

    public void hideSubjectLoading(Long subjectId) {
        final SubjectItem subjectItem = subjectItems.get(subjectId);
        if (subjectItem != null) {
            subjectItem.hideLoading();
        }
    }

    public void showSubjectLoading(Long subjectId) {
        final SubjectItem subjectItem = subjectItems.get(subjectId);
        if (subjectItem != null) {
            subjectItem.showLoading();
        }
    }

    public SubjectData getSubjectData(Long subjectId) {
        final SubjectItem subjectItem = subjectItems.get(subjectId);
        if (subjectItem != null) {
            return subjectItem.getSubjectData();
        } else {
            return null;
        }
    }

    public void selectSubjectItem(long id) {
        final SubjectItem subjectItem = subjectItems.get(id);
        if (subjectItem != null) {
            subjectItem.select();
        }
    }

    public void unselectSubjectItem(long id) {
        final SubjectItem subjectItem = subjectItems.get(id);
        if (subjectItem != null) {
            subjectItem.unselect();
        }
    }
}
