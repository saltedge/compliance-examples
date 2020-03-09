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
package com.saltedge.connector.example.connector;

import com.saltedge.connector.example.model.AccountEntity;
import com.saltedge.connector.example.model.PaymentEntity;
import com.saltedge.connector.example.model.TransactionEntity;
import com.saltedge.connector.example.model.repository.AccountsRepository;
import com.saltedge.connector.example.model.repository.TransactionsRepository;

public class ConnectorServiceTools {
    /**
     * update balances & create transactions
     */
    public static void createTransaction(AccountsRepository accountsRepository,
                                         TransactionsRepository transactionsRepository,
                                         PaymentEntity payment) {
        AccountEntity account = accountsRepository.findById(payment.accountId).orElse(null);
        if (account == null) return;

        TransactionEntity transaction = new TransactionEntity();
        transaction.amount = String.format("%.2f", -payment.total);
        transaction.currencyCode = account.currencyCode;
        transaction.description = payment.description;
        transaction.madeOn = payment.getCreatedAt();
        transaction.status = "posted";
        transaction.fees = payment.fees;
        transaction.extra = payment.extra;
        transaction.account = account;
        transactionsRepository.save(transaction);
    }
}
