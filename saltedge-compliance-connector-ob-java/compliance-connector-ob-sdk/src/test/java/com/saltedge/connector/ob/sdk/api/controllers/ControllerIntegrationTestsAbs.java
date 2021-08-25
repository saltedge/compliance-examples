/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2021 Salt Edge.
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
package com.saltedge.connector.ob.sdk.api.controllers;

import com.saltedge.connector.ob.sdk.api.ApiConstants;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.model.jpa.ConsentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.LinkedMultiValueMap;

abstract public class ControllerIntegrationTestsAbs {
    @LocalServerPort
    private int port = 0;
    @Autowired
    ConsentsRepository consentsRepository;
    protected TestRestTemplate testRestTemplate = new TestRestTemplate();

    protected void seedConsents() {
        Consent newConsent1 = new Consent("tppAppName", "1", "accepted");
        newConsent1.id = 1L;
        newConsent1.userId = "1";
        newConsent1.accessToken = "validToken";
        consentsRepository.save(newConsent1);

        Consent newConsent2 = new Consent("tppAppName", "2", "accepted");
        newConsent2.id = 2L;
        newConsent2.userId = "2";
        newConsent2.accessToken = "validToken2";
        consentsRepository.save(newConsent2);
    }

    protected LinkedMultiValueMap<String, String> createHeaders() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(ApiConstants.HEADER_CLIENT_ID, "clientId");
        headers.add(ApiConstants.HEADER_ACCESS_TOKEN, "validToken");
        return headers;
    }

    protected String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
