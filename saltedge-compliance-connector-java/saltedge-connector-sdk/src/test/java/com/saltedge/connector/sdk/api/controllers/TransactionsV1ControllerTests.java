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

import com.saltedge.connector.sdk.api.mapping.TransactionsRequest;
import com.saltedge.connector.sdk.api.mapping.TransactionsResponse;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.ProviderApi;
import com.saltedge.connector.sdk.provider.models.TransactionData;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class TransactionsV1ControllerTests {
    ProviderApi mockProviderService = Mockito.mock(ProviderApi.class);

    @Test
    public void basePathTest() {
        assertThat(TransactionsV1Controller.BASE_PATH).isEqualTo(Constants.API_BASE_PATH + "/v1/transactions");
    }

    @Test
    public void whenList_thenReturnStatus200AndTransactionsList() throws ParseException {
        List<TransactionData> testData = getTestData();
        Date startDate = new Date();
        Date endDate = startDate;
        given(mockProviderService.getTransactions("1", 1L, startDate, endDate)).willReturn(testData);

        TransactionsV1Controller controller = new TransactionsV1Controller();
        controller.providerService = mockProviderService;

        ResponseEntity<TransactionsResponse> result = controller.list(
                new Token("1"),
                new TransactionsRequest(1L, startDate, endDate, "sessionSecret")
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().data).isEqualTo(testData);
    }

    private List<TransactionData> getTestData() throws ParseException {
        ArrayList<TransactionData> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date madeOn = dateFormat.parse("2020-01-01");
        result.add(new TransactionData(
                "t1",
                "a1",
                "100.0",
                "EUR",
                "Payment",
                madeOn,
                "posted",
                new ArrayList<>(),
                new HashMap<>()
        ));
        return result;
    }
}
