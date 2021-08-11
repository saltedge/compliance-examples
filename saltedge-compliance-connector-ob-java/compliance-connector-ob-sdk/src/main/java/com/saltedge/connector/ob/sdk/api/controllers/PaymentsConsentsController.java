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

import com.saltedge.connector.ob.sdk.SDKConstants;
import com.saltedge.connector.ob.sdk.api.ApiConstants;
import com.saltedge.connector.ob.sdk.api.models.errors.NotFound;
import com.saltedge.connector.ob.sdk.api.models.errors.Unauthorized;
import com.saltedge.connector.ob.sdk.api.models.request.PaymentConsentsCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.PaymentFundsConfirmationRequest;
import com.saltedge.connector.ob.sdk.api.models.response.EmptyJsonResponse;
import com.saltedge.connector.ob.sdk.api.models.response.FundsConfirmationResponse;
import com.saltedge.connector.ob.sdk.api.services.ConsentsCreateService;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
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
import javax.validation.constraints.NotNull;

/**
 * Controller is responsible for implementing authentication and authorization of Customer.
 * Process of token creation starts once Customer grants his consent to TTP.
 * At the end of authorization, Connector should issue an access_token which can be used for further actions.
 * https://priora.saltedge.com/docs/aspsp/ob/pis#connector-endpoints-payments-payments-consent
 */
@RestController
@RequestMapping(PaymentsConsentsController.BASE_PATH)
@Validated
public class PaymentsConsentsController extends BaseController {
    public final static String BASE_PATH = ApiConstants.API_BASE_PATH + "/payment_consents";
    private static final Logger log = LoggerFactory.getLogger(PaymentsConsentsController.class);
    @Autowired
    private ConsentsCreateService createService;

    /**
     * Create an access token with a set of access rights, named scopes.
     * As a result, Connector should send a success, update or fail callback to Salt Edge PSD2 Compliance with the result of the operation,
     * be it a success, fail or request for additional steps.
     *
     * @param request for consent creation
     * @return empty JSON object
     */
    @PostMapping
    public ResponseEntity<EmptyJsonResponse> create(@Valid PaymentConsentsCreateRequest request) {
        createService.createPisConsent(request);
        return super.createEmptyOkResponseEntity();
    }

    /**
     * Checks whether a specific amount is available at point of time of the request on an account addressed by IBAN or other available identifiers.
     *
     * @param consent linked to Access-Token header
     * @param accountId unique id of bank account
     * @param request data
     * @return list of transactions data with nextId of next page
     */
    @GetMapping(path = "/{" + SDKConstants.KEY_ACCOUNT_ID + "}/transactions")
    public ResponseEntity<FundsConfirmationResponse> fundsConfirmation(
      @NotNull Consent consent,
      @Valid PaymentFundsConfirmationRequest request
    ) {
        if (!consent.isPisConsent()) throw new Unauthorized.AccessDenied();
        if (consent.payment.debtorAccount == null || consent.payment.instructedAmount == null) throw new NotFound.PaymentNotFound();
        boolean fundsAvailable = providerService.confirmFunds(
          consent.userId,
          consent.payment.debtorAccount,
          consent.payment.instructedAmount
        );
        return new ResponseEntity<>(new FundsConfirmationResponse(fundsAvailable), HttpStatus.OK);
    }
}
