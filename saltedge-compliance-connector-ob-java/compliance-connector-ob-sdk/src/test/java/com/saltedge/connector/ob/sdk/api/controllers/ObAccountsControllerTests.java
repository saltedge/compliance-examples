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
import com.saltedge.connector.ob.sdk.api.models.request.DefaultRequest;
import com.saltedge.connector.ob.sdk.api.models.request.TransactionsIndexRequest;
import com.saltedge.connector.ob.sdk.api.models.response.AccountsResponse;
import com.saltedge.connector.ob.sdk.api.models.response.TransactionsIndexResponse;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.provider.ProviderServiceAbs;
import com.saltedge.connector.ob.sdk.provider.dto.account.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class ObAccountsControllerTests {
    private final ProviderServiceAbs mockProviderService = Mockito.mock(ProviderServiceAbs.class);
    private final String userId = "1";
    private Consent consent;
    private ObAccountsController testController;

    @BeforeEach
    void setUp() {
        consent = Consent.createAisConsent("tppAppName", "1", "Authorised", Collections.emptyList(), null, null, null);
        consent.id = 1L;
        consent.userId = userId;
        consent.accessToken = "validToken";
        testController = new ObAccountsController();
        testController.providerService = mockProviderService;
    }

    @Test
    public void basePathTest() {
        assertThat(ObAccountsController.BASE_PATH).isEqualTo(ApiConstants.API_BASE_PATH + "/accounts");
    }

    @Test
    public void whenList_thenReturnStatus200AndAccountsList() {
        //given
        List<ObAccount> testData = getTestAccountsData();
        given(mockProviderService.getAccountsOfUser(userId)).willReturn(testData);

        //when
        ResponseEntity<AccountsResponse> result = testController.accountsIndex(consent, new DefaultRequest());

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().data).isEqualTo(testData);
    }

    @Test
    public void whenList_thenReturnStatus200AndTransactionsList() throws ParseException {
        //given
        String accountId = "1";
        List<ObTransaction> testData = getTestTransactionsData();
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();
        given(mockProviderService.getTransactionsOfAccount(userId, accountId, startDate, endDate, "fromId"))
            .willReturn(new TransactionsPage(testData, "nextId"));

        //when
        ResponseEntity<TransactionsIndexResponse> result = testController.transactionsIndexOfAccount(
            consent,
            accountId,
            new TransactionsIndexRequest(accountId, startDate, endDate, "fromId")
        );

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(result.getBody()).data).isEqualTo(testData);
        assertThat(Objects.requireNonNull(result.getBody()).meta.nextId).isEqualTo("nextId");
    }

    private List<ObAccount> getTestAccountsData() {
        List<ObBalance> balances = Lists.list(
            new ObBalance(new ObAmount("1000.0", "GBP"), "debit", "closingAvailable", Instant.now()),
            new ObBalance(new ObAmount("2000.0", "GBP"), "debit", "openingAvailable", Instant.now())
        );
        return Lists.list(new ObAccount(
            "1",
            "EUR",
            "account",
            "active",
            balances
        ));
    }

    private List<ObTransaction> getTestTransactionsData() throws ParseException {
        ArrayList<ObTransaction> result = new ArrayList<>();
        ObTransaction transaction = new ObTransaction();
        transaction.id = "t1";
        transaction.amount = new ObAmount("100.0", "GBP");
        transaction.status = "Booked";
        transaction.bookingDateTime = Instant.parse("2021-01-01T10:15:30.00Z");
        result.add(transaction);
        return result;
    }
}
