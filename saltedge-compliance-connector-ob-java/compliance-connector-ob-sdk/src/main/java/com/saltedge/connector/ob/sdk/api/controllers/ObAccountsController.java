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
import com.saltedge.connector.ob.sdk.api.models.request.DefaultRequest;
import com.saltedge.connector.ob.sdk.api.models.request.TransactionsIndexRequest;
import com.saltedge.connector.ob.sdk.api.models.response.AccountsResponse;
import com.saltedge.connector.ob.sdk.api.models.response.TransactionsIndexResponse;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.provider.dto.account.Meta;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccount;
import com.saltedge.connector.ob.sdk.provider.dto.account.TransactionsPage;
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
import java.util.Collections;
import java.util.List;

/**
 * This controller is responsible for fetching list of accounts belonging to a PSU and transactions.
 * https://priora.saltedge.com/docs/aspsp/ob/ais#connector-endpoints-accounts
 */
@RestController
@RequestMapping(ObAccountsController.BASE_PATH)
@Validated
public class ObAccountsController extends ObBaseController {
    public final static String BASE_PATH = ApiConstants.API_BASE_PATH + "/accounts";
    private static final Logger log = LoggerFactory.getLogger(ObAccountsController.class);

    /**
     * Fetch list of accounts belonging to a PSU and all relevant information about them.
     *
     * @param consent linked to Access-Token header
     * @param request empty request
     * @return list of Account Data
     */
    @GetMapping
    public ResponseEntity<AccountsResponse> accountsIndex(@NotNull Consent consent, @Valid DefaultRequest request) {
        List<ObAccount> accounts = providerService.getAccountsOfUser(consent.userId);
        if (accounts == null) accounts = Collections.emptyList();
        AccountsResponse response = new AccountsResponse(accounts);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Fetch transactions which belong to requested account.
     *
     * @param consent linked to Access-Token header
     * @param accountId unique id of bank account
     * @param request transactions list request data
     * @return list of transactions data with nextId of next page
     */
    @GetMapping(path = "/{" + SDKConstants.KEY_ACCOUNT_ID + "}/transactions")
    public ResponseEntity<TransactionsIndexResponse> transactionsIndexOfAccount(
      @NotNull Consent consent,
      @NotEmpty @PathVariable(name = SDKConstants.KEY_ACCOUNT_ID) String accountId,
      @Valid TransactionsIndexRequest request
    ) {
        TransactionsPage resultPage = providerService.getTransactionsOfAccount(
          consent.userId,
          accountId,
          request.fromDate,
          request.toDate,
          request.fromId
        );
        if (resultPage == null) resultPage = new TransactionsPage(Collections.emptyList(), null);
        return new ResponseEntity<>(new TransactionsIndexResponse(resultPage.transactions, new Meta(resultPage.nextId)), HttpStatus.OK);
    }
}
