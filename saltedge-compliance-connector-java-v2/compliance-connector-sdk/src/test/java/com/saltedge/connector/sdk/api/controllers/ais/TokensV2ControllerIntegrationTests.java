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
package com.saltedge.connector.sdk.api.controllers.ais;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.TestTools;
import com.saltedge.connector.sdk.api.controllers.ControllerIntegrationTests;
import com.saltedge.connector.sdk.api.models.EmptyJsonModel;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.api.models.requests.CreateAisTokenRequest;
import com.saltedge.connector.sdk.api.models.requests.DefaultRequest;
import com.saltedge.connector.sdk.api.models.responses.ErrorResponse;
import com.saltedge.connector.sdk.config.ApplicationProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests Interceptors + TokensV2Controller
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokensV2ControllerIntegrationTests extends ControllerIntegrationTests {

    @BeforeEach
    public void setUp() {
        seedTokensRepository();
        callbackService.applicationProperties = new ApplicationProperties();
    }

    @Test
    public void givenValidPayload_whenCreatePayment_thenReturnOK() {
        // given
        CreateAisTokenRequest request = new CreateAisTokenRequest();
        request.sessionSecret = "sessionSecret";
        request.providerCode = "code";
        request.tppAppName = "name";
        request.authorizationType = "oAuth";
        request.redirectUrl = "url";
        request.requestedConsent = new ProviderConsents(ProviderConsents.GLOBAL_CONSENT_VALUE);
        request.validUntil = LocalDate.now().plusDays(1);
        request.recurringIndicator = true;

        String auth = TestTools.createAuthorizationHeaderValue(request, TestTools.getInstance().getRsaPrivateKey());
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(SDKConstants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<EmptyJsonModel> response = testRestTemplate.exchange(
                createURLWithPort(TokensV2Controller.BASE_PATH),
                HttpMethod.POST,
                new HttpEntity<>(headers),
                EmptyJsonModel.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenHeaderWithInvalidPayload_whenCreatePayment_thenReturnBadRequestError() {
        // given
        CreateAisTokenRequest request = new CreateAisTokenRequest();
        request.sessionSecret = "sessionSecret";
        String auth = TestTools.createAuthorizationHeaderValue(request, TestTools.getInstance().getRsaPrivateKey());
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(SDKConstants.HEADER_AUTHORIZATION, auth);

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
                new DefaultRequest("sessionSecret"),
                TestTools.getInstance().getRsaPrivateKey(),
                Instant.now().minus(1, ChronoUnit.MINUTES)
        );
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(SDKConstants.HEADER_AUTHORIZATION, auth);

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
        headers.add(SDKConstants.HEADER_AUTHORIZATION, "Bearer ABCDEFGH1234567890");

        // when
        ResponseEntity<ErrorResponse> response = doCreateRequestForError(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().errorClass).isEqualTo("JWTDecodeError");
        assertThat(response.getBody().errorMessage).isEqualTo("JWT strings must contain exactly 2 period characters. Found: 0");
    }

    private ResponseEntity<ErrorResponse> doCreateRequestForError(LinkedMultiValueMap<String, String> headers) {
        return testRestTemplate.exchange(
                createURLWithPort(TokensV2Controller.BASE_PATH), HttpMethod.POST, new HttpEntity<>(headers), ErrorResponse.class
        );
    }
}
