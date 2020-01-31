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

import com.saltedge.connector.example.model.Account;
import com.saltedge.connector.example.model.Transaction;
import com.saltedge.connector.example.model.User;
import com.saltedge.connector.sdk.provider.models.*;

import java.util.ArrayList;
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

    public static List<AccountData> convertAccountsToAccountData(List<Account> accounts, User user) {
        return accounts.stream().map(account -> convertAccountToAccountData(account, user)).collect(Collectors.toList());
    }

    public static AccountData convertAccountToAccountData(Account account, User user) {
        ArrayList<BalanceData> balances = new ArrayList<>();
        balances.add(new BalanceData(String.format("%.2f", account.availableAmount), account.currencyCode, "closingAvailable"));//Closing balance of amount of money that is at the disposal of the account owner on the date specified.
        balances.add(new BalanceData(String.format("%.2f", account.balance), account.currencyCode, "openingAvailable"));//Opening balance of amount of money that is at the disposal of the account owner on the date specified.
        AccountData result = new AccountData(
                account.id.toString(),
                account.name,
                balances,
                account.nature,
                account.currencyCode
        );
        result.bban = account.number;
        result.iban = account.iban;
        result.msisdn = user.phone;
        result.product = account.nature;
        result.status = account.status;
        return result;
    }

    public static List<TransactionData> convertTransactionsToTransactionsData(List<Transaction> transactions) {
        return transactions.stream().map(ConnectorTypeConverters::convertTransactionToTransactionsData).collect(Collectors.toList());
    }

    public static TransactionData convertTransactionToTransactionsData(Transaction transaction) {
        TransactionData result = new TransactionData(
                transaction.id.toString(),
                String.format("%.2f", transaction.amount),
                transaction.currencyCode,
                transaction.status,
                transaction.madeOn
        );
        result.bookingDate = transaction.postDate;
        result.creditorDetails = createParticipantDetails(transaction.account);
        result.debtorDetails = new ParticipantDetails(new ParticipantDetails.Account());
        result.debtorDetails.account.iban = "GB29 NWBK 6016 1331 9268 19";
        result.debtorDetails.account.name = "Unknown payee";

        List<CurrencyExchange> exchanges = new ArrayList<>();
        exchanges.add(new CurrencyExchange("", "1.0", transaction.postDate, transaction.currencyCode, transaction.currencyCode, transaction.currencyCode));
        result.currencyExchange = exchanges;

        result.extra = new TransactionExtra();
        result.extra.ultimateCreditor = result.creditorDetails.account.name;
        result.extra.ultimateDebtor = result.debtorDetails.account.name;
        return result;
    }

    public static ParticipantDetails createParticipantDetails(Account account) {
        ParticipantDetails.Account participantAccount = new ParticipantDetails.Account();
        participantAccount.bban = account.number;
        participantAccount.currencyCode = account.currencyCode;
        participantAccount.iban = account.iban;
        participantAccount.maskedPan = maskPan(account.pan);
        participantAccount.msisdn = account.user.phone;
        participantAccount.pan = account.pan;
        participantAccount.name = account.name;
        return new ParticipantDetails(participantAccount);
    }

    private static String maskPan(String pan) {
        if (pan == null || pan.length() < 4) return pan;
        else return "**** **** **** " + pan.substring(pan.length() - 4);
    }
}
