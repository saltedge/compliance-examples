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
package com.saltedge.connector.sdk.api.controllers.ais;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.controllers.BaseV2Controller;
import com.saltedge.connector.sdk.api.models.Meta;
import com.saltedge.connector.sdk.api.models.requests.DefaultRequest;
import com.saltedge.connector.sdk.api.models.requests.TransactionsRequest;
import com.saltedge.connector.sdk.api.models.responses.CardAccountsResponse;
import com.saltedge.connector.sdk.api.models.responses.CardTransactionsResponse;
import com.saltedge.connector.sdk.models.CardTransactionsPage;
import com.saltedge.connector.sdk.models.domain.AisToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * This controller is responsible for fetching card account information for Account Information Service.
 * Card account Information Endpoints are responsible for the access to card account identification data
 * and card account transactions history.
 * https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-card_accounts
 */
@RestController
@RequestMapping(CardAccountsV2Controller.BASE_PATH)
@Validated
public class CardAccountsV2Controller extends BaseV2Controller {
    public final static String BASE_PATH = SDKConstants.API_BASE_PATH + "/card_accounts";
    private static final Logger log = LoggerFactory.getLogger(CardAccountsV2Controller.class);

    /**
     * Fetch list of card accounts belonging to a PSU (Bank Customer) and all relevant information about them.
     *
     * @param token linked to Access-Token header
     * @param request request with sessionSecret
     * @return list of Card Account Data
     */
    @GetMapping
    public ResponseEntity<CardAccountsResponse> cardAccounts(@NotNull AisToken token, @Valid DefaultRequest request) {
        return new ResponseEntity<>(
            new CardAccountsResponse(providerService.getCardAccountsOfUser(token.userId)),
            HttpStatus.OK
        );
    }

    /**
     * Fetch all transactions related to a card account.
     *
     * @param token linked to Access-Token header
     * @param accountId unique id of bank account
     * @param request data
     * @return list of card transactions data with nextId of next page.
     */
    @GetMapping(path = "/{" + SDKConstants.KEY_ACCOUNT_ID + "}/transactions")
    public ResponseEntity<CardTransactionsResponse> transactionsOfCardAccount(
            @NotNull AisToken token,
            @NotEmpty @PathVariable(name = SDKConstants.KEY_ACCOUNT_ID) String accountId,
            @Valid TransactionsRequest request
    ) {
        CardTransactionsPage resultPage = providerService.getTransactionsOfCardAccount(
                token.userId,
                accountId,
                request.fromDate,
                request.toDate,
                request.fromId
        );
        return new ResponseEntity<>(
            new CardTransactionsResponse(resultPage.cardTransactions, new Meta(resultPage.nextId)),
            HttpStatus.OK
        );
    }
}
