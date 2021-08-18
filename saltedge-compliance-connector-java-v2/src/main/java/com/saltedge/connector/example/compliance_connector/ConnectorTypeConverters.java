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
package com.saltedge.connector.example.compliance_connector;

import com.saltedge.connector.example.model.*;
import com.saltedge.connector.sdk.api.models.*;

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

    public static List<Account> convertAccountsToAccountData(List<AccountEntity> accounts, UserEntity user) {
        return accounts.stream().map(account -> convertAccountToAccountData(account, user)).collect(Collectors.toList());
    }

    public static Account convertAccountToAccountData(AccountEntity account, UserEntity user) {
        ArrayList<AccountBalance> balances = new ArrayList<>();
        balances.add(new AccountBalance(account.availableAmount, account.currencyCode, "closingAvailable"));//Closing balance of amount of money that is at the disposal of the account owner on the date specified.
        balances.add(new AccountBalance(account.balance, account.currencyCode, "openingAvailable"));//Opening balance of amount of money that is at the disposal of the account owner on the date specified.
        Account result = new Account(
                account.id.toString(),
                account.name,
                balances,
                account.nature,
                account.currencyCode
        );
        result.setIban(account.iban);
        result.setBic(account.bic);
        result.setBban(account.bban);
        result.setSortCode(account.sortCode);
        result.setMsisdn(user.phone);
        result.setProduct(account.nature);
        result.setStatus(account.status);
        return result;
    }

    public static List<CardAccount> convertCardAccountsToAccountData(List<CardAccountEntity> accounts, UserEntity user) {
        return accounts.stream().map(account -> convertAccountToAccountData(account, user)).collect(Collectors.toList());
    }

    public static CardAccount convertAccountToAccountData(CardAccountEntity account, UserEntity user) {
        ArrayList<CardAccountBalance> balances = new ArrayList<>();
        balances.add(new CardAccountBalance(account.availableAmount, account.currencyCode, "closingAvailable"));//Closing balance of amount of money that is at the disposal of the account owner on the date specified.
        balances.add(new CardAccountBalance(account.balance, account.currencyCode, "openingAvailable"));//Opening balance of amount of money that is at the disposal of the account owner on the date specified.
        return new CardAccount(
                account.id.toString(),
                account.name,
                maskPan(account.pan),
                account.currencyCode,
                account.product,
                account.status,
                balances,
                new Amount(account.creditLimit, account.currencyCode),
                new CardAccountExtra()
        );
    }

    public static List<Transaction> convertTransactionsToTransactionsData(
            List<TransactionEntity> transactions
    ) {
        return transactions.stream()
                .map(ConnectorTypeConverters::convertTransactionToTransactionsData)
                .collect(Collectors.toList());
    }

    public static Transaction convertTransactionToTransactionsData(TransactionEntity transaction) {
        Transaction result = new Transaction();
        result.setId(transaction.id.toString());
        result.setAmount(transaction.amount);
        result.setCurrencyCode(transaction.currencyCode);
        result.setStatus(transaction.status);
        result.setValueDate(transaction.madeOn);
        result.setBookingDate(transaction.postDate);
        result.setDebtorDetails(createParticipantDetails(transaction.account));
        result.setCreditorDetails(new ParticipantDetails(
          transaction.toAccountName,
          ParticipantAccount.createWithIbanAndName(
            transaction.toIban,
            transaction.toAccountName
          )
        ));

        List<CurrencyExchange> exchanges = new ArrayList<>();
        exchanges.add(new CurrencyExchange("", "1.0", transaction.postDate, transaction.currencyCode, transaction.currencyCode, transaction.currencyCode));
        result.setCurrencyExchange(exchanges);

        result.setExtra(new TransactionExtra());
        result.getExtra().ultimateCreditor = result.getCreditorDetails().account.name;
        result.getExtra().ultimateDebtor = result.getDebtorDetails().account.name;

        TransactionRemittanceInformation information = new TransactionRemittanceInformation();
        information.structured = transaction.description;
        information.unstructured = transaction.description;
        result.setRemittanceInformation(information);
        return result;
    }

    public static List<CardTransaction> convertCardTransactionsToTransactionsData(List<CardTransactionEntity> transactions) {
        return transactions.stream().map(ConnectorTypeConverters::convertTransactionToTransactionsData).collect(Collectors.toList());
    }

    public static CardTransaction convertTransactionToTransactionsData(CardTransactionEntity transaction) {
        List<CurrencyExchange> exchanges = new ArrayList<>();
        exchanges.add(new CurrencyExchange("", "1.0", transaction.postDate, transaction.currencyCode, transaction.currencyCode, transaction.currencyCode));

        return new CardTransaction(
                transaction.id.toString(),
                transaction.amount,
                transaction.currencyCode,
                transaction.status,
                transaction.madeOn,
                "Payment details",
                transaction.postDate,
                new CardTransaction.AcceptorAddress(),
                "cardAcceptorId",
                exchanges,
                false,
                new Amount("0.0", transaction.currencyCode),
                "0.0",
                maskPan(transaction.pan),
                "merchantCategoryCode",
                new Amount(transaction.amount, transaction.currencyCode),
                "proprietaryBankTransactionCode",
                "terminalId"
        );
    }

    public static ParticipantDetails createParticipantDetails(AccountEntity account) {
        ParticipantAccount participantAccount = new ParticipantAccount();
        participantAccount.bban = account.bban;
        participantAccount.currencyCode = account.currencyCode;
        participantAccount.iban = account.iban;
        participantAccount.maskedPan = maskPan(account.pan);
        participantAccount.msisdn = account.user.phone;
        participantAccount.pan = account.pan;
        participantAccount.name = account.name;
        return new ParticipantDetails(account.name, participantAccount);
    }

    private static String maskPan(String pan) {
        if (pan == null || pan.length() < 4) return pan;
        else return "**** **** **** " + pan.substring(pan.length() - 4);
    }
}
