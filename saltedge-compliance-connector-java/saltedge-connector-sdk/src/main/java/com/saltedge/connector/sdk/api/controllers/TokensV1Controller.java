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

import com.saltedge.connector.sdk.api.mapping.ConfirmTokenRequest;
import com.saltedge.connector.sdk.api.mapping.CreateTokenRequest;
import com.saltedge.connector.sdk.api.mapping.DefaultRequest;
import com.saltedge.connector.sdk.api.mapping.EmptyJsonModel;
import com.saltedge.connector.sdk.api.services.tokens.ConfirmTokenService;
import com.saltedge.connector.sdk.api.services.tokens.CreateTokenService;
import com.saltedge.connector.sdk.api.services.tokens.ReconnectTokenService;
import com.saltedge.connector.sdk.api.services.tokens.RevokeTokenService;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Controller is responsible for implementing authentication and authorization of Customer.
 * Process of token creation starts once Customer grants his consent to TTP.
 * At the end of authorization, Connector should issue an access_token which can be used for further actions.
 */
@RestController
@RequestMapping(TokensV1Controller.BASE_PATH)
@Validated
public class TokensV1Controller extends BaseV1Controller {
    public final static String BASE_PATH = Constants.API_BASE_PATH + "/v1/tokens";
    public final static String TOKEN_RECONNECT_PATH = BASE_PATH + "/reconnect";
    @Autowired
    CreateTokenService createTokenService;
    @Autowired
    ReconnectTokenService reconnectTokenService;
    @Autowired
    ConfirmTokenService confirmService;
    @Autowired
    RevokeTokenService revokeService;

    /**
     * Create an access token with a set of access rights, named scopes.
     * As a result, Connector will send an update or fail callback to Salt Edge PSD2 Compliance with result of the operation,
     * be it success, fail or request for additional steps.
     *
     * @param request for token creation
     * @return empty JSON object
     */
    @PostMapping("/create")
    public ResponseEntity<EmptyJsonModel> create(@Valid CreateTokenRequest request) {
        createTokenService.startAuthorization(request);
        return super.createEmptyOkResponseEntity();
    }

    /**
     * This endpoint is used for processing additional interactive steps in the process of access token creation.
     * As a result, Connector will send a success or fail callback to Salt Edge PSD2 Compliance with result of the operation.
     *
     * @param request for token confirmation
     * @return empty JSON object
     */
    @PostMapping("/confirm")
    public ResponseEntity<EmptyJsonModel> confirm(@Valid ConfirmTokenRequest request) {
        confirmService.confirmToken(request.sessionSecret, request.getCredentials());
        return super.createEmptyOkResponseEntity();
    }

    /**
     * This endpoint is used when a TPP asks to refresh an active token.
     * ASPSP should determine behavior for this action:
     * ASPSP can just return a new token sending it as session/success callback,
     * ask for MFA using sessions/update callback
     * or just deny using sessions/fail callback.
     *
     * @param token linked to Access-Token header
     * @param request with sessionSecret
     * @return empty JSON object
     */
    @PostMapping(TOKEN_RECONNECT_PATH)
    public ResponseEntity<EmptyJsonModel> reconnect(Token token, @Valid DefaultRequest request) {
        reconnectTokenService.reconnect(token, request.sessionSecret);
        return super.createEmptyOkResponseEntity();
    }

    /**
     * Revoke an already existing and active access token.
     *
     * @param token linked to Access-Token header
     * @param request with sessionSecret
     * @return empty JSON object
     */
    @PostMapping("/revoke")
    public ResponseEntity<EmptyJsonModel> revoke(Token token, @Valid DefaultRequest request) {
        revokeService.revokeTokenAsync(token);
        return super.createEmptyOkResponseEntity();
    }

    /**
     * Cancel any access token that is in the process of enrollment, meaning it has not been confirmed yet.
     *
     * @param request with sessionSecret
     * @return empty JSON object
     */
    @PostMapping("/cancel")
    public ResponseEntity<EmptyJsonModel> cancel(@Valid DefaultRequest request) {
        revokeService.revokeTokenBySessionSecret(request.sessionSecret);
        return super.createEmptyOkResponseEntity();
    }
}
