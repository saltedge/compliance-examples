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

import com.saltedge.connector.example.connector.config.PaymentTemplates;
import com.saltedge.connector.example.model.*;
import com.saltedge.connector.sdk.provider.models.PaymentTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConnectorServiceTools {
    public static String extractDescriptionFromPaymentAttributes(Map<String, String> attributes) {
        return attributes.get(PaymentTemplates.KEY_DESCRIPTION);
    }

    public static Double extractAmountFromPaymentAttributes(Map<String, String> attributes) {
        if (!attributes.containsKey(PaymentTemplates.KEY_AMOUNT)) return null;
        return Double.valueOf(attributes.get(PaymentTemplates.KEY_AMOUNT));
    }

    public static List<Fee> getFees(PaymentTemplate paymentTemplate) {
        ArrayList<Fee> result = new ArrayList<>();
        result.add(new Fee("payment fee", 0.1, "EUR"));
        return result;
    }

    public static Double getTotalAmount(Double amount, List<Fee> fees) {
        return amount + fees.stream().map(fee -> fee.amount).reduce(0.0, Double::sum);
    }

    public static Account findDebtorAccount(AccountsRepository accountsRepository,
                                            PaymentTemplate paymentTemplate,
                                            Map<String, String> paymentAttributes) {
        String accountIdentifier = null;
        switch (paymentTemplate.code) {
            case PaymentTemplates.TYPE_INTERNAL_TRANSFER:
                accountIdentifier = paymentAttributes.get("from_account");//TODO use template
                break;
            case PaymentTemplates.TYPE_SWIFT:
                accountIdentifier = paymentAttributes.get("from_account_number");//TODO use template
                break;
            case PaymentTemplates.TYPE_SEPA:
                accountIdentifier = paymentAttributes.get("from_iban");//TODO use template
                break;
        }
        if (accountIdentifier == null) return null;
        List<Account> accounts = accountsRepository.findByAccountIdentifier(accountIdentifier);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    public static boolean isExemptPayment(Payment payment) {
        return payment.total <= 30;
    }

    /**
     * update balances & create transactions
     */
    public static void createTransaction(AccountsRepository accountsRepository,
                                         TransactionsRepository transactionsRepository,
                                         Payment payment) {
        Account account = accountsRepository.findById(payment.accountId).orElse(null);
        if (account == null) return;

        Transaction transaction = new Transaction();
        transaction.amount = - payment.total;
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
