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

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * Created 25.05.2009 21:43:51
 *
 * @author Andrey Khalzov
 */
public abstract class AbstractInputController<T extends AbstractInputWidget, R> {

    public static final String EMAIL_REGEX
            = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*"
            + "@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    public static final String WARNING_COMMENT_STYLE = "warning";

    protected final StringConstants stringConstants = StringConstants.StringConstantsSingleton.getInstance();

    protected final ServerRequest<R> serverRequest;

    protected final Mediator mediator;

    protected T inputWidget;

    protected TextBox captchaTextBox;
    private HTML captchaComment;

    public AbstractInputController(Mediator mediator) {
        this.mediator = mediator;
        serverRequest = createServerRequest();
    }

    public abstract boolean isInitialized();

    @SuppressWarnings("unchecked")
    public void setInputWidget(AbstractInputWidget inputWidget) {
        this.inputWidget = (T) inputWidget;
    }

    public void setCaptchaTextBox(TextBox captchaTextBox) {
        this.captchaTextBox = captchaTextBox;
    }

    public void setCaptchaComment(HTML captchaComment) {
        this.captchaComment = captchaComment;
    }

    public void submit() {
        checkIsInitialized();

        if (_validateInput()) {
            execute();
        } else {
            History.back();
            reloadCaptcha();
        }
    }

    public void stopExecuting() {
        checkIsInitialized();

        History.back();
        reloadCaptcha();
    }

    public void setFocusToCaptcha() {
        checkIsInitialized();

        captchaTextBox.setFocus(true);
    }

    public void showWarning(String msg) {
        checkIsInitialized();

        hideCheckCaptchaResult();

        captchaComment.setVisible(true);
        captchaComment.addStyleDependentName(WARNING_COMMENT_STYLE);

        captchaComment.setHTML(msg);
    }

    public void hideCheckCaptchaResult() {
        checkIsInitialized();

        captchaComment.removeStyleDependentName(WARNING_COMMENT_STYLE);
        captchaComment.setVisible(false);
    }

    public void showWaitingCheckCaptchaStatus() {
        checkIsInitialized();

        hideCheckCaptchaResult();

        captchaComment.setVisible(true);

        captchaComment.setHTML(stringConstants.captchaWaiting());
    }

    public void reloadCaptcha() {
        DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
            public void execute() {
                checkIsInitialized();

                inputWidget.reloadCaptcha();
                captchaTextBox.setText("");
            }
        });
    }

    public void clearInput() {
        _clearInput();

        captchaTextBox.setText("");

        hideCheckCaptchaResult();
    }

    protected abstract void callServer();

    protected abstract void handleSuccess(R result);

    protected abstract boolean validateInput(boolean valid);

    protected abstract void _clearInput();

    protected void execute() {
        checkIsInitialized();

        serverRequest.execute();
    }

    protected void checkIsInitialized() {
        if (!_isInitialized()) {
            throw new IllegalStateException(getClass().getName() + " is not initailzed!");
        }
    }

    private boolean _validateInput() {
        checkIsInitialized();

        boolean valid = validateCaptchaInput();

        valid = validateInput(valid);

        return valid;
    }

    private boolean validateCaptchaInput() {
        boolean valid = true;
        if ("".equals(captchaTextBox.getText().trim())) {
            valid = false;
            captchaComment.setHTML(stringConstants.toBeFilledWarning());
            captchaComment.addStyleDependentName(WARNING_COMMENT_STYLE);
            captchaComment.setVisible(true);
            setFocusToCaptcha();
        } else {
            captchaComment.setVisible(false);
        }
        return valid;
    }

    private boolean _isInitialized() {
        return inputWidget != null
                && captchaTextBox != null
                && captchaComment != null
                && isInitialized();
    }

    private ServerRequest<R> createServerRequest() {
        return new ServerRequest<R>(mediator) {
            @Override
            protected void request() {
                showWaitingCheckCaptchaStatus();

                callServer();
            }

            @Override
            protected void handleSuccess(R result) {
                AbstractInputController.this.handleSuccess(result);

                clearInput();
            }

            @Override
            protected void handleTimeout() {
                stopExecuting();

                showWarning(stringConstants.timeoutExceeded());
            }

            @Override
            protected void handleStatusCodeException(Throwable caught) {
                stopExecuting();

                showWarning(
                        Messages.MessagesSingleton.getInstance().serverErrorWithCode(
                                String.valueOf(((StatusCodeException) caught).getStatusCode())
                        )
                );
            }

            @Override
            protected void handleUncheckedException(Throwable caught) {
                stopExecuting();

                showWarning(stringConstants.serverError());
            }

            @Override
            protected void handleUnknownException(Throwable caught) {
                stopExecuting();

                showWarning(stringConstants.unknownError());
            }
        };
    }
}
