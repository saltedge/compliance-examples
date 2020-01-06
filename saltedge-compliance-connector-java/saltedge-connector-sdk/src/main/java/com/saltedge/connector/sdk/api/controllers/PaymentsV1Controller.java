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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * This controller are responsible for creating payment orders on behalf of Customer via TPP interface.
 * Process of payment creation starts once Customer fills a payment template form and submits the request.
 */
@RestController
@RequestMapping(PaymentsV1Controller.BASE_PATH)
@Validated
public class PaymentsV1Controller extends BaseV1Controller {
    private static Logger log = LoggerFactory.getLogger(PaymentsV1Controller.class);
    public final static String BASE_PATH = Constants.API_BASE_PATH + "/v1/payments";
    @Autowired
    CheckFundsService checkFundsService;
    @Autowired
    CreatePaymentService createPaymentService;
    @Autowired
    ConfirmPaymentService confirmPaymentService;
    @Autowired
    CancelPaymentService cancelPaymentService;
    @Autowired
    ShowPaymentService showPaymentService;

    /**
     * Is used to check availability of funds for a specific account.
     * As a result, Connector will send a success or fail callback to Salt Edge PSD2 Compliance with result of the operation.
     *
     * @param token linked to Access-Token header
     * @param request data
     * @return empty JSON object
     */
    @PostMapping("/check_funds")
    public ResponseEntity<EmptyJsonModel> checkFunds(Token token, @Valid CheckFundsRequest request) {
        checkFundsService.checkFunds(token, request);
        return super.createEmptyOkResponseEntity();
    }

    /**
     * Fetch all data relevant to a payment including fees, status and other.
     *
     * @param token linked to Access-Token header
     * @param request params
     * @return Payment data by payment id
     */
    @GetMapping("/show")
    public ResponseEntity<PaymentResponse> show(Token token, @Valid ShowPaymentRequest request) {
        PaymentData result = showPaymentService.showPayment(request.paymentId);
        return new ResponseEntity<>(new PaymentResponse(result), HttpStatus.OK);
    }

    /**
     * Create a payment.
     * As a result, Connector will send a success, update or fail callback to Salt Edge PSD2 Compliance with result of the operation.
     *
     * @param token linked to Access-Token header
     * @param request params
     * @return empty JSON object
     */
    @PostMapping("/create")
    public ResponseEntity<EmptyJsonModel> create(Token token, @Valid CreatePaymentRequest request) {
        createPaymentService.createPayment(token, request.sessionSecret, request.prioraPaymentId, request.paymentType,
                request.redirectUrl, request.getPaymentAttributes(), request.getExtra());
        return super.createEmptyOkResponseEntity();
    }

    /**
     * Is used for processing additional interactive steps in the process of payment creation.
     * As a result, Connector will send a success or fail callback to Salt Edge PSD2 Compliance with result of the operation.
     *
     * @param token linked to Access-Token header
     * @param request params
     * @return empty JSON object
     */
    @PostMapping("/confirm")
    public ResponseEntity<EmptyJsonModel> confirm(Token token, @Valid ConfirmPaymentRequest request) {
        confirmPaymentService.confirmPayment(
                request.sessionSecret,
                request.paymentId,
                request.prioraPaymentId,
                request.getCredentials()
        );
        return super.createEmptyOkResponseEntity();
    }

    /**
     * Cancel the payment that is in the process of creation, meaning it has not been confirmed yet.
     *
     * @param token linked to Access-Token header
     * @param request params
     * @return empty JSON object
     */
    @PostMapping("/cancel")
    public ResponseEntity<EmptyJsonModel> cancel(Token token, @NotNull CancelPaymentRequest request) {
        cancelPaymentService.cancelPayment(request.paymentId);
        return super.createEmptyOkResponseEntity();
    }
}
