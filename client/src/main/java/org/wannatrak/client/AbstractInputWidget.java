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

package org.wannatrak.client;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.History;

/**
 * Created 25.05.2009 21:33:08
 *
 * @author Andrey Khalzov
 */
public abstract class AbstractInputWidget<T extends AbstractInputController<? extends AbstractInputWidget, R>, R> extends VerticalPanel {

    protected static final String COMMENT_STYLE = "comment";

    protected final StringConstants stringConstants = StringConstants.StringConstantsSingleton.getInstance();

    protected final Mediator mediator;

    protected final T inputController;

    protected final InputGrid inputGrid;

    protected Image captchaImage;


    public AbstractInputWidget(Mediator mediator, T inputController) {
        this.mediator = mediator;
        this.inputController = inputController;

        inputController.setInputWidget(this);

        setHorizontalAlignment(ALIGN_CENTER);

        final Label header = getHeaderLabel();

        captchaImage = Captcha.createCaptcha();

        inputGrid = createInputGrid();

        final VerticalPanel inputPanel = createInputPanel(header);

        add(inputPanel);
        add(CopyrightLabel.getInstance());

        getElement().setAttribute("align", "center");
    }

    protected abstract Label getHeaderLabel();

    protected abstract Button createSubmitButton();

    protected abstract void fillInputGrid(InputGrid inputGrid);

    protected abstract int getNumOfRows();

    protected abstract HistoryToken getThisHistoryToken();

    protected abstract HistoryToken getSubmitHistoryToken();

    public void reloadCaptcha() {
        final Image newCaptcha = Captcha.createCaptcha();
        inputGrid.replace(captchaImage, newCaptcha);
        captchaImage = newCaptcha;
    }

    public void setFocus() {
        ((TextBox) inputGrid.getWidget(0, 1)).setFocus(true);
        inputController.reloadCaptcha();
    }

    protected int getNumOfColumns() {
        return 2;
    }

    protected Label createCaptchaLabel() {
        final Label captchaTitle = new Label(getCaptchaHeader());
        captchaTitle.setStylePrimaryName("captchaTitle");
        return captchaTitle;
    }

    protected String getCaptchaHeader() {
        return stringConstants.captchaInput();
    }

    protected KeyboardListener getKeyboardListener() {
        return new KeyboardListenerAdapter() {
            @Override
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                if (KeyboardListener.KEY_ENTER == keyCode) {
                    History.newItem(getSubmitHistoryToken().toString());
                }
            }
        };
    }

    protected ClickListener getSubmitClickListener() {
        return new ClickListener() {
            public void onClick(Widget sender) {
                History.newItem(getSubmitHistoryToken().toString());
            }
        };
    }

    private VerticalPanel createInputPanel(Label header) {
        final VerticalPanel inputPanel = new VerticalPanel();
        inputPanel.add(header);
        inputPanel.add(inputGrid);

        final Button submitButton = createSubmitButton();
        submitButton.addClickListener(getSubmitClickListener());

        inputPanel.add(submitButton);
        inputPanel.setStylePrimaryName("inputPanel");
        inputPanel.setSpacing(14);

        inputPanel.setCellHorizontalAlignment(submitButton, ALIGN_CENTER);
        return inputPanel;
    }

    private InputGrid createInputGrid() {
        final InputGrid inputGrid = new InputGrid(
                getNumOfRows(), getNumOfColumns(), "inputLabelCell", "inputBoxCell", "inputBox"
        );

        fillInputGrid(inputGrid);

        createCaptcha(inputGrid);

        return inputGrid;
    }

    private void createCaptcha(final InputGrid inputGrid) {
        final Label captchaTitle = createCaptchaLabel();
        inputGrid.addWidget(captchaTitle);

        inputGrid.addWidget(captchaImage);

        final Hyperlink reloadCaptcha = new Hyperlink(
                stringConstants.reloadCaptcha(),
                getThisHistoryToken().toString()
        );
        reloadCaptcha.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                reloadCaptcha();
            }
        });
        reloadCaptcha.setStylePrimaryName("reloadCaptcha");

        inputGrid.addWidget(reloadCaptcha);

        createCaptchaTextBox(inputGrid);
    }

    private void createCaptchaTextBox(InputGrid inputGrid) {
        final TextBox captchaTextBox = inputGrid.addLabeledTextBoxWithComment(
                stringConstants.captchaInstruction(),
                stringConstants.captchaComment(),
                COMMENT_STYLE
        );
        captchaTextBox.addKeyboardListener(getKeyboardListener());
        inputController.setCaptchaTextBox(captchaTextBox);
        inputController.setCaptchaComment(inputGrid.getComment(captchaTextBox));
    }
}
