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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.callback.CallbackRestClient;
import com.saltedge.connector.sdk.callback.mapping.BaseCallbackRequest;
import com.saltedge.connector.sdk.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import javax.validation.constraints.NotEmpty;

/**
 * Token Endpoints allow Connector to perform operations like getting the list of all tokens created by a specific PSU
 * and revoking them. This endpoints prove to be useful in case ASPSP decides to give PSU the possibility to control
 * and revoke consents using his personal Web-Banking Dashboard.
 */
@Service
@Validated
public class TokensCallbackService extends CallbackRestClient {
    private static Logger log = LoggerFactory.getLogger(TokensCallbackService.class);

    /**
     * Revoke callback needs to be called any time a token is revoked on the Provider Connector side.
     *
     * @param accessToken
     */
    @Async
    public void sendRevokeTokenCallback(@NotEmpty String accessToken) {
        String url = createCallbackRequestUrl(SDKConstants.CALLBACK_BASE_PATH + "/tokens/revoke");
        LinkedMultiValueMap<String, String> headers = createCallbackRequestHeaders(null);
        headers.add("Token", accessToken);
        printPayload(url, headers, null);
        doCallbackRequest(url, headers);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
