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

import com.saltedge.connector.sdk.api.mapping.AccountsResponse;
import com.saltedge.connector.sdk.api.mapping.DefaultRequest;
import com.saltedge.connector.sdk.api.mapping.EmptyJsonModel;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * This controller is responsible for fetching account information for Account Information Service.
 */
@RestController
@RequestMapping(AccountsV1Controller.BASE_PATH)
@Validated
public class AccountsV1Controller extends BaseV1Controller {
    public final static String BASE_PATH = Constants.API_BASE_PATH + "/v1/accounts";

    /**
     * Fetch list of accounts belonging to a User and all relevant information about them.
     *
     * @param token linked to Access-Token header
     * @param request request with sessionSecret
     * @return list of Account Data
     */
    @GetMapping
    public ResponseEntity<AccountsResponse> list(Token token, @Valid DefaultRequest request) {
        return new ResponseEntity<>(new AccountsResponse(providerService.getAccounts(token.userId)), HttpStatus.OK);
    }

    /**
     * In case the connector uses a different database from Core Banking,
     * this endpoint enables the process of refreshing accounts and transactions on connector side
     * before sending them to SaltEdge PSD2 Compliance Solution.
     *
     * Now is implemented for compliance.
     *
     * @param token linked to Access-Token header
     * @param request request with sessionSecret
     * @return empty JSON object
     */
    @PutMapping
    public ResponseEntity<EmptyJsonModel> refresh(Token token, @Valid DefaultRequest request) {
        return super.createEmptyOkResponseEntity();
    }
}
