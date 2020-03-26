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
import com.saltedge.connector.sdk.api.mapping.CreatePaymentRequest;
import com.saltedge.connector.sdk.api.mapping.CreateTokenRequest;
import com.saltedge.connector.sdk.api.mapping.EmptyJsonModel;
import com.saltedge.connector.sdk.api.mapping.RevokeTokenRequest;
import com.saltedge.connector.sdk.api.services.payments.PaymentsService;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.ProviderServiceAbs;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class PaymentsV2ControllerTests {
    PaymentsService mockPaymentsService = Mockito.mock(PaymentsService.class);
    ProviderServiceAbs mockProviderService = Mockito.mock(ProviderServiceAbs.class);
    PaymentsV2Controller controller = createController();

    @Test
    public void basePathTest() {
        assertThat(PaymentsV2Controller.BASE_PATH).isEqualTo(SDKConstants.API_BASE_PATH + "/payments");
    }

    @Test
    public void whenCreate_thenReturnStatus200AndEmptyResponse() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        ResponseEntity<EmptyJsonModel> result = controller.create(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockPaymentsService).createPayment(request);
        verifyNoInteractions(mockProviderService);
    }

    private PaymentsV2Controller createController() {
        PaymentsV2Controller controller = new PaymentsV2Controller();
        controller.paymentsService = mockPaymentsService;
        controller.providerService = mockProviderService;
        return controller;
    }
}
