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
import com.saltedge.connector.example.model.TransactionEntity;
import com.saltedge.connector.example.model.UserEntity;
import com.saltedge.connector.ob.sdk.provider.dto.account.*;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data adapter between DB format and required by Compliance SDK.
 * Some data are faked.
 */
public class ConnectorDataConverters {

    public static List<ObAccount> convertAccountsToAccountData(List<AccountEntity> accounts, UserEntity user) {
        return accounts.stream().map(account -> convertAccountToAccountData(account, user)).collect(Collectors.toList());
    }

    public static ObAccount convertAccountToAccountData(AccountEntity accountEntry, UserEntity user) {
        ArrayList<ObBalance> balances = new ArrayList<>();
        balances.add(new ObBalance(new ObAmount(accountEntry.availableAmount, accountEntry.currencyCode), "debit", "InterimAvailable", accountEntry.getUpdatedAt().toInstant()));
        balances.add(new ObBalance(new ObAmount(accountEntry.balance, accountEntry.currencyCode), "debit", "openingAvailable", accountEntry.getUpdatedAt().toInstant()));

        ArrayList<ObAccountIdentifier> identifiers = new ArrayList<>();
        identifiers.add(new ObAccountIdentifier(
            "UK.OBIE.SortCodeAccountNumber",
            accountEntry.accountNumber,
            accountEntry.accountNumber,
            accountEntry.sortCode
        ));

        return new ObAccount(
            accountEntry.id.toString(),
            accountEntry.currencyCode,
            accountEntry.accountType,
            accountEntry.accountSubType,
            balances,
            accountEntry.status,
            accountEntry.getUpdatedAt().toInstant(),
            accountEntry.name + " / " + accountEntry.accountType + " / " + accountEntry.currencyCode,
            accountEntry.name,
            accountEntry.getCreatedAt().toInstant(),
            accountEntry.getUpdatedAt().toInstant(),
            "processing",
            identifiers,
            new ObAccountIdentifier("UK.OBIE.SortCodeAccountNumber", accountEntry.accountNumber)
        );
    }

    public static List<ObTransaction> convertTransactionsToTransactionsData(List<TransactionEntity> transactions) {
        return transactions.stream()
                .map(ConnectorDataConverters::convertTransactionToTransactionsData)
                .collect(Collectors.toList());
    }

    public static ObTransaction convertTransactionToTransactionsData(TransactionEntity transaction) {
        ObTransaction result = new ObTransaction();
        result.id = transaction.id.toString();
        result.creditDebitIndicator = transaction.amount.startsWith("-") ? "credit" : "debit";

        result.status = transaction.status;
        result.bookingDateTime = transaction.madeOn.atStartOfDay().toInstant(ZoneOffset.UTC);

        result.amount = new ObAmount(transaction.amount, transaction.currencyCode);
        result.transactionInformation = transaction.description;
        result.creditorAccount = new ObAccountIdentifier(
          "UK.OBIE.SortCodeAccountNumber",
          transaction.toAccountNumber
        );
        return result;
    }
}
