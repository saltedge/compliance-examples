/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2020 Salt Edge.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.saltedge.connector.sdk.callback;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.models.responses.ErrorResponse;
import com.saltedge.connector.sdk.callback.mapping.BaseCallbackRequest;
import com.saltedge.connector.sdk.callback.mapping.BaseFailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Sessions callback service
 */
@Service
public class SessionsCallbackService extends CallbackRestClient {
    private static final Logger log = LoggerFactory.getLogger(SessionsCallbackService.class);

    public ErrorResponse sendUpdateCallback(String sessionSecret, BaseCallbackRequest params) {
        String url = createCallbackRequestUrl(createSessionPath(sessionSecret) + "/update");
        return sendSessionCallback(url, sessionSecret, params);
    }

    @Async
    public CompletableFuture<ErrorResponse> sendUpdateCallbackAsync(
            String sessionSecret,
            BaseCallbackRequest params
    ) throws InterruptedException {
        return CompletableFuture.completedFuture(sendUpdateCallback(sessionSecret, params));
    }

    public ErrorResponse sendSuccessCallback(String sessionSecret, BaseCallbackRequest params) {
        String url = createCallbackRequestUrl(createSessionPath(sessionSecret) + "/success");
        return sendSessionCallback(url, sessionSecret, params);
    }

    @Async
    public CompletableFuture<ErrorResponse> sendSuccessCallbackAsync(
            String sessionSecret,
            BaseCallbackRequest params
    ) throws InterruptedException {
        return CompletableFuture.completedFuture(sendSuccessCallback(sessionSecret, params));
    }

    public ErrorResponse sendFailCallback(String sessionSecret, Exception exception) {
        return sendFailCallback(sessionSecret, exception, null, null);
    }

    public ErrorResponse sendFailCallback(String sessionSecret, Exception exception, String userId) {
        return sendFailCallback(sessionSecret, exception, userId, null);
    }

    @Async
    public void sendFailCallbackAsync(String sessionSecret, Exception exception) {
        sendFailCallback(sessionSecret, exception, null);
    }

    @Async
    public CompletableFuture<ErrorResponse> sendFailCallbackAsync(
            String sessionSecret,
            Exception exception,
            String userId
    ) throws InterruptedException {
        return CompletableFuture.completedFuture(sendFailCallback(sessionSecret, exception, userId));
    }

    @Async
    public CompletableFuture<ErrorResponse> sendFailCallbackAsync(
            String sessionSecret,
            Exception exception,
            String userId,
            String status
    ) throws InterruptedException {
        return CompletableFuture.completedFuture(sendFailCallback(sessionSecret, exception, userId, status));
    }

    private ErrorResponse sendFailCallback(String sessionSecret, Exception exception, String userId, String status) {
        BaseFailRequest params;
        if (exception instanceof HttpErrorParams) {
            HttpErrorParams errorParams = (HttpErrorParams) exception;
            params = new BaseFailRequest(errorParams.getErrorClass(), errorParams.getErrorMessage(), userId);
        } else {
            params = new BaseFailRequest(exception.getClass().getSimpleName(), exception.getMessage(), userId);
        }
        if (Objects.equals(status, "RJCT") || Objects.equals(status, "CANC")) params.status = status;
        String url = createCallbackRequestUrl(createSessionPath(sessionSecret) + "/fail");
        return sendSessionCallback(url, sessionSecret, params);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    private String createSessionPath(String sessionSecret) {
        return SDKConstants.CALLBACK_BASE_PATH + "/sessions/" + sessionSecret;
    }

    private ErrorResponse sendSessionCallback(String url, String sessionSecret, BaseCallbackRequest params) {
        params.sessionSecret = sessionSecret;
        LinkedMultiValueMap<String, String> headers = createCallbackRequestHeaders(params);
        printPayload(url, headers, params);
        return doCallbackRequest(url, headers);
    }
}
