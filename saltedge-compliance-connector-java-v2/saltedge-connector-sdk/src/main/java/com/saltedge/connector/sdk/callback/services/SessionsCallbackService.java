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
package com.saltedge.connector.sdk.callback.services;

import com.saltedge.connector.sdk.callback.CallbackRestClient;
import com.saltedge.connector.sdk.callback.mapping.BaseCallbackRequest;
import com.saltedge.connector.sdk.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sessions callback service
 */
@Service
public class SessionsCallbackService extends CallbackRestClient {
    private static Logger log = LoggerFactory.getLogger(SessionsCallbackService.class);

    public void sendUpdateCallback(String sessionSecret, BaseCallbackRequest params) {
        String url = createCallbackRequestUrl(createSessionPath(sessionSecret) + "/update");
        doCallbackRequest(url, sessionSecret, params);
    }

    public void sendSuccessCallback(String sessionSecret, BaseCallbackRequest params) {
        String url = createCallbackRequestUrl(createSessionPath(sessionSecret) + "/success");
        doCallbackRequest(url, sessionSecret, params);
    }

    @Override
    protected String getFailCallbackPath(String sessionSecret) {
        return createSessionPath(sessionSecret) + "/fail";
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    private String createSessionPath(String sessionSecret) {
        return Constants.CALLBACK_BASE_PATH + "/sessions/" + sessionSecret;
    }
}
