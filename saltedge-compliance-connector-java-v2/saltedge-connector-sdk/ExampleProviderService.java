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

import com.saltedge.connector.example.compliance_connector.collector.AccountsCollector;
import com.saltedge.connector.example.compliance_connector.config.AuthorizationTypes;
import com.saltedge.connector.example.controllers.consent.UserOAuthAuthController;
import com.saltedge.connector.example.model.*;
import com.saltedge.connector.example.model.repository.*;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.*;
import com.saltedge.connector.sdk.api.models.err.BadRequest;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.models.CardTransactionsPage;
import com.saltedge.connector.sdk.models.TransactionsPage;
import com.saltedge.connector.sdk.provider.ProviderServiceAbs;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;

/**
 * This Service is an example (Proof of concept) of Provider Service,
 * designated for communication between Compliance Connector SDK and Provider/ASPSP application.
 * Replace all business logic with own (non-fake) code.
 *
 * @see ProviderServiceAbs
 */
@Service
@Validated
public class ExampleProviderService implements ProviderServiceAbs {
    public static int PAGE_SIZE = 30;
    private static Logger log = LoggerFactory.getLogger(ProviderService.class);
    @Autowired
    private Environment env;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private CardTransactionsRepository cardTransactionsRepository;
    @Autowired
    private CardAccountsRepository cardAccountsRepository;
    @Autowired
    private PaymentsRepository paymentsRepository;

    /**
     * Return Authorization Types registered in `Dashboard/Settings/Authorization types` for Customer.
     * (https://priora.saltedge.com/providers/settings#authorization_types)
     *
     * @return list of AuthorizationType objects
     * @see AuthorizationType
     */
    @Override
    public List<AuthorizationType> getAuthorizationTypes() {
        ArrayList<AuthorizationType> fakeTypes = new ArrayList<>();
        fakeTypes.add(
                new AuthorizationType(
                        AuthMode.EMBEDDED,
                        "login_password",
                        "Login & Password",
                        ConnectorTypeConverters.convertCodesToInputFields(new String[]{"login", "password"}),
                        null
                )
        );
        return fakeTypes;
    }

    /**
     * Provides current currencies exchange rates of Bank.
     * Used by SDK on Confirm Funds flow.
     * (https://priora.saltedge.com/docs/aspsp/v1/connector_endpoints#connectorendpoints-payments-checkfunds)
     *
     * @return list of ExchangeRate objects
     * @see ExchangeRate
     */
    @Override
    public List<ExchangeRate> getExchangeRates() {
        ArrayList<ExchangeRate> fakeRates = new ArrayList<>();
        fakeRates.add(new ExchangeRate("EUR", 1.0f));
        fakeRates.add(new ExchangeRate("USD", 0.90f));
        fakeRates.add(new ExchangeRate("GBP", 1.502f));
        return fakeRates;
    }

