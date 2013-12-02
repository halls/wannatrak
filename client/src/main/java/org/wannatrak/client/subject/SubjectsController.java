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
 * 08.01.2009 22:19:05
 */
package org.wannatrak.client.subject;

import org.wannatrak.client.*;
import org.wannatrak.client.layout.command.ShowRightWidgetCommand;
import org.wannatrak.client.layout.command.HideRightWidgetCommand;
import org.wannatrak.client.state.LoggedInState;
import org.wannatrak.client.state.param.LoggedInHistoryTokenParam;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.StatusCodeException;

import java.util.Set;
import java.util.HashSet;

public class SubjectsController extends HistoryTokenParamHandler {
    private static final String SUBJECTS_PARAM_KEY = "subjects=";
    private static final String SUBJECTS_PARAM_REGEX = "[sS][uU][bB][jJ][eE][cC][tT][sS]=((\\d)+,)*&";
    private static final int REFRESH_TIMEOUT = 60000;

    private final StringConstants stringConstants = StringConstants.StringConstantsSingleton.getInstance();
    private final Messages messages = Messages.MessagesSingleton.getInstance();

    private final Mediator mediator;

    private final ServerRequest<SubjectData[]> getSubjectsRequest;
    private final ServerRequest<SubjectData[]> refreshSubjectsRequest;
    private final ServerRequest<PositionData> getLastPositionRequest;
    private final ServerRequest<SubjectInfoData> getSubjectInfoRequest;

    private final Set<Long> checkedSubjects;
    private SubjectsWidget subjectsWidget;
    private SubjectInfoWidget subjectInfoWidget;
    private SubjectSettingsWidget subjectSettingsWidget;
    private Long showInfoSubjectId;
    private Timer timer;

    public SubjectsController(Mediator mediator) {
        this.mediator = mediator;
        checkedSubjects = new HashSet<Long>();

        mediator.setSubjectsController(this);

        getSubjectsRequest = createGetSubjectsRequest();
        refreshSubjectsRequest = createRefreshSubjectsRequest();
        getLastPositionRequest = createGetLastPositionRequest();
        getSubjectInfoRequest = createGetSubjectInfoRequest();

        timer = new Timer() {
            @Override
            public void run() {
                refreshSubjects();
            }
        };
    }

    public HistoryTokenParam getHistoryTokenParam() {
        return LoggedInHistoryTokenParam.subjects;
    }

    public void applyParams(String params) {
        subjectsWidget.resetSubjects();

        if (params != null && !params.isEmpty()) {
            final String[] subjectIds = params.split(",");
            for (String subjectIdStr : subjectIds) {
                try {
                    final Long subjectId = Long.parseLong(subjectIdStr);
                    checkedSubjects.add(subjectId);
                    subjectsWidget.checkSubject(subjectId);
                } catch (NumberFormatException e) {
                }
            }
        }

        mediator.showSubjects(true);
    }

    public void setSubjectsWidget(SubjectsWidget subjectsWidget) {
        this.subjectsWidget = subjectsWidget;
    }

    public void setSubjectInfoWidget(SubjectInfoWidget subjectInfoWidget) {
        this.subjectInfoWidget = subjectInfoWidget;
    }

    public void setSubjectSettingsWidget(SubjectSettingsWidget subjectSettingsWidget) {
        this.subjectSettingsWidget = subjectSettingsWidget;
    }

    public Set<Long> getCheckedSubjects() {
        return checkedSubjects;
    }

    public Long getShowInfoSubjectId() {
        return showInfoSubjectId;
    }

    public void copyCheckedSubjects(SubjectsController subjectsController) {
        checkedSubjects.clear();
        checkedSubjects.addAll(subjectsController.getCheckedSubjects());
    }

    public boolean isInitialized() {
        return subjectsWidget != null;
    }

    public boolean isInitializedForLoggedIn() {
        return subjectInfoWidget != null && subjectSettingsWidget != null && isInitialized();
    }

    public void requestSubjects() {
        checkIsInitialized();

        timer.cancel();

        subjectsWidget.clearSubjects();
        subjectsWidget.showWaiting(this.stringConstants.loading());
        getSubjectsRequest.execute();
    }

