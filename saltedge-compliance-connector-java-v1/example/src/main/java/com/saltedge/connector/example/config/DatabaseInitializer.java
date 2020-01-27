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
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class DatabaseInitializer {
    private static Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    CurrenciesRepository currenciesRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    AccountsRepository accountsRepository;
    @Autowired
    TransactionsRepository transactionsRepository;
    private User user;
    private Account account1;
    private Account account2;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedCurrencies();
        seedUsers();
        seedAccounts();
        seedTransactions();
    }

    private void seedCurrencies() {
        if (currenciesRepository.count() == 0) {
            currenciesRepository.save(new Currency("USD", "USD", 0.812f));
            currenciesRepository.save(new Currency("GBP", "GBP", 1.502f));
            currenciesRepository.save(new Currency("EUR", "EUR", 1.0f));

            log.info("Currencies Seeded");
        } else {
            log.info("Currencies Seeding Not Required");
        }
    }

    private void seedUsers() {
        if (usersRepository.count() == 0) {
            user = usersRepository.save(new User(
                    "John Doe",
                    "john.doe@example.org",
                    "Toronto, Ontario, Canada",
                    "01-02-2000",
                    "+1-555-5555",
                    "username",
                    "secret"
            ));

            log.info("Users Seeded");
        } else {
            log.info("Users Seeding Not Required");
        }
    }

    private void seedAccounts() {
        if (accountsRepository.count() == 0) {
            account1 = accountsRepository.save(new Account(
                    "My Payment Account 1",
                    "card",
                    "EUR",
                    "FK93RAND49838728129441",
                    "619656551",
                    "82-78-61",
                    "TBNFFR21PAR",
                    20000.0,
                    20000.0,
                    0.0,
                    true,
                    "active",
                    new HashMap<>(),
                    user
            ));
            account2 = accountsRepository.save(new Account(
                    "My Payment Account 2",
                    "account",
                    "EUR",
                    "FK93RAND49838728129442",
                    "619656552",
                    "82-78-62",
                    "TBNFFR22PAR",
                    10000.0,
                    10000.0,
                    10000.0,
                    true,
                    "active",
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
            log.info("Transactions Seeding Not Required");
        }
    }

    private void generateTransactions(Account account, LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) return;
        for (LocalDate date = fromDate; (date.isBefore(toDate) || date.isEqual(toDate)); date = date.plusDays(1)) {
            double amount = -(double) date.getDayOfMonth();
            transactionsRepository.save(new Transaction(
                    amount,
                    account.currencyCode,
                    "Payment " + amount + " " + account.currencyCode + "(Account:" + account.id + ")",
                    date.toDate(),
                    "posted",
                    new ArrayList<>(),
                    new HashMap<>(),
                    account
            ));
        }
    }
}
