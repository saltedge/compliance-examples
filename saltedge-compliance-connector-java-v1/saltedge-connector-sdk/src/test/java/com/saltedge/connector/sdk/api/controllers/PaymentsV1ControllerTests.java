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

import com.saltedge.connector.sdk.api.mapping.*;
import com.saltedge.connector.sdk.api.services.payments.*;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.PaymentData;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class PaymentsV1ControllerTests {
    private CheckFundsService mockCheckFundsService = Mockito.mock(CheckFundsService.class);
    private CreatePaymentService mockCreatePaymentService = Mockito.mock(CreatePaymentService.class);
    private ConfirmPaymentService mockConfirmPaymentService = Mockito.mock(ConfirmPaymentService.class);
    private CancelPaymentService mockCancelPaymentService = Mockito.mock(CancelPaymentService.class);
    private ShowPaymentService mockShowPaymentService = Mockito.mock(ShowPaymentService.class);
    private PaymentsV1Controller controller = createController();

    @Test
    public void basePathTest() {
        assertThat(PaymentsV1Controller.BASE_PATH).isEqualTo(Constants.API_BASE_PATH + "/v1/payments");
    }

    @Test
    public void whenCheckFunds_thenReturnStatus200AndEmptyResponse() {
        Token token = new Token("1");
        CheckFundsRequest request = new CheckFundsRequest();
        ResponseEntity<EmptyJsonModel> result = controller.checkFunds(token, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockCheckFundsService).checkFunds(token, request);
        verifyNoInteractions(mockCreatePaymentService, mockConfirmPaymentService, mockCancelPaymentService, mockShowPaymentService);
    }

    @Test
    public void whenShow_thenReturnStatus200AndEmptyResponse() {
        Token token = new Token("1");
        ShowPaymentRequest request = new ShowPaymentRequest("100");
        PaymentData testData = new PaymentData(
                "1",
                "",
                "status",
                100.0,
                100.0,
                new ArrayList<>(),
                new HashMap<>(),
                new HashMap<>()
        );
        given(mockShowPaymentService.showPayment("100")).willReturn(testData);
        ResponseEntity<PaymentResponse> result = controller.show(token, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().data).isEqualTo(testData);
        verifyNoInteractions(mockCheckFundsService, mockCreatePaymentService, mockConfirmPaymentService, mockCancelPaymentService);
    }

    @Test
    public void whenCreate_thenReturnStatus200AndEmptyResponse() {
        Token token = new Token("1");
        CreatePaymentRequest request = new CreatePaymentRequest();
        ResponseEntity<EmptyJsonModel> result = controller.create(token, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockCreatePaymentService).createPayment(token, request.sessionSecret, request.prioraPaymentId, request.paymentType, request.redirectUrl, request.getPaymentAttributes(), request.getExtra());
        verifyNoInteractions(mockCheckFundsService, mockConfirmPaymentService, mockCancelPaymentService, mockShowPaymentService);
    }

    @Test
    public void whenConfirm_thenReturnStatus200AndEmptyResponse() {
        Token token = new Token("1");
        ConfirmPaymentRequest request = new ConfirmPaymentRequest();
        request.sessionSecret = "sessionSecret";
        request.paymentId = "2";
        request.prioraPaymentId = 3L;
        request.originalRequest = new ConfirmPaymentRequest.OriginalRequest();
        request.originalRequest.payload = new ConfirmPaymentClientPayload();
        request.originalRequest.payload.data = new ConfirmPaymentClientPayload.Data();
        request.originalRequest.payload.data.credentials = new HashMap<>();
        ResponseEntity<EmptyJsonModel> result = controller.confirm(token, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockConfirmPaymentService).confirmPayment("sessionSecret", "2", 3L, new HashMap<>());
        verifyNoInteractions(mockCheckFundsService, mockCreatePaymentService, mockCancelPaymentService, mockShowPaymentService);
    }

    @Test
    public void whenCancel_thenReturnStatus200AndEmptyResponse() {
        Token token = new Token("1");
        CancelPaymentRequest request = new CancelPaymentRequest("2");
        ResponseEntity<EmptyJsonModel> result = controller.cancel(token, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockCancelPaymentService).cancelPayment("2");
        verifyNoInteractions(mockCheckFundsService, mockCreatePaymentService, mockConfirmPaymentService, mockShowPaymentService);
    }

    private PaymentsV1Controller createController() {
        PaymentsV1Controller controller = new PaymentsV1Controller();
        controller.checkFundsService = mockCheckFundsService;
        controller.createPaymentService = mockCreatePaymentService;
        controller.confirmPaymentService = mockConfirmPaymentService;
        controller.cancelPaymentService = mockCancelPaymentService;
        controller.showPaymentService = mockShowPaymentService;
        return controller;
    }
}
