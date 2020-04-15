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
package com.saltedge.connector.sdk.api.controllers;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.callback.services.SessionsCallbackService;
import com.saltedge.connector.sdk.callback.services.TokensCallbackService;
import com.saltedge.connector.sdk.models.Token;
import com.saltedge.connector.sdk.models.TokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.LinkedMultiValueMap;

abstract public class ControllerIntegrationTests {
    @LocalServerPort
    private int port = 0;
    @Autowired
    TokensRepository tokensRepository;
    @MockBean
    protected SessionsCallbackService callbackService;
    @MockBean
    private TokensCallbackService tokensCallbackService;
    protected TestRestTemplate testRestTemplate = new TestRestTemplate();

    protected void seedTokensRepository() {
        Token newToken1 = new Token("sessionSecret", "tppAppName", "oauth", "tppRedirectUrl");
        newToken1.id = 1L;
        newToken1.userId = "1";
        newToken1.initConfirmedToken();
        newToken1.accessToken = "validToken";
        tokensRepository.save(newToken1);

        Token newToken2 = new Token("sessionSecret2", "tppAppName", "oauth", "tppRedirectUrl");
        newToken2.id = 2L;
        newToken2.userId = "2";
        newToken2.initConfirmedToken();
        newToken2.accessToken = "validToken2";
        tokensRepository.save(newToken2);
    }

    protected LinkedMultiValueMap<String, String> createHeaders() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(SDKConstants.HEADER_CLIENT_ID, "clientId");
        headers.add(SDKConstants.HEADER_ACCESS_TOKEN, "validToken");
        return headers;
    }

    protected String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
