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
import com.saltedge.connector.ob.sdk.api.models.request.PaymentCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.response.EmptyJsonResponse;
import com.saltedge.connector.ob.sdk.api.services.ObPaymentService;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.provider.ProviderServiceAbs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class ObPaymentsControllerTests {
    private final ObPaymentService mockPaymentsService = Mockito.mock(ObPaymentService.class);
    private final ProviderServiceAbs mockProviderService = Mockito.mock(ProviderServiceAbs.class);
    private Consent testConsent;
    private ObPaymentsController testController;

    @BeforeEach
    void setUp() {
        testConsent = new Consent();
        testConsent.userId = "1";
        testController = new ObPaymentsController();
        testController.providerService = mockProviderService;
        testController.paymentService = mockPaymentsService;
    }

    @Test
    public void basePathTest() {
        assertThat(ObPaymentsController.BASE_PATH).isEqualTo(ApiConstants.API_BASE_PATH + "/payments");
    }

    @Test
    public void whenCreate_thenReturnStatus200AndEmptyResponse() {
        //given
        PaymentCreateRequest request = new PaymentCreateRequest();

        //when
        ResponseEntity<EmptyJsonResponse> result = testController.create(testConsent, request);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        Mockito.verify(mockPaymentsService).initiatePayment(testConsent.consentId, request);
        Mockito.verifyNoMoreInteractions(mockProviderService, mockPaymentsService);
    }
}
