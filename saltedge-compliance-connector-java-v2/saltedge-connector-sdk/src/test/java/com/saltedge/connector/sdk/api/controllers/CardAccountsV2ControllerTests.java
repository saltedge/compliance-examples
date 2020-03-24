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

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.mapping.CardAccountsResponse;
import com.saltedge.connector.sdk.api.mapping.CardTransactionsResponse;
import com.saltedge.connector.sdk.api.mapping.DefaultRequest;
import com.saltedge.connector.sdk.api.mapping.TransactionsRequest;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.ProviderServiceAbs;
import com.saltedge.connector.sdk.provider.models.*;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class CardAccountsV2ControllerTests {
    ProviderServiceAbs mockProviderService = Mockito.mock(ProviderServiceAbs.class);

    @Test
    public void basePathTest() {
        assertThat(CardAccountsV2Controller.BASE_PATH).isEqualTo(SDKConstants.API_BASE_PATH + "/card_accounts");
    }

    @Test
    public void whenList_thenReturnStatus200AndAccountsList() {
        // given
        List<CardAccount> testData = getTestAccountsData();
        given(mockProviderService.getCardAccountsOfUser("1")).willReturn(testData);

        // when
        CardAccountsV2Controller controller = new CardAccountsV2Controller();
        controller.providerService = mockProviderService;
        ResponseEntity<CardAccountsResponse> result = controller.cardAccounts(
                new Token("1"),
                new DefaultRequest()
        );

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().data).isEqualTo(testData);
    }

    @Test
    public void whenList_thenReturnStatus200AndTransactionsList() throws ParseException {
        List<CardTransaction> testData = getTestTransactionsData();
        Date startDate = new Date();
        Date endDate = startDate;
        given(mockProviderService.getTransactionsOfCardAccount("1", "1", startDate, endDate)).willReturn(testData);

        CardAccountsV2Controller controller = new CardAccountsV2Controller();
        controller.providerService = mockProviderService;

        ResponseEntity<CardTransactionsResponse> result = controller.transactionsOfCardAccount(
                new Token("1"),
                "1",
                new TransactionsRequest("1", startDate, endDate, "sessionSecret")
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().data).isEqualTo(testData);
    }

    private List<CardAccount> getTestAccountsData() {
        ArrayList<CardAccountBalance> balances = new ArrayList<>();
        balances.add(new CardAccountBalance(String.format("%.2f", 1000.0), "EUR", "closingAvailable"));
        balances.add(new CardAccountBalance(String.format("%.2f", 1000.0), "EUR", "openingAvailable"));
        ArrayList<CardAccount> result = new ArrayList<>();
        result.add(new CardAccount(
                "1",
                "name",
                "**** **** **** 1111",
                "EUR",
                "product",
                "active",
                balances,
                new Amount("0.0", "EUR"),
                new CardAccountExtra()
        ));
        return result;
    }

    private List<CardTransaction> getTestTransactionsData() throws ParseException {
        ArrayList<CardTransaction> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date madeOn = dateFormat.parse("2020-01-01");
        result.add(new CardTransaction(
                "t1",
                "100.00",
                "EUR",
                "booked",
                madeOn,
                "details",
                null, null, null, null, null, null, null, null, null, null, null, null
        ));
        return result;
    }
}
