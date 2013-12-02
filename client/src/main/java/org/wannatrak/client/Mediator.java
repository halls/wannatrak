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
 * 21.12.2008 20:35:06
 */
package org.wannatrak.client;

import org.wannatrak.client.layout.*;
import org.wannatrak.client.layout.command.ShowSingleWidgetCommand;
import org.wannatrak.client.state.*;
import org.wannatrak.client.state.param.LoggedInHistoryTokenParam;
import org.wannatrak.client.exception.LoginFailedException;
import org.wannatrak.client.exception.CheckedException;
import org.wannatrak.client.register.RegisterWidget;
import org.wannatrak.client.register.RegisterController;
import org.wannatrak.client.login.LoginService;
import org.wannatrak.client.login.LoginWidget;
import org.wannatrak.client.subject.SubjectsWidget;
import org.wannatrak.client.subject.SubjectsController;
import org.wannatrak.client.subject.SubjectInfoData;
import org.wannatrak.client.subject.SubjectInfoWidget;
import org.wannatrak.client.demo.DemoWidget;
import org.wannatrak.client.device.HowtoSetupWidget;
import org.wannatrak.client.setnewpass.*;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.maps.client.MapWidget;

import java.util.Date;
import java.util.Map;

public class Mediator {

    private final StringConstants stringConstants = StringConstants.StringConstantsSingleton.getInstance();

    private State state = StartState.getInstance();

    private Header header;

    private MapWidget mapWidget;
    private MapController mapController;

    private RegisterWidget registerWidget;
    private RegisterController registerController;

    private RequestSetNewPassWidget requestSetNewPassWidget;
    private RequestSetNewPassController requestSetNewPassController;

    private MainWidget mainWidget;

    private Label rightHeaderLabel;

    private MainWidgetLayoutManager mainWidgetLayoutManager;

    private LoginWidget loginWidget;

    private FilterWidget filterWidget;

    private SubjectsWidget subjectsWidget;
    private SubjectInfoWidget subjectInfoWidget;
    private SubjectsController subjectsController;

    private DemoWidget demoWidget;

    private LoginOnStartupRequest loginOnStartupServerRequest;
    private ServerRequest<String> loginServerRequest;
    private ServerRequest logoutServerRequest;

    private Grid majorLayoutPanel;

    private String login;

    private SetNewPassWidget setNewPassWidget;
    private SetNewPassController setNewPassController;

    private CaptchaController captchaController;

    public Mediator() {
        loginOnStartupServerRequest = createLoginOnStartupServerRequest();
        loginServerRequest = createLoginServerRequest();
        logoutServerRequest = createLogoutServerRequest();
    }

    public boolean isInitializedForLoggedIn() {
        return subjectInfoWidget != null && isInitialized();
    }

    public boolean isInitialized() {
        return header != null
                && mainWidget != null
                && mainWidgetLayoutManager != null
                && rightHeaderLabel != null
                && loginWidget != null
                && subjectsWidget != null
                && mapWidget != null
                && mapController != null
                && filterWidget != null
                && subjectsController != null
                && loginServerRequest != null
                && majorLayoutPanel != null
                && demoWidget != null;
    }

    public MainWidgetLayout getMainWidgetLayout() {
        return mainWidget.getMainWidgetLayout();
    }

    public boolean isLoggedIn() {
        return LoggedInState.getInstance().equals(state);
    }

    public State getState() {
        return state;
    }

