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
 * 10.12.2008 13:39:04
 */
package org.wannatrak.client;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.core.client.GWT;

public interface StringConstants extends Constants {

    String logout();

    String slogan();

    String loginLabel();

    String passwordLabel();

    String labelColon();

    String subjects();

    String remindPass();

    String loginButton();

    String register();

    String loginFailed();

    String serverError();

    String unknownError();

    String timeoutExceeded();

    String loginWaiting();

    String repeatPasswordLabel();

    String emailLabel();

    String registrationLabel();

    String copyrightLabel();

    String registerButton();

    String checkLogin();

    String captchaRegister();

    String captchaInstruction();

    String checkLoginWaiting();

    String reloadCaptcha();

    String loginComment();

    String passwordComment();

    String repeatPasswordComment();

    String emailComment();

    String captchaComment();

    String toBeFilledWarning();

    String passwordsMismatch();

    String captchaFailed();

    String captchaWaiting();

    String wrongEmail();

    String demoSubjects();

    String hostName();

    String timeInterval();

    String fromTimeInterval();

    String toTimeInterval();

    String today();

    String yesterday();

    String twoDaysAgo();

    String threeDaysAgo();

    String fourDaysAgo();

    String fiveDaysAgo();

    String sixDaysAgo();

    String weekAgo();

    String eightDaysAgo();

    String nineDaysAgo();

    String tenDaysAgo();

    String elevenDaysAgo();

    String twelveDaysAgo();

    String thirteenDaysAgo();

    String twoWeeksAgo();

    String monthAgo();

    String twoMonthesAgo();

    String halfYearAgo();

    String yearAgo();

    String showWithErrors();

    String intro();

    String registerRightNow();

    String download();

    String howtoSetup();    

    String howtoSetupText();

    String ready();

    String setupLabel();

    String goBack();

    String loading();

    String repeatQuery();

    String subjectName();

    String savePeriod();

    String sendPeriod();

    String removeSubject();

    String confirmToDeleteSubject();

    String subjectTracking();

    String subjectConnectionFailed();

    String subjectSwitchedOff();

    String subjectState();

    String speed();

    String altitude();

    String speedUnits();

    String altitudeUnits();

    String noData();

    String settings();

    String save();

    String subjectLocationNotDefined();

    String noAccess();

    String ques();  

    String inSecs();

    String inMins();

    String savePeriodInfo();

    String sendPeriodInfo();

    String zeroSavePeriod();

    String zeroSendPeriod();

    String saveSettingsWaiting();

    String userNotFoundByLoginAndEmail();

    String remindPassSendMailFail();

    String setNewPassHeader();

    String setNewPassSumbit();

    String setNewPassError();

    String continueSetNewPass();

    String requestSetNewPassLabel();

    String captchaInput();

    String close();

    String newPasswordLabel();

    String repeatNewPasswordLabel();

    String logo();

    String keepMyLogin();

    String getAndroidApp();

    public static class StringConstantsSingleton {
        private static final StringConstants instance = GWT.create(StringConstants.class);

        public static StringConstants getInstance() {
            return instance;
        }
    }
}