    /**
     * Provides url of provider's authorization page
     * designated for authorization of new Consent to access Account Information data
     *
     * @param sessionSecret create consent session secret.
     *                      should be returned on authentication success or fail.
     * @param userConsentIsRequired if true then user consent for Account Information (balances, transactions) is required
     *                              and should be returned on authentication success.
     * @see ConnectorSDKCallbackService#onAccountInformationAuthorizationSuccess
     * @see ConnectorSDKCallbackService#onAccountInformationAuthorizationFail
     * @return URL string
     */
    @Override
    public String getAccountInformationAuthorizationPageUrl(
            String sessionSecret,
            boolean userConsentIsRequired
    ) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getAutorizationPageUrl());
            Arrays.stream(params).forEach(item -> builder.queryParam("session_secret", sessionSecret));
            return builder.build().toUriString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return accounts information of user.
     * Serves accounts endpoint
     * (https://priora.saltedge.com/docs/aspsp/v2/connector_endpoints#connectorendpoints-accounts-get)
     *
     * @param userId User identifier on Provider side
     * @return list of Account objects
     * @see Account
     */
    @Override
    public List<Account> getAccountsOfUser(@NotEmpty String userId) {
        //Validate user and collect data
        ArrayList<AccountBalance> fakeBalances = new ArrayList<>();
        fakeBalances.add(new AccountBalance(String.format("%.2f", 1000.0), "EUR", "closingAvailable"));
        fakeBalances.add(new AccountBalance(String.format("%.2f", 1000.0), "EUR", "openingAvailable"));
        ArrayList<Account> fakeAccounts = new ArrayList<>();
        fakeAccounts.add(new Account(
                "1",
                "account",
                fakeBalances,
                "active",
                "EUR"
        ));
        return fakeAccounts;
    }

    /**
     * Provides transactions which belong to an account of user.
     * Serves transactions endpoint
     * (https://priora.saltedge.com/docs/aspsp/v2/connector_endpoints#accounts-transactions)
     *
     * @param userId User identifier on Provider side
     * @param accountId Account identifier on Provider side
     * @param fromDate Specifies the starting date, from which transactions should be fetched.
     *                 This value can be set to 90 days ago by default.
     * @param toDate Specifies the ending date, to which transactions should be fetched.
     *               This value will always be the today’s date.
     * @param fromId Specifies the ID of page for Pageable request.
     * @return page object with list of Transaction objects and next page id
     * @see Transaction
     * @see TransactionsPage
     */
    @Override
    public TransactionsPage getTransactionsOfAccount(
            @NotEmpty String userId,
            @NotEmpty String accountId,
            @NotNull LocalDate fromDate,
            @NotNull LocalDate toDate,
            String fromId
    ) {
        //Validate user and collect data
        ArrayList<Transaction> fakeTransactions = new ArrayList<>();
        fakeTransactions.add(new Transaction(
                "t1",
                "100.00",
                "EUR",
                "booked",
                LocalDate.parse("2020-01-01")
        ));
        return new TransactionsPage(fakeTransactions, null)
    }

    /**
     * Provides card accounts information of user.
     * Serves accounts endpoint
     * (https://priora.saltedge.com/docs/aspsp/v2/connector_endpoints#card-accounts-get)
     *
     * @param userId User identifier on Provider side
     * @return list of CardAccount objects
     * @see CardAccount
     */
    @Override
    public List<CardAccount> getCardAccountsOfUser(@NotEmpty String userId) {
        //Validate user and collect data
        ArrayList<CardAccountBalance> fakeBalances = new ArrayList<>();
        fakeBalances.add(new CardAccountBalance(String.format("%.2f", 1000.0), "EUR", "closingAvailable"));
        fakeBalances.add(new CardAccountBalance(String.format("%.2f", 1000.0), "EUR", "openingAvailable"));
        ArrayList<CardAccount> fakeAccounts = new ArrayList<>();
        fakeAccounts.add(new CardAccount(
                "1",
                "name",
                "**** **** **** 1111",
                "EUR",
                "product",
                "active",
                fakeBalances,
                new Amount("0.0", "EUR"),
                new CardAccountExtra()
        ));
        return fakeAccounts;
    }

    /**
     * Provides transactions which belong to a card account of user.
     * Serves transactions endpoint
     * (https://priora.saltedge.com/docs/aspsp/v2/connector_endpoints#card-accounts-transactions)
     *
     * @param userId User identifier on Provider side
     * @param accountId Account identifier on Provider side
     * @param fromDate Specifies the starting date, from which transactions should be fetched.
     *                 This value can be set to 90 days ago by default.
     * @param toDate Specifies the ending date, to which transactions should be fetched.
     *               This value will always be the today’s date.
     * @param fromId Specifies the ID of page for Pageable request.
     * @return page object with list of CardTransaction objects and next page id
     * @see CardTransaction
     * @see CardTransactionsPage
     */
    @Override
    public CardTransactionsPage getTransactionsOfCardAccount(
            @NotEmpty String userId,
            @NotEmpty String accountId,
            @NotNull LocalDate fromDate,
            @NotNull LocalDate toDate,
            String fromId
    ) {
        //Validate user and collect data
        ArrayList<CardTransaction> fakeTransactions = new ArrayList<>();
        fakeTransactions.add(new CardTransaction(
                "t1",
                "100.00",
                "EUR",
                "booked",
                LocalDate.parse("2020-01-01"),
                "details",
                null, null, null, null, null, null, null, null, null, null, null, null
        ));
        return new CardTransactionsPage(fakeTransactions, null);
    }

    /**
     * Initiate a payment order.
     * Serves payment endpoint (https://priora.saltedge.com/docs/aspsp/v2/connector_endpoints#payments-create)
     *
     * @param creditorIban of payment order
     * @param creditorName of payment order
     * @param debtorIban of payment order
     * @param amount of payment order
     * @param currency of payment order
     * @param description of payment order
     * @param extraData hash object
     * @return unique identifier of payment or null if payment is not initiated
     */
    @Override
    public String createPayment(
            @NotEmpty String creditorIban,
            @NotEmpty String creditorName,
            @NotEmpty String debtorIban,
            @NotEmpty String amount,
            @NotEmpty String currency,
            String description,
            @NotNull Map<String, String> extraData
    ) {
        String fakePaymentId = "1"
        return fakePaymentId;
    }

    /**
     * Provides url of provider's authorization page
     * designated for authorization session of new Payment Initiation Session
     *
     * @param paymentId unique identifier of payment order for which is required authorization
     * @return URL string
     */
    @Override
    public String getPaymentAuthorizationPageUrl(@NotEmpty String paymentId) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getAutorizationPageUrl());
            Arrays.stream(params).forEach(item -> builder.queryParam("payment_id", paymentId));
            return builder.build().toUriString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Prepare Autorization page url.
     * Host url can be stored as environment parameter
     * Path of Autorization page can be stored in Controller.
     * Replace fake code.
     *
     * @return Autorization page url string
     */
    private String getAutorizationPageUrl() {
        String fakeAutorizationUrl = "https://my_host.org/authorization";
        return fakeAutorizationUrl
    }
}
