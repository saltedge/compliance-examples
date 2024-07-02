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
import com.saltedge.connector.sdk.callback.SessionsCallbackService;
import com.saltedge.connector.sdk.callback.TokensCallbackService;
import com.saltedge.connector.sdk.models.ConsentStatus;
import com.saltedge.connector.sdk.models.ParticipantAccount;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.models.domain.AisTokensRepository;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import com.saltedge.connector.sdk.models.domain.PiisTokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.util.LinkedMultiValueMap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

abstract public class ControllerIntegrationTests {
    @LocalServerPort
    protected int port = 0;

    @Autowired
    AisTokensRepository aisTokensRepository;
    @Autowired
    PiisTokensRepository piisTokensRepository;
    @MockBean
    protected SessionsCallbackService callbackService;
    @MockBean
    private TokensCallbackService tokensCallbackService;
    protected TestRestTemplate testRestTemplate = new TestRestTemplate();

    protected void seedTokensRepository() {
        AisToken newAisToken1 = new AisToken("sessionSecret", "tppAppName", "oauth", "tppRedirectUrl", Instant.now().plus(24 * 60, ChronoUnit.MINUTES), null);
        newAisToken1.id = 1L;
        newAisToken1.userId = "1";
        newAisToken1.status = ConsentStatus.CONFIRMED;
        newAisToken1.accessToken = "validToken";
        aisTokensRepository.save(newAisToken1);

        AisToken newAisToken2 = new AisToken("sessionSecret2", "tppAppName", "oauth", "tppRedirectUrl", Instant.now().plus(24 * 60, ChronoUnit.MINUTES), null);
        newAisToken2.id = 2L;
        newAisToken2.userId = "2";
        newAisToken2.status = ConsentStatus.CONFIRMED;
        newAisToken2.accessToken = "validToken2";
        aisTokensRepository.save(newAisToken2);

        PiisToken newPiisToken1 = new PiisToken("sessionSecret", "tppAppName", "oauth", "tppRedirectUrl", ParticipantAccount.createWithIbanAndCurrency("iban", "EUR"), null);
        newPiisToken1.id = 1L;
        newPiisToken1.userId = "1";
        newPiisToken1.status = ConsentStatus.CONFIRMED;
        newPiisToken1.accessToken = "validToken";
        piisTokensRepository.save(newPiisToken1);
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
