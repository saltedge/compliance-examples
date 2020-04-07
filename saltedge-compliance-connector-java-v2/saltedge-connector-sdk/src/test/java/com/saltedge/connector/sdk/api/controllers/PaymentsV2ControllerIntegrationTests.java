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
import com.saltedge.connector.sdk.TestTools;
import com.saltedge.connector.sdk.api.mapping.CreatePaymentRequest;
import com.saltedge.connector.sdk.api.mapping.DefaultRequest;
import com.saltedge.connector.sdk.api.mapping.EmptyJsonModel;
import com.saltedge.connector.sdk.api.mapping.ErrorResponse;
import com.saltedge.connector.sdk.callback.services.SessionsCallbackService;
import com.saltedge.connector.sdk.config.ApplicationProperties;
import com.saltedge.connector.sdk.models.PaymentOrder;
import com.saltedge.connector.sdk.provider.models.Amount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests Interceptors + PaymentsV2Controller
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentsV2ControllerIntegrationTests extends ControllerIntegrationTests {

    @Before
    public void setUp() {
        seedTokensRepository();
        callbackService.applicationProperties = new ApplicationProperties();
    }

    @Test
    public void givenHeaderWithValidAuthorization_whenCreatePayment_thenReturnOK() {
        // given
        CreatePaymentRequest request = new CreatePaymentRequest(
                "appName",
                "providerCode",
                "returnToUrl",
                new PaymentOrder(
                        new PaymentOrder.Account("iban1"),
                        "creditorName",
                        new PaymentOrder.Account("iban2"),
                        new Amount("1.0", "USD"),
                        "endToEndIdentification",
                        "remittanceInformationUnstructured"
                )
        );
        request.sessionSecret = "sessionSecret";
        String auth = TestTools.createAuthorizationHeaderValue(request, TestTools.getInstance().getRsaPrivateKey());
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(SDKConstants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<EmptyJsonModel> response = testRestTemplate.exchange(
                createURLWithPort(PaymentsV2Controller.BASE_PATH),
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
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.sessionSecret = "sessionSecret";
        String auth = TestTools.createAuthorizationHeaderValue(request, TestTools.getInstance().getRsaPrivateKey());
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(SDKConstants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                createURLWithPort(PaymentsV2Controller.BASE_PATH),
                HttpMethod.POST,
                new HttpEntity<>(headers),
                ErrorResponse.class
        );

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
        ResponseEntity<ErrorResponse> response = doCreatePaymentRequestForError(headers);

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
        ResponseEntity<ErrorResponse> response = doCreatePaymentRequestForError(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().errorClass).isEqualTo("JWTDecodeError");
        assertThat(response.getBody().errorMessage).isEqualTo("JWT strings must contain exactly 2 period characters. Found: 0");
    }

    private ResponseEntity<ErrorResponse> doCreatePaymentRequestForError(LinkedMultiValueMap<String, String> headers) {
        return testRestTemplate.exchange(
                createURLWithPort(PaymentsV2Controller.BASE_PATH), HttpMethod.POST, new HttpEntity<>(headers), ErrorResponse.class
        );
    }
}
