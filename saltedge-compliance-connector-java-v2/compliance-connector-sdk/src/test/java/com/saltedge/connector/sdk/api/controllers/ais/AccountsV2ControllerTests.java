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
import com.saltedge.connector.sdk.api.models.Account;
import com.saltedge.connector.sdk.api.models.AccountBalance;
import com.saltedge.connector.sdk.api.models.Transaction;
import com.saltedge.connector.sdk.api.models.requests.DefaultRequest;
import com.saltedge.connector.sdk.api.models.requests.TransactionsRequest;
import com.saltedge.connector.sdk.api.models.responses.AccountsResponse;
import com.saltedge.connector.sdk.api.models.responses.TransactionsResponse;
import com.saltedge.connector.sdk.models.TransactionsPage;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.provider.ProviderServiceAbs;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class AccountsV2ControllerTests {
    ProviderServiceAbs mockProviderService = Mockito.mock(ProviderServiceAbs.class);

    @Test
    public void basePathTest() {
        assertThat(AccountsV2Controller.BASE_PATH).isEqualTo(SDKConstants.API_BASE_PATH + "/accounts");
    }

    @Test
    public void whenList_thenReturnStatus200AndAccountsList() {
        // given
        List<Account> testData = getTestAccountsData();
        given(mockProviderService.getAccountsOfUser("1")).willReturn(testData);

        // when
        AccountsV2Controller controller = new AccountsV2Controller();
        controller.providerService = mockProviderService;
        ResponseEntity<AccountsResponse> result = controller.accounts(
                new AisToken("1"),
                new DefaultRequest()
        );

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().data).isEqualTo(testData);
    }

    @Test
    public void whenList_thenReturnStatus200AndTransactionsList() throws ParseException {
        List<Transaction> testData = getTestTransactionsData();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate;
        given(mockProviderService.getTransactionsOfAccount("1", "1", startDate, endDate, "fromId"))
                .willReturn(new TransactionsPage(testData, "nextId"));

        AccountsV2Controller controller = new AccountsV2Controller();
        controller.providerService = mockProviderService;

        ResponseEntity<TransactionsResponse> result = controller.transactionsOfAccount(
                new AisToken("1"),
                "1",
                new TransactionsRequest("1", startDate, endDate, "fromId", "sessionSecret")
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(result.getBody()).data).isEqualTo(testData);
        assertThat(Objects.requireNonNull(result.getBody()).meta.nextId).isEqualTo("nextId");
    }

    private List<Account> getTestAccountsData() {
        ArrayList<AccountBalance> balances = new ArrayList<>();
        balances.add(new AccountBalance(String.format("%.2f", 1000.0), "EUR", "closingAvailable"));
        balances.add(new AccountBalance(String.format("%.2f", 1000.0), "EUR", "openingAvailable"));
        ArrayList<Account> result = new ArrayList<>();
        result.add(new Account(
                "1",
                "account",
                balances,
                "active",
                "EUR"
        ));
        return result;
    }

    private List<Transaction> getTestTransactionsData() throws ParseException {
        ArrayList<Transaction> result = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setId("t1");
        transaction.setAmount("100.0");
        transaction.setCurrencyCode("EUR");
        transaction.setStatus("booked");
        transaction.setBookingDate(LocalDate.parse("2020-01-01"));
        result.add(transaction);
        return result;
    }
}