    public void refreshSubjects() {
        checkIsInitialized();

        refreshSubjectsRequest.execute();
    }

    public void checkSubject(long id) {
        checkedSubjects.add(id);

        if (showInfoSubjectId != null) {
            getLastPositionRequest.execute();
        }

        final String subjectParams = createSubjectParamsString();

        updateHistoryToken(subjectParams);
    }

    public void showSubjectInfo() {
        if (showInfoSubjectId != null && LoggedInState.getInstance().equals(mediator.getState())) {
            checkIsInitializedForLoggedIn();

            final SubjectData subjectData = subjectsWidget.getSubjectData(showInfoSubjectId);
            if (subjectData == null) {
                return;
            }
            mediator.showSubjectName(subjectData.getName());

            subjectsWidget.selectSubjectItem(showInfoSubjectId);
            subjectInfoWidget.showWaiting(this.stringConstants.loading());
            getSubjectInfoRequest.execute();
        }
    }

    public void showSubjectInfo(long id) {
        checkIsInitialized();

        if (showInfoSubjectId != null) {
            subjectsWidget.unselectSubjectItem(showInfoSubjectId);
        }
        showInfoSubjectId = id;
        subjectsWidget.selectSubjectItem(id);
        if (showInfoSubjectId != null && checkedSubjects.contains(id)) {
            getLastPositionRequest.execute();
        }
        if (LoggedInState.getInstance().equals(mediator.getState())) {
            showSubjectInfo();
        }
    }

    public void uncheckSubject(long id) {
        checkedSubjects.remove(id);

        final String subjectParams = createSubjectParamsString();

        updateHistoryToken(subjectParams);
    }

    public void hideSubjectLoading(Long subjectId) {
        checkIsInitialized();

        subjectsWidget.hideSubjectLoading(subjectId);
    }

    public void showSubjectLoading(Long subjectId) {
        checkIsInitialized();

        subjectsWidget.showSubjectLoading(subjectId);
    }

    public void removeSubject() {
        if (showInfoSubjectId != null) {
            createRemoveSubjectRequest().execute();
        }
    }

    public void saveSubjectSettings() {
        checkIsInitializedForLoggedIn();

        createSaveSubjectSettingsRequest().execute();
    }

    private void checkIsInitialized() {
        if (!isInitialized()) {
            throw new IllegalStateException("SubjectsController is not initailzed!");
        }
    }

    private void checkIsInitializedForLoggedIn() {
        if (!isInitializedForLoggedIn()) {
            throw new IllegalStateException("SubjectsController is not initailzed for logged in!");
        }
    }

    private void updateHistoryToken(String subjectParams) {
        String historyToken = History.getToken();
        int subjectsParamIndex = historyToken.indexOf(SUBJECTS_PARAM_KEY);
        if (subjectsParamIndex >= 0) {
            historyToken = historyToken.replaceFirst(SUBJECTS_PARAM_REGEX, subjectParams);
        } else {
            historyToken = historyToken + HistoryListener.PARAMS_BEGINNING + subjectParams;
        }
        History.newItem(historyToken);
    }


    private String createSubjectParamsString() {
        if (checkedSubjects.isEmpty()) {
            return SUBJECTS_PARAM_KEY + HistoryListener.PARAMS_SEPARATOR;
        }
        final StringBuilder sb = new StringBuilder(SUBJECTS_PARAM_KEY);
        for (Long checkedSubjectId : checkedSubjects) {
            sb.append(checkedSubjectId).append(',');
        }
        sb.append(HistoryListener.PARAMS_SEPARATOR);
        return sb.toString();
    }

