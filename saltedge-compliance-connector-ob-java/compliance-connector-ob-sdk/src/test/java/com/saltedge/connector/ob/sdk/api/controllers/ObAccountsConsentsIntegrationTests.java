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

import com.saltedge.connector.ob.sdk.TestTools;
import com.saltedge.connector.ob.sdk.api.ApiConstants;
import com.saltedge.connector.ob.sdk.api.models.request.AccountsConsentsCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.DefaultRequest;
import com.saltedge.connector.ob.sdk.api.models.response.EmptyJsonResponse;
import com.saltedge.connector.ob.sdk.api.models.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Test Interceptors & ObAccountsConsentsController
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ObAccountsConsentsIntegrationTests extends ControllerIntegrationTestsAbs {

    @BeforeEach
    public void setUp() {
        seedConsents();
    }

    @Test
    public void givenValidPayload_whenCreateConsent_thenReturnOK() {
        // given
        AccountsConsentsCreateRequest request = new AccountsConsentsCreateRequest();
        request.tppAppName = "name";
        request.providerCode = "code";
        request.consentId = "123";
        request.permissions = List.of("ReadAccountsBasic");

        String auth = TestTools.createAuthorizationHeaderValue(request, TestTools.getInstance().getRsaPrivateKey());
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(ApiConstants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<EmptyJsonResponse> response = testRestTemplate.exchange(
                createURLWithPort(ObAccountsConsentsController.BASE_PATH),
                HttpMethod.POST,
                new HttpEntity<>(headers),
                EmptyJsonResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenHeaderWithInvalidPayload_whenCreatePayment_thenReturnBadRequestError() {
        // given
        AccountsConsentsCreateRequest request = new AccountsConsentsCreateRequest();

        String auth = TestTools.createAuthorizationHeaderValue(request, TestTools.getInstance().getRsaPrivateKey());
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(ApiConstants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<ErrorResponse> response = doCreateRequestForError(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().errorClass).isEqualTo("WrongRequestFormat");
    }

    @Test
    public void givenHeaderWithExpiredSignature_whenMakeRequest_thenReturnJWTExpiredSignature() {
        // given
        String auth = TestTools.createAuthorizationHeaderValue(
                new DefaultRequest(),
                TestTools.getInstance().getRsaPrivateKey(),
                Instant.now().minus(1, ChronoUnit.MINUTES)
        );
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(ApiConstants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<ErrorResponse> response = doCreateRequestForError(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().errorClass).isEqualTo("JWTExpiredSignature");
        assertThat(response.getBody().errorMessage).isEqualTo("JWT Expired Signature.");
    }

    @Test
    public void givenHeaderWithInvalidAuthorizationsHeader_whenMakeRequest_thenReturnJWTDecodeError() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(ApiConstants.HEADER_AUTHORIZATION, "Bearer ABCDEFGH1234567890");

        // when
        ResponseEntity<ErrorResponse> response = doCreateRequestForError(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().errorClass).isEqualTo("JWTDecodeError");
        assertThat(response.getBody().errorMessage).isEqualTo("JWT strings must contain exactly 2 period characters. Found: 0");
    }

    private ResponseEntity<ErrorResponse> doCreateRequestForError(LinkedMultiValueMap<String, String> headers) {
        return testRestTemplate.exchange(
                createURLWithPort(ObAccountsConsentsController.BASE_PATH), HttpMethod.POST, new HttpEntity<>(headers), ErrorResponse.class
        );
    }
}