    public CaptchaController getCaptchaController() {
        return captchaController;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setMainWidget(MainWidget mainWidget) {
        this.mainWidget = mainWidget;
        mapWidget = mainWidget.getMap();
        mapController = new MapController(this, mapWidget);
    }

    public void setMainWidgetLayoutManager(MainWidgetLayoutManager mainWidgetLayoutManager) {
        this.mainWidgetLayoutManager = mainWidgetLayoutManager;
    }

    public void setMajorLayoutPanel(Grid majorLayoutPanel) {
        this.majorLayoutPanel = majorLayoutPanel;
    }

    public void setSubjectsWidget(SubjectsWidget subjectsWidget) {
        this.subjectsWidget = subjectsWidget;
    }

    public void setSubjectInfoWidget(SubjectInfoWidget subjectInfoWidget) {
        this.subjectInfoWidget = subjectInfoWidget;
    }

    public SubjectsController getSubjectsController() {
        return subjectsController;
    }

    public void setSubjectsController(SubjectsController subjectsController) {
        if (this.subjectsController != null) {
            subjectsController.copyCheckedSubjects(this.subjectsController);
        }
        this.subjectsController = subjectsController;
    }

    public void setLoginWidget(LoginWidget loginWidget) {
        this.loginWidget = loginWidget;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public void setFilterWidget(FilterWidget filterWidget) {
        this.filterWidget = filterWidget;
    }

    public void setDemoWidget(DemoWidget demoWidget) {
        this.demoWidget = demoWidget;
    }

    public void showRefreshedSubjects() {
        final Date currentDate = new Date();
        final int currentMins = currentDate.getMinutes() + currentDate.getHours() * 60;

        if (Integer.parseInt(filterWidget.getToDaysAgo()) == 0
                && currentMins
                    <= Integer.parseInt(filterWidget.getToHour()) * 60 + Integer.parseInt(filterWidget.getToMinute())
        ) {
            showSubjects();
        }
    }

    public void showSubjects() {
        showSubjects(false);
    }

    public void showSubjects(boolean updateOnlyNew) {
        checkInitialized();

        mapController.showSubjects(
                subjectsController.getCheckedSubjects(),
                filterWidget.getFromDaysAgo(),
                filterWidget.getFromHour(),
                filterWidget.getFromMinute(),
                filterWidget.getToDaysAgo(),
                filterWidget.getToHour(),
                filterWidget.getToMinute(),
                filterWidget.isShowWithErrors(),
                updateOnlyNew
        );
    }

    public void tryToLogin(Map<String, String> params) {
        checkInitialized();

        loginOnStartupServerRequest.setParams(params);
        loginOnStartupServerRequest.execute();
    }

    public void requestLogin() {
        checkInitialized();

        loginWidget.showWaiting(stringConstants.loginWaiting());

        loginServerRequest.execute();
    }

    public void returnToLoggedIn() {
        checkInitialized();

        showMap();

        applyLayout(getPrevLayout(LeftCenterBottomLayout.getInstance()));

        state = LoggedInState.getInstance();
    }

    public void returnToDemo() {
        checkInitialized();

        showMap();

        applyLayout(getPrevLayout(LeftCenterBottomRightLayout.getInstance()));

        state = DemoState.getInstance();
    }

    public void showMap() {
        mainWidget.setCenterWidget(mainWidget.getMap());
    }

    public void loginAfterRegister(String login) {
        checkInitialized();

        showMap();

        _login(login);
    }

    public void fireLoginFailedException(LoginFailedException loginFailedException) {
        checkInitialized();

        History.back();
        loginWidget.showError(stringConstants.loginFailed());

        if (StartState.getInstance().equals(state)) {
            logout();
        }
    }

    public void logout() {
        checkInitialized();

        logoutServerRequest.execute();
    }

    public void applyLayout(MainWidgetLayout layout) {
        checkInitialized();

        mainWidgetLayoutManager.applyLayout(mainWidget, layout);
    }

    public void requestSetNewPass() {
        checkInitialized();

        mainWidget.setPrevMainWidgetLayout(mainWidget.getMainWidgetLayout());

        mainWidget.setCenterWidget(getRequestSetNewPassWidget());
        majorLayoutPanel.getCellFormatter().setStyleName(1, 0, "remindpass-widgetCell");
        getRequestSetNewPassController().clearInput();
        getRequestSetNewPassController().setLogin(loginWidget.getLogin());

        captchaController = getRequestSetNewPassController();

        new ShowSingleWidgetCommand(this).execute();

        state = RequestChangePassState.getInstance();
    }

    public void showSetNewPass() {
        checkInitialized();

        final SetNewPassWidget setNewPassWidget = getSetNewPassWidget();

        mainWidget.setPrevMainWidgetLayout(mainWidget.getMainWidgetLayout());
        mainWidget.setCenterWidget(setNewPassWidget);
        majorLayoutPanel.getCellFormatter().setStyleName(1, 0, "remindpass-widgetCell");

        captchaController = getSetNewPassController();

        new ShowSingleWidgetCommand(this).execute();

        state = SetNewPassState.getInstance();
    }

    public void register() {
        checkInitialized();

        mainWidget.setPrevMainWidgetLayout(mainWidget.getMainWidgetLayout());

        mainWidget.setCenterWidget(getRegisterWidget());
        majorLayoutPanel.getCellFormatter().setStyleName(1, 0, "singleWidgetCell");

        captchaController = getRegisterController();

        new ShowSingleWidgetCommand(this).execute();

        DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
            public void execute() {
                getRegisterWidget().setFocus();
            }
        });

        state = RegisterState.getInstance();
    }

