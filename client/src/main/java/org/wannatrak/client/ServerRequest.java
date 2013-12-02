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
 * 24.12.2008 21:24:59
 */
package org.wannatrak.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.StatusCodeException;
import org.wannatrak.client.exception.CheckedException;

public abstract class ServerRequest<T> {
    private static final int SERVER_TIMEOUT = 10000;

    private AsyncCallback<T> asyncCallback;
    private Timer timer;
    private boolean timeoutExceeded = false;
    private int numOfActiveRequests = 0;
    private Mediator mediator;

    protected ServerRequest(Mediator mediator) {
        this.mediator = mediator;
        timer = new Timer() {
            @Override
            public void run() {
                timeoutExceeded = true;
                handleTimeout();
            }
        };

        asyncCallback = new AsyncCallback<T>() {
            public void onFailure(Throwable caught) {
                numOfActiveRequests--;

                if (timeoutExceeded || numOfActiveRequests > 0) {
                    return;
                }

                timer.cancel();
                handleFailure(caught);
            }

            public void onSuccess(T result) {
                numOfActiveRequests--;

                if (timeoutExceeded || numOfActiveRequests > 0) {
                    return;
                }

                timer.cancel();
                handleSuccess(result);
            }
        };
    }

    public AsyncCallback<T> getAsyncCallback() {
        return asyncCallback;
    }

    public void execute() {
        timer.cancel();
        timer.schedule(SERVER_TIMEOUT);

        timeoutExceeded = false;

        request();

        numOfActiveRequests++;
    }

    protected abstract void request();

    protected abstract void handleSuccess(T result);

    protected abstract void handleTimeout();

    protected abstract void handleStatusCodeException(Throwable caught);

    protected abstract void handleUncheckedException(Throwable caught);

    protected abstract void handleUnknownException(Throwable caught);

    protected void handleCheckedException(CheckedException caught) {
        caught.throwTo(mediator);
    }

    protected void handleFailure(Throwable caught) {
        if (caught instanceof InvocationException || caught instanceof IncompatibleRemoteServiceException) {
            if (caught instanceof StatusCodeException) {
                handleStatusCodeException(caught);
            } else {
                handleUncheckedException(caught);
            }
        } else if (caught instanceof CheckedException) {
            handleCheckedException((CheckedException) caught);
        } else {
            handleUnknownException(caught);
        }
    }
}
