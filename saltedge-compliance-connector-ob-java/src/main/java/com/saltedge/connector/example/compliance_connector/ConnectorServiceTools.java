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
package com.saltedge.connector.example.compliance_connector;

import com.saltedge.connector.example.model.AccountEntity;
import com.saltedge.connector.example.model.PaymentEntity;
import com.saltedge.connector.example.model.TransactionEntity;
import com.saltedge.connector.example.model.repository.AccountsRepository;
import com.saltedge.connector.example.model.repository.TransactionsRepository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

//public class ConnectorServiceTools {
//    /**
//     * update balances and create transactions
//     */
//    public static void createTransaction(AccountsRepository accountsRepository,
//                                         TransactionsRepository transactionsRepository,
//                                         PaymentEntity payment) {
//        AccountEntity account = accountsRepository.findById(payment.accountId).orElse(null);
//        if (account == null) return;
//
//        TransactionEntity transaction = new TransactionEntity();
//        transaction.amount = String.format("%.2f", -payment.total);
//        transaction.currencyCode = account.currencyCode;
//        transaction.description = payment.description;
//        transaction.madeOn = LocalDate.now();
//        transaction.status = "posted";
//        transaction.fees = payment.fees;
//        transaction.paymentExtra = payment.extra;
//        transaction.account = account;
//        transactionsRepository.save(transaction);
//    }
//
//    public static Double getAmountValue(String amountString) {
//        try {
//            return Double.valueOf(amountString);
//        } catch (NumberFormatException e) {
//            return null;
//        }
//
//    }
//
//    public static AccountEntity findDebtorAccount(AccountsRepository accountsRepository, String identifier) {
//        if (StringUtils.isEmpty(identifier)) return null;
//        return accountsRepository.findFirstByIbanOrBban(identifier, identifier);
//    }
//
//    public static boolean isExemptPayment(PaymentEntity payment) {
//        return payment.total <= 30;
//    }
//}