    public void showHowtoSetup() {
        checkInitialized();
        mainWidget.setPrevMainWidgetLayout(mainWidget.getMainWidgetLayout());

        _showHowtoSetup(
                stringConstants.goBack(),
                new ClickListener() {
                    public void onClick(Widget sender) {
                        History.back();
                    }
                }
        );

        state = isLoggedIn()
                ? HowtoSetupLoggedInState.getInstance()
                : HowtoSetupState.getInstance();
    }

    public void showHowtoSetupAfterRegister() {
        checkInitialized();
        _showHowtoSetup(
                stringConstants.ready(),
                new ClickListener() {
                    public void onClick(Widget sender) {
                        History.newItem(HistoryToken.logged_in.toString());
                    }
                }
        );

        state = HowtoSetupAfterRegisterState.getInstance();
    }

    public void demo() {
        checkInitialized();

        subjectsController.requestSubjects();

        showMap();
        majorLayoutPanel.getCellFormatter().setStyleName(1, 0, "mainWidgetCell");

        applyLayout(LeftCenterBottomRightLayout.getInstance());

        DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
            public void execute() {
                loginWidget.setFocus();
            }
        });

        state = DemoState.getInstance();
    }

    public void login() {
        checkInitialized();

        _login(login);
    }

    public void setMapCenter(double latitude, double longitude) {
        mapController.setMapCenter(latitude, longitude);
    }

    public RegisterController getRegisterController() {
        return registerController;
    }

    public RegisterWidget getRegisterWidget() {
        if (registerWidget == null) {
            registerController = new RegisterController(this);
            registerWidget = new RegisterWidget(this, registerController);
        }
        return registerWidget;
    }

    public RequestSetNewPassWidget getRequestSetNewPassWidget() {
        if (requestSetNewPassWidget == null) {
            requestSetNewPassController = new RequestSetNewPassController(this);
            requestSetNewPassWidget = new RequestSetNewPassWidget(this, requestSetNewPassController);
        }
        return requestSetNewPassWidget;
	}

    public SetNewPassWidget getSetNewPassWidget() {
        if (setNewPassWidget == null) {
            setNewPassController = new SetNewPassController(this);
            setNewPassWidget = new SetNewPassWidget(this, setNewPassController);
        }
        return setNewPassWidget;
	}

    public void hideSubjectLoading(Long subjectId) {
        subjectsController.hideSubjectLoading(subjectId);
    }

    public void showSublectLoading(Long subjectId) {
        subjectsController.showSubjectLoading(subjectId);
    }

    public boolean isShowValid() {
        return !filterWidget.isShowWithErrors();
    }

    public void showSubjectInfo(SubjectInfoData subjectInfoData) {
        checkInitializedForLoggedIn();

        mainWidget.setRightWidget(subjectInfoWidget);
        subjectInfoWidget.setSubjectInfoData(subjectInfoData);
    }

    public void showSubjectName(String name) {
        checkInitializedForLoggedIn();

        rightHeaderLabel.setText(name);
        rightHeaderLabel.addStyleDependentName("subjectInfo");
    }

    public void setRightHeaderLabel(Label rightHeaderLabel) {
        this.rightHeaderLabel = rightHeaderLabel;
    }

    public void enableRightShowHideButton() {
        mainWidget.enableRightShowHideButton();
    }

    public void disableRightShowHideButton() {
        mainWidget.disableRightShowHideButton();
    }

    public void pinMapToRight() {
        mapWidget.addStyleDependentName("pinnedToRight");
    }

    public void unpinMapFromRight() {
        mapWidget.removeStyleDependentName("pinnedToRight");
    }

    public void showSubjectInfoNoAccess() {
        subjectInfoWidget.showError(stringConstants.noAccess());
    }

    public void enableSaveSettingsButton() {
        subjectInfoWidget.enableSaveButton();
    }

    public void showNameWarning(String warning) {
        subjectInfoWidget.showNameWarning(warning);
    }

    public RequestSetNewPassController getRequestSetNewPassController() {
        return requestSetNewPassController;
    }

    public SetNewPassController getSetNewPassController() {
        return setNewPassController;
    }

    public void showSetNewPassInfo(String email) {
        checkInitialized();

        mainWidget.setCenterWidget(new SetNewPassInfoWidget(email));
        majorLayoutPanel.getCellFormatter().setStyleName(1, 0, "singleWidgetCell");

        new ShowSingleWidgetCommand(this).execute();

        state = SetNewPassInfoState.getInstance();
    }

    private void _showHowtoSetup(String closeLabel, ClickListener closeClickListener) {
        mainWidget.setCenterWidget(new HowtoSetupWidget(
                closeLabel,
                closeClickListener
        ));
        majorLayoutPanel.getCellFormatter().setStyleName(1, 0, "singleWidgetCell");

        new ShowSingleWidgetCommand(this).execute();
    }

    private void checkInitialized() {
        if (!isInitialized()) {
            throw new IllegalStateException("Mediator is not initialized!");
        }
    }

    private void checkInitializedForLoggedIn() {
        if (!isInitializedForLoggedIn()) {
            throw new IllegalStateException("Mediator is not initialized for logged in!");
        }
    }

    private ServerRequest<String> createLoginServerRequest() {
        return new ServerRequest<String>(this) {
            protected void request() {
                LoginService.App.getInstance().login(
                        loginWidget.getLogin(),
                        loginWidget.getPassword(),
                        loginWidget.getKeepMyLogin(),
                        getAsyncCallback()
                );
            }

            protected void handleSuccess(String result) {
                Mediator.this.login = result;
                _login(login);
            }

            protected void handleTimeout() {
                History.back();
                loginWidget.showError(stringConstants.timeoutExceeded());
            }

            protected void handleStatusCodeException(Throwable caught) {
                History.back();
                loginWidget.showError(
                        Messages.MessagesSingleton.getInstance().serverErrorWithCode(
                                String.valueOf(((StatusCodeException) caught).getStatusCode())
                        )
                );
            }

            protected void handleUncheckedException(Throwable caught) {
                History.back();
                loginWidget.showError(stringConstants.serverError());
            }

            protected void handleUnknownException(Throwable caught) {
                History.back();
                loginWidget.showError(stringConstants.unknownError());
            }
        };
    }

    private LoginOnStartupRequest createLoginOnStartupServerRequest() {
        return new LoginOnStartupRequest(this);
    }

    private void goToDemo() {
        History.newItem(HistoryToken.demo.toString());
    }

    private ServerRequest createLogoutServerRequest() {
        return new ServerRequest(this) {
            protected void request() {
                LoginService.App.getInstance().logout(getAsyncCallback());
            }

            protected void handleSuccess(Object result) {
                _logout();
            }

            //todo logout errors
            protected void handleTimeout() {
                History.back();
//                loginWidget.showError(StringConstants.StringConstantsSingleton.getInstance().timeoutExceeded());
            }

            protected void handleStatusCodeException(Throwable caught) {
                History.back();
/*                loginWidget.showError(
                        Messages.MessagesSingleton.getInstance().serverErrorWithCode(
                                String.valueOf(((StatusCodeException) caught).getStatusCode())
                        )
                );*/
            }

            protected void handleUncheckedException(Throwable caught) {
                History.back();
//                loginWidget.showError(StringConstants.StringConstantsSingleton.getInstance().serverError());
            }

            protected void handleUnknownException(Throwable caught) {
                History.back();
//                loginWidget.showError(StringConstants.StringConstantsSingleton.getInstance().unknownError());
            }
        };
    }

    private MainWidgetLayout getPrevLayout(MainWidgetLayout defaultLayout) {
        final MainWidgetLayout prevMainWidgetLayout = mainWidget.getPrevMainWidgetLayout();
        if (prevMainWidgetLayout == null) {
            return defaultLayout;
        } else {
            mainWidget.setPrevMainWidgetLayout(null);
            return prevMainWidgetLayout;
        }
    }

    private void _logout() {
        header.logout();

        mainWidget.setLeftWidget(new DemoWidget(this));
        mainWidget.setRightWidget(loginWidget);
        mainWidget.disableRightShowHideButton();

        mainWidget.setCenterHeaderWidget(
                new Hyperlink(
                    stringConstants.howtoSetup(),
                    HistoryToken.how_to_setup.toString()
                )
        );

        rightHeaderLabel.setText(stringConstants.slogan());
        rightHeaderLabel.removeStyleDependentName("subjectInfo");

        unpinMapFromRight();
        applyLayout(LeftCenterBottomRightLayout.getInstance());

        mainWidget.getLeftWidgetLabel().setText(
                stringConstants.demoSubjects()
        );

        subjectsController.getCheckedSubjects().clear();
        subjectsController.requestSubjects();

        DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
            public void execute() {
                loginWidget.setFocus();
            }
        });

        state = DemoState.getInstance();
    }

    private void _login(String login) {
        loginWidget.hideWaiting();

        header.login(login);


        mainWidget.setLeftWidget(new SubjectsWidget(this, new SubjectsController(this)));
        mainWidget.setRightWidget(new SubjectInfoWidget(this, subjectsController));
        mainWidget.setCenterHeaderWidget(
                new Hyperlink(
                    stringConstants.howtoSetup(),
                    HistoryToken.how_to_setup_logged_in.toString()
                )
        );

        applyLayout(LeftCenterBottomLayout.getInstance());

        loginWidget.clearInput();

        mainWidget.getLeftWidgetLabel().setText(
                stringConstants.subjects()
        );

        if (!StartState.getInstance().equals(state)) {
            subjectsController.getCheckedSubjects().clear();
        }
        subjectsController.requestSubjects();

        state = LoggedInState.getInstance();
    }

    class LoginOnStartupRequest extends ServerRequest<String> {
        private Map<String, String> params;

        LoginOnStartupRequest(Mediator mediator) {
            super(mediator);
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        protected void request() {
            LoginService.App.getInstance().tryToLogin(getAsyncCallback());
        }

        protected void handleSuccess(String result) {
            Mediator.this.login = result;
            if (!History.getToken().startsWith(HistoryToken.logged_in.toString())) {
                History.newItem(HistoryToken.logged_in.toString());
            } else {
                login();
                getSubjectsController().applyParams(params.get(LoggedInHistoryTokenParam.subjects.toString()));
            }
        }

        protected void handleTimeout() {
            goToDemo();
        }

        protected void handleStatusCodeException(Throwable caught) {
            goToDemo();
        }

        protected void handleUncheckedException(Throwable caught) {
            goToDemo();
        }

        protected void handleUnknownException(Throwable caught) {
            goToDemo();
        }

        @Override
        protected void handleCheckedException(CheckedException caught) {
            goToDemo();
        }
    }
}
