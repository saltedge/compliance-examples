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
import com.saltedge.connector.ob.sdk.api.models.request.AccountsConsentsCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.ConsentsRevokeRequest;
import com.saltedge.connector.ob.sdk.api.models.response.EmptyJsonResponse;
import com.saltedge.connector.ob.sdk.api.services.ObConsentsCreateService;
import com.saltedge.connector.ob.sdk.api.services.ObConsentsRevokeService;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Controller is responsible for implementing authentication and authorization of Customer.
 * Process of token creation starts once Customer grants his consent to TTP.
 * At the end of authorization, Connector should issue an access_token which can be used for further actions.
 * https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-tokens
 */
@RestController
@RequestMapping(ObAccountsConsentsController.BASE_PATH)
@Validated
public class ObAccountsConsentsController extends ObBaseController {
    public final static String BASE_PATH = ApiConstants.API_BASE_PATH + "/consents";
    private static final Logger log = LoggerFactory.getLogger(ObAccountsConsentsController.class);
    @Autowired
    ObConsentsCreateService createService;
    @Autowired
    ObConsentsRevokeService revokeService;

    /**
     * Create an access token with a set of access rights, named scopes.
     * As a result, Connector should send an success, update or fail callback to Salt Edge PSD2 Compliance with the result of the operation,
     * be it a success, fail or request for additional steps.
     *
     * @param request consent creation data
     * @return empty JSON response
     */
    @PostMapping
    public ResponseEntity<EmptyJsonResponse> create(@Valid AccountsConsentsCreateRequest request) {
        createService.createAisConsent(request);
        return super.createEmptyOkResponseEntity();
    }

    /**
     * Revoke an already existing and active access token.
     *
     * @param consentId unique identifier of Consent model
     * @param consent Consent model find by access-token
     * @param request revoke consent data
     * @return empty JSON response
     * @throws NotFound.ConsentNotFound if consent cannot be found or access-token not corresponds to consentId
     */
    @PatchMapping(path = "/{" + SDKConstants.KEY_CONSENT_ID + "}/revoke")
    public ResponseEntity<EmptyJsonResponse> revoke(
      @NotEmpty @PathVariable(name = SDKConstants.KEY_CONSENT_ID) String consentId,
      @NotNull Consent consent,
      @Valid ConsentsRevokeRequest request
    ) {
        if (consentId.equals(consent.consentId)) {
            revokeService.revokeConsent(consentId);
            return super.createEmptyOkResponseEntity();
        } else {
            throw new NotFound.ConsentNotFound();
        }
    }
}
