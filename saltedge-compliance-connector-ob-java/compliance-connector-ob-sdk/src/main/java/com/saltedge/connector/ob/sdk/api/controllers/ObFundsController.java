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
import com.saltedge.connector.ob.sdk.api.models.errors.Unauthorized;
import com.saltedge.connector.ob.sdk.api.models.request.FundsConfirmationRequest;
import com.saltedge.connector.ob.sdk.api.models.response.FundsConfirmationResponse;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * This controller is responsible for checking funds availability.
 * https://priora.saltedge.com/docs/aspsp/ob/piis#connector-endpoints-funds-confirmation-fundscheck-get
 */
@RestController
@RequestMapping(ObFundsController.BASE_PATH)
@Validated
public class ObFundsController extends ObBaseController {
    public final static String BASE_PATH = ApiConstants.API_BASE_PATH + "/funds_check";
    private static final Logger log = LoggerFactory.getLogger(ObFundsController.class);

    /**
     * Fetch list of accounts belonging to a PSU and all relevant information about them.
     *
     * @param consent linked to Access-Token header
     * @param request funds confirmation data
     * @return funds confirmation response
     */
    @GetMapping
    public ResponseEntity<FundsConfirmationResponse> fundsConfirmation(
      @NotNull Consent consent,
      @Valid FundsConfirmationRequest request
    ) {
        if (!consent.isPiisConsent()) throw new Unauthorized.AccessDenied();
        boolean fundsAvailable = providerService.confirmFunds(
          consent.userId,
          consent.debtorAccount,
          request.instructedAmount
        );
        return new ResponseEntity<>(new FundsConfirmationResponse(fundsAvailable), HttpStatus.OK);
    }
}
