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
import com.saltedge.connector.ob.sdk.api.models.errors.BadRequest;
import com.saltedge.connector.ob.sdk.api.models.request.PaymentCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.response.EmptyJsonResponse;
import com.saltedge.connector.ob.sdk.api.services.ObPaymentService;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Controller is responsible for implementing payment creation.
 *
 * https://priora.saltedge.com/docs/aspsp/ob/pis#connector-endpoints-payments-payments-payment
 */
@RestController
@RequestMapping(ObPaymentsController.BASE_PATH)
@Validated
public class ObPaymentsController extends ObBaseController {
    public final static String BASE_PATH = ApiConstants.API_BASE_PATH + "/payments";
    private static final Logger log = LoggerFactory.getLogger(ObPaymentsController.class);
    @Autowired
    ObPaymentService paymentService;

    /**
     * Create a payment. As a result, Connector should send an update callback to Salt Edge PSD2 Compliance with the result of the operation for additional steps.
     *
     * @param consent linked to Access-Token header
     * @param request payment initiation data
     * @return empty JSON response
     */
    @PostMapping
    public ResponseEntity<EmptyJsonResponse> create(@NotNull Consent consent, @Valid PaymentCreateRequest request) {
        if (!Objects.equals(consent.consentId, request.consentId)) {
            throw new BadRequest.InvalidAttributeValue("PaymentCreateRequest.consent_id");
        }
        paymentService.initiatePayment(consent.consentId, request);
        return super.createEmptyOkResponseEntity();
    }
}