    private ServerRequest<SubjectData[]> createGetSubjectsRequest() {
        return new ServerRequest<SubjectData[]>(mediator) {
            @Override
            protected void request() {
                SubjectService.App.getInstance().getSubjects(getAsyncCallback());
            }

            @Override
            protected void handleSuccess(SubjectData[] result) {
                subjectsWidget.hideWaiting();

                if (LoggedInState.getInstance().equals(mediator.getState())) {
                    if (result.length > 0) {
                        mediator.enableRightShowHideButton();
                        new ShowRightWidgetCommand(mediator).execute();
                    } else {
                        new HideRightWidgetCommand(mediator).execute();
                        mediator.disableRightShowHideButton();
                        mediator.pinMapToRight();
                    }
                }
                boolean checkedExists = false;
                for (SubjectData subjectData : result) {
                    boolean checked = checkedSubjects.contains(subjectData.getSubjectId());

                    subjectsWidget.addSubject(subjectData, checked);
                    
                    if (checked && !checkedExists) {
                        showSubjectInfo(subjectData.getSubjectId());

                        checkedExists = true;
                    }
                }
                if (!checkedExists && result.length > 0) {
                    checkSubject(result[0].getSubjectId());
                    showSubjectInfo(result[0].getSubjectId());
                }

                timer.schedule(REFRESH_TIMEOUT);
            }

            @Override
            protected void handleTimeout() {
                subjectsWidget.showError(stringConstants.timeoutExceeded());
            }

            @Override
            protected void handleStatusCodeException(Throwable caught) {
                subjectsWidget.showError(
                        messages.serverErrorWithCode(
                                String.valueOf(((StatusCodeException) caught).getStatusCode())
                        )
                );
            }

            @Override
            protected void handleUncheckedException(Throwable caught) {
                subjectsWidget.showError(stringConstants.serverError());
            }

            @Override
            protected void handleUnknownException(Throwable caught) {
                subjectsWidget.showError(stringConstants.unknownError());
            }
        };
    }

    private ServerRequest<SubjectData[]> createRefreshSubjectsRequest() {
        return new ServerRequest<SubjectData[]>(mediator) {
            @Override
            protected void request() {
                SubjectService.App.getInstance().getSubjects(getAsyncCallback());
            }

            @Override
            protected void handleSuccess(SubjectData[] result) {
                subjectsWidget.clearSubjects();

                boolean showSubject = false;
                for (SubjectData subjectData : result) {
                    subjectsWidget.addSubject(subjectData, checkedSubjects.contains(subjectData.getSubjectId()));
                    if (subjectData.getSubjectId().equals(showInfoSubjectId)) {
                        subjectsWidget.selectSubjectItem(showInfoSubjectId);
                        showSubjectInfo();
                        showSubject = true;
                    }
                }
                if (!showSubject && result.length > 0) {
                    showSubjectInfo(result[0].getSubjectId());
                }
                mediator.showRefreshedSubjects();

                timer.schedule(REFRESH_TIMEOUT);
            }

            @Override
            protected void handleTimeout() {
                timer.schedule(REFRESH_TIMEOUT);
            }

            @Override
            protected void handleStatusCodeException(Throwable caught) {
                timer.schedule(REFRESH_TIMEOUT);
            }

            @Override
            protected void handleUncheckedException(Throwable caught) {
                timer.schedule(REFRESH_TIMEOUT);
            }

            @Override
            protected void handleUnknownException(Throwable caught) {
                timer.schedule(REFRESH_TIMEOUT);
            }
        };
    }

    private ServerRequest<PositionData> createGetLastPositionRequest() {
        return new ServerRequest<PositionData>(mediator) {
            @Override
            protected void request() {
                SubjectService.App.getInstance().getLastPosition(
                        showInfoSubjectId,
                        mediator.isShowValid(),
                        getAsyncCallback()
                );
            }

            @Override
            protected void handleSuccess(PositionData result) {
                if (result != null) {
                    mediator.setMapCenter(result.getLatitude(), result.getLongitude());
                }
            }

            @Override
            protected void handleTimeout() {
            }

            @Override
            protected void handleStatusCodeException(Throwable caught) {
            }

            @Override
            protected void handleUncheckedException(Throwable caught) {
            }

            @Override
            protected void handleUnknownException(Throwable caught) {
            }
        };
    }

