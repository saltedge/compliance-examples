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
import com.saltedge.connector.sdk.api.mapping.EmptyJsonModel;
import com.saltedge.connector.sdk.api.services.payments.PaymentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * This controller are responsible for creating payment orders on behalf of Customer via TPP interface.
 * Process of payment creation starts once Customer fills a payment template form and submits the request.
 */
@RestController
@RequestMapping(PaymentsV2Controller.BASE_PATH)
@Validated
public class PaymentsV2Controller extends BaseV2Controller {
    public final static String BASE_PATH = SDKConstants.API_BASE_PATH + "/payments";
    private static Logger log = LoggerFactory.getLogger(PaymentsV2Controller.class);
    @Autowired
    PaymentsService paymentsService;

    /**
     * Create a payment.
     * As a result, Connector will send a success, update or fail callback to Salt Edge PSD2 Compliance with result of the operation.
     *
     * @param request for token creation
     * @return empty JSON object
     */
    @PostMapping
    public ResponseEntity<EmptyJsonModel> create(@Valid CreatePaymentRequest request) {
        paymentsService.createPayment(request);
        return super.createEmptyOkResponseEntity();
    }
}
