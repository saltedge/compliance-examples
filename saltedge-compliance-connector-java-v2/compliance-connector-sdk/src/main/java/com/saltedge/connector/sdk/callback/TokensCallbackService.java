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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;

/**
 * Token Endpoints allow Connector to perform operations like getting the list of all tokens created by a specific PSU
 * and revoking them. These endpoints prove to be useful in case ASPSP decides to give PSU the possibility to control
 * and revoke consents using his personal Web-Banking Dashboard.
 */
@Service
@Validated
public class TokensCallbackService extends CallbackRestClient {
    private static final Logger log = LoggerFactory.getLogger(TokensCallbackService.class);

    /**
     * Notify Salt Edge Compliance to revoke AIS consent by accessToken.
     * Revoke callback needs to be called any time an AIS consent is revoked by Provider.
     *
     * @param accessToken unique token of current consent
     */
    @Async
    public void sendRevokeAisTokenCallbackAsync(@NotEmpty String accessToken) {
        sendRevokeCallback(accessToken, "/tokens/revoke");
    }

    /**
     * Notify Salt Edge Compliance to revoke PIIS consent by accessToken.
     * Revoke callback needs to be called any time an PIIS consent is revoked by Provider.
     *
     * @param accessToken unique token of current consent
     */
    @Async
    public void sendRevokePiisTokenCallbackAsync(@NotEmpty String accessToken) {
        sendRevokeCallback(accessToken, "/funds_confirmations/tokens/revoke");
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    private void sendRevokeCallback(@NotEmpty String accessToken, String path) {
        String url = createCallbackRequestUrl(SDKConstants.CALLBACK_BASE_PATH + path);
        HashMap<String, String> params = new HashMap<>();
        LinkedMultiValueMap<String, String> headers = createCallbackRequestHeaders(params);
        headers.add("Token", accessToken);
        printPayload(url, headers, params);
        doCallbackRequest(url, headers);
    }
}