    private ServerRequest<SubjectInfoData> createGetSubjectInfoRequest() {
        return new ServerRequest<SubjectInfoData>(mediator) {
            protected void request() {
                SubjectService.App.getInstance().getSubjectInfo(showInfoSubjectId, getAsyncCallback());
            }

            protected void handleSuccess(SubjectInfoData result) {
                subjectInfoWidget.hideWaiting();

                mediator.showSubjectInfo(result);
            }

            @Override
            protected void handleTimeout() {
                subjectInfoWidget.showError(stringConstants.timeoutExceeded());
            }

            @Override
            protected void handleStatusCodeException(Throwable caught) {
                subjectInfoWidget.showError(
                        messages.serverErrorWithCode(
                                String.valueOf(((StatusCodeException) caught).getStatusCode())
                        )
                );
            }

            @Override
            protected void handleUncheckedException(Throwable caught) {
                subjectInfoWidget.showError(stringConstants.serverError());
            }

            @Override
            protected void handleUnknownException(Throwable caught) {
                subjectInfoWidget.showError(stringConstants.unknownError());
            }
        };
    }

    private ServerRequest createRemoveSubjectRequest() {
        return new ServerRequest(mediator) {
            private Long subjectId;
            private String subjectName;

            @Override
            protected void request() {
                final SubjectData subjectData = subjectsWidget.getSubjectData(showInfoSubjectId);
                if (subjectData != null) {
                    subjectName = subjectData.getName();
                }
                subjectId = showInfoSubjectId;
                SubjectService.App.getInstance().removeSubject(subjectId, getAsyncCallback());
            }

            @Override
            protected void handleSuccess(Object result) {
                checkedSubjects.remove(subjectId);
                requestSubjects();
            }

            @Override
            protected void handleTimeout() {
                Window.alert(
                        messages.deleteSubjectFailed(subjectName) + " "
                            + stringConstants.timeoutExceeded().replaceAll("<br>", " ")
                );
            }

            @Override
            protected void handleStatusCodeException(Throwable caught) {
                Window.alert(
                        messages.deleteSubjectFailed(subjectName) + " "
                                + messages.serverErrorWithCode(
                                    String.valueOf(((StatusCodeException) caught).getStatusCode())
                                )
                );
            }

            @Override
            protected void handleUncheckedException(Throwable caught) {
                Window.alert(messages.deleteSubjectFailed(subjectName) + " " + stringConstants.serverError());
            }

            @Override
            protected void handleUnknownException(Throwable caught) {
                Window.alert(messages.deleteSubjectFailed(subjectName) + " " + stringConstants.unknownError());
            }
        };
    }

    private ServerRequest createSaveSubjectSettingsRequest() {
        return new ServerRequest(mediator) {

            private String subjectName;

            protected void request() {
                subjectName = subjectSettingsWidget.getModifiedSettings().getName();
                SubjectService.App.getInstance().saveSettings(
                        showInfoSubjectId,
                        subjectSettingsWidget.getModifiedSettings(),
                        getAsyncCallback()
                );
            }

            protected void handleSuccess(Object result) {
                subjectSettingsWidget.hideStatusLabel();
                refreshSubjects();
            }

            @Override
            protected void handleTimeout() {
                Window.alert(
                        messages.saveSubjectSettingsFailed(subjectName) + " "
                            + stringConstants.timeoutExceeded().replaceAll("<br>", " ")
                );
                subjectInfoWidget.enableSaveButton();
            }

            @Override
            protected void handleStatusCodeException(Throwable caught) {
                Window.alert(
                        messages.saveSubjectSettingsFailed(subjectName) + " "
                                + messages.serverErrorWithCode(
                                    String.valueOf(((StatusCodeException) caught).getStatusCode())
                                )
                );
                subjectInfoWidget.enableSaveButton();
            }

            @Override
            protected void handleUncheckedException(Throwable caught) {
                Window.alert(messages.saveSubjectSettingsFailed(subjectName) + " " + stringConstants.serverError());
                subjectInfoWidget.enableSaveButton();
            }

            @Override
            protected void handleUnknownException(Throwable caught) {
                Window.alert(messages.saveSubjectSettingsFailed(subjectName) + " " + stringConstants.unknownError());
                subjectInfoWidget.enableSaveButton();
            }
        };
    }

    public SubjectInfoData getSubjectInfoData() {
        return subjectInfoWidget.getSubjectInfoData();
    }
}
