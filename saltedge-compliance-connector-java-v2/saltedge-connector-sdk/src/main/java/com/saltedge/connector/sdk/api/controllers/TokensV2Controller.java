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

import com.saltedge.connector.sdk.api.mapping.CreateTokenRequest;
import com.saltedge.connector.sdk.api.mapping.EmptyJsonModel;
import com.saltedge.connector.sdk.api.mapping.RevokeTokenRequest;
import com.saltedge.connector.sdk.api.services.tokens.ConfirmTokenService;
import com.saltedge.connector.sdk.api.services.tokens.CreateTokenService;
import com.saltedge.connector.sdk.api.services.tokens.RevokeTokenService;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping(TokensV2Controller.BASE_PATH)
@Validated
public class TokensV2Controller extends BaseV2Controller {
    public final static String BASE_PATH = Constants.API_BASE_PATH + "/tokens";
    private static Logger log = LoggerFactory.getLogger(TokensV2Controller.class);
    @Autowired
    CreateTokenService createTokenService;
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
    @PostMapping
    public ResponseEntity<EmptyJsonModel> create(@Valid CreateTokenRequest request) {
        createTokenService.startAuthorization(request);
        return super.createEmptyOkResponseEntity();
    }

    /**
     * Revoke an already existing and active access token.
     *
     * @param token linked to Access-Token header
     * @param request with sessionSecret
     * @return empty JSON object
     */
    @PatchMapping("/revoke")
    public ResponseEntity<EmptyJsonModel> revoke(Token token, @Valid RevokeTokenRequest request) {
        revokeService.revokeTokenAsync(token);
        return super.createEmptyOkResponseEntity();
    }
}
