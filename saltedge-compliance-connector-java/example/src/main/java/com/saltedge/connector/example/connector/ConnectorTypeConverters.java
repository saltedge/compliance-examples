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

import com.saltedge.connector.example.model.*;
import com.saltedge.connector.sdk.provider.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectorTypeConverters {
    public static InputField[] convertCodesToInputFields(String[] codes) {
        InputField[] result = new InputField[codes.length];
        for (int i = 0; i < codes.length; i++) {
            result[i] = new InputField(codes[i], codes[i]);
        }
        return result;
    }

    public static KycData convertUserToKycData(User user) {
        return (user == null) ? null : new KycData(user.address, user.dob, user.email, user.name, user.phone);
    }

    public static List<AccountData> convertAccountsToAccountData(List<Account> accounts) {
        return accounts.stream().map(ConnectorTypeConverters::convertAccountToAccountData).collect(Collectors.toList());
    }

    public static AccountData convertAccountToAccountData(Account account) {
        return new AccountData(
            account.id.toString(),
            account.availableAmount,
            account.balance,
            account.creditLimit,
            account.currencyCode,
            account.iban,
            account.name,
            account.nature,
            account.number,
            account.isPaymentAccount,
            account.sortCode,
            account.status,
            account.swiftCode,
            new HashMap<>()
        );
    }

    public static List<TransactionData> convertTransactionsToTransactionsData(List<Transaction> transactions) {
        return transactions.stream().map(ConnectorTypeConverters::convertTransactionToTransactionsData).collect(Collectors.toList());
    }

    public static TransactionData convertTransactionToTransactionsData(Transaction transaction) {
        return new TransactionData(
                transaction.id.toString(),
                transaction.account.id.toString(),
                String.valueOf(transaction.amount),
                transaction.currencyCode,
                transaction.description,
                transaction.madeOn,
                transaction.status,
                convertFeesToFeeData(transaction.fees),
                new HashMap<>()
        );
    }

    public static List<FeeData> convertFeesToFeeData(List<Fee> fees) {
        return fees.stream().map(fee -> new FeeData(fee.description, fee.amount, fee.currencyCode)).collect(Collectors.toList());
    }

    public static PaymentData convertPaymentToPaymentData(Payment payment) {
        if (payment == null) return null;
        return new PaymentData(
                payment.id.toString(),
                payment.description,
                payment.status.toString().toLowerCase(),
                payment.amount,
                payment.total,
                convertFeesToFeeData(payment.fees),
                payment.paymentAttributes,
                payment.extra
        );
    }
}
