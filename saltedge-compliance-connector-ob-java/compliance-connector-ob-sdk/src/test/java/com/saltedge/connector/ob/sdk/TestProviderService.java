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
package com.saltedge.connector.ob.sdk;

import com.saltedge.connector.ob.sdk.provider.ProviderServiceAbs;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccount;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccountIdentifier;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAmount;
import com.saltedge.connector.ob.sdk.provider.dto.account.TransactionsPage;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TestProviderService implements ProviderServiceAbs {
    @Override
    public List<ObAccount> getAccountsOfUser(String userId) {
        return null;
    }

    @Override
    public TransactionsPage getTransactionsOfAccount(String userId, String accountId, @NotNull LocalDate fromDate, @NotNull LocalDate toDate, String fromId) {
        return null;
    }

    @Override
    public boolean confirmFunds(String userId, @NotNull ObAccountIdentifier debtorAccount, @NotNull ObAmount amount) {
        return false;
    }

    @Override
    public String initiatePayment(String userId, @NotNull ObPaymentInitiationData params) {
        return null;
    }
}
