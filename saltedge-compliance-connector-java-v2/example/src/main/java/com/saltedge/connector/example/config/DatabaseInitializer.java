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
package com.saltedge.connector.example.config;

import com.saltedge.connector.example.model.*;
import com.saltedge.connector.example.model.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class DatabaseInitializer {
    private static Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    CurrenciesRepository currenciesRepository;
    @Autowired
    public UsersRepository usersRepository;
    @Autowired
    public AccountsRepository accountsRepository;
    @Autowired
    public CardAccountsRepository cardAccountsRepository;
    @Autowired
    public TransactionsRepository transactionsRepository;
    @Autowired
    public CardTransactionsRepository cardTransactionsRepository;
    private UserEntity user;
    private AccountEntity account1;
    private AccountEntity account2;
    private AccountEntity account3;
    private CardAccountEntity cardAccount1;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seed();
    }

    public void seed() {
        seedCurrencies();
        user = seedUsers();
        seedAccounts();
        seedTransactions();
        seedCardAccounts();
        seedCardTransactions();
    }

    private void seedCurrencies() {
        if (currenciesRepository.count() == 0) {
            currenciesRepository.save(new CurrencyEntity("USD", "USD", 0.812f, "EUR"));
            currenciesRepository.save(new CurrencyEntity("GBP", "GBP", 1.502f, "EUR"));
            currenciesRepository.save(new CurrencyEntity("EUR", "EUR", 1.0f, "EUR"));

            log.info("Currencies Seeded");
        } else {
            log.info("Currencies Seeding Not Required");
        }
    }

    private UserEntity seedUsers() {
        if (usersRepository.count() == 0) {
            log.info("Users Seeded");
            return usersRepository.save(new UserEntity(
                    "John Doe",
                    "john.doe@example.org",
                    "Toronto, Ontario, Canada",
                    "01-02-2000",
                    "+1-555-5555",
                    "username",
                    "secret"
            ));
        } else {
            log.info("Users Seeding Not Required");
            return usersRepository.findAll().get(0);

        }
    }

    private void seedAccounts() {
        if (accountsRepository.count() == 0) {
            account1 = accountsRepository.save(new AccountEntity(
                    "My Payment Account 1",
                    "SLRY",//Accounts used for salary payments.
                    "EUR",
                    "GB46BUKB20041538290008",
                    "BARCGB22XXX",
                    "",
                    "",
                    "BUKB20041538290008",
                    "20000.00",
                    "20000.00",
                    "0.00",
                    true,
                    "enabled",
                    null,
                    new HashMap<>(),
                    user
            ));
            account2 = accountsRepository.save(new AccountEntity(
                    "My Payment Account 2",
                    "CACC",//Account used to post debits and credits when no specific account has been nominated.
                    "EUR",
                    "FK93RAND49838728129442",
                    "BARCGB22XXX",
                    "",
                    "",
                    "TBNFFR22PAR",
                    "10000.00",
                    "10000.00",
                    "10000.00",
                    true,
                    "enabled",
                    null,
                    new HashMap<>(),
                    user
            ));
            account3 = accountsRepository.save(new AccountEntity(
              "My FPS Payment Account",
              "CACC",
              "EUR",
              "",
              "",
              "MIDL40051512345674",
              "400515",
              "TBNFFR22PAR",
              "10000.00",
              "10000.00",
              "10000.00",
              true,
              "enabled",
              null,
              new HashMap<>(),
              user
            ));

            log.info("Accounts Seeded");
        } else {
            log.info("Accounts Seeding Not Required");
        }
    }

    private void seedTransactions() {
        if (transactionsRepository.count() == 0) {
            generateTransactions(account1, LocalDate.now().minusMonths(3), LocalDate.now());

            log.info("Transactions Seeded");
        } else {
            log.info("Transactions Seed Not Required");
        }
    }

    private void generateTransactions(AccountEntity account, LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) return;
        for (LocalDate date = fromDate; (date.isBefore(toDate) || date.isEqual(toDate)); date = date.plusDays(1)) {
            double amount = -(double) date.getDayOfMonth();
            TransactionEntity transaction = new TransactionEntity(
              String.format("%.2f", amount),
              account.currencyCode,
              "Payment " + amount + " " + account.currencyCode + "(Account:" + account.id + ")",
              date,
              "booked",
              new ArrayList<>(),
              new HashMap<>(),
              account
            );
            transaction.toIban = "GB29NWBK60161331926819";
            transaction.toAccountName = "Test Creditor Account";
            transaction.toCurrencyCode = "EUR";
            transaction.postDate = date;
            transactionsRepository.save(transaction);
        }
    }

    private void seedCardAccounts() {
        if (cardAccountsRepository.count() == 0) {
            cardAccount1 = cardAccountsRepository.save(new CardAccountEntity(
                    "My Card",
                    "1111 2222 3333 4444",
                    "EUR",
                    "card",
                    "enabled",
                    "20000.00",
                    "20000.00",
                    "0.00",
                    new HashMap<>(),
                    user
            ));

            log.info("Card Accounts Seeded");
        } else {
            log.info("Card Accounts Seed Not Required");
        }
    }

    private void seedCardTransactions() {
        if (cardTransactionsRepository.count() == 0) {
            generateTransactions(cardAccount1, LocalDate.now().minusMonths(3), LocalDate.now());

            log.info("Card Transactions Seeded");
        } else {
            log.info("Card Transactions Seeding Not Required");
        }
    }

    private void generateTransactions(CardAccountEntity account, LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) return;
        for (LocalDate date = fromDate; (date.isBefore(toDate) || date.isEqual(toDate)); date = date.plusDays(1)) {
            double amount = -(double) date.getDayOfMonth();
            cardTransactionsRepository.save(new CardTransactionEntity(
                    String.format("%.2f", amount),
                    account.currencyCode,
                    "Payment " + amount + " " + account.currencyCode + "(Account:" + account.id + ")",
                    date,
                    "booked",
                    new ArrayList<>(),
                    new HashMap<>(),
                    account
            ));
        }
    }
}
