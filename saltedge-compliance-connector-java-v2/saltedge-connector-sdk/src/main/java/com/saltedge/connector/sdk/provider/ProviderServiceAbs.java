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
package com.saltedge.connector.sdk.provider;

import com.saltedge.connector.sdk.api.models.*;
import com.saltedge.connector.sdk.models.CardTransactionsPage;
import com.saltedge.connector.sdk.models.TransactionsPage;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface for communication between Compliance Connector SDK and Provider/ASPSP application.
 * Service Provider application should implement `@Service` which `implements ProviderApi`
 */
public interface ProviderServiceAbs {
    /**
     * Provides Authorization Types registered in `Dashboard/Settings/Authorization types` for Customer.
     * (https://priora.saltedge.com/providers/settings#authorization_types)
     *
     * @return list of AuthorizationType objects
     * @see AuthorizationType
     */
    List<AuthorizationType> getAuthorizationTypes();

    /**
     * Provides current currencies exchange rates
     *
     * @return list of ExchangeRate objects
     * @see ExchangeRate
     */
    List<ExchangeRate> getExchangeRates();

    /**
     * Provides url of provider's authorization page
     * designated for authorization session of new Account Information Session
     *
     * @param sessionSecret create consent session secret.
     *                      should be returned on authentication success or fail.
     * @param userConsentIsRequired if true then user consent for Account Information (balances, transactions) is required
     *                              and should be returned on authentication success.
     * @see ConnectorSDKCallbackService#onAccountInformationAuthorizationSuccess
     * @see ConnectorSDKCallbackService#onAccountInformationAuthorizationFail
     * @return URL string
     */
    String getAccountInformationAuthorizationPageUrl(
            @NotEmpty String sessionSecret,
            boolean userConsentIsRequired
    );

    /**
     * Return accounts information of user.
     * Serves accounts endpoint (https://priora.saltedge.com/docs/aspsp/v2/connector_endpoints#accounts-get)
     *
     * @param userId User identifier on Provider side
     * @return list of Account objects
     * @see Account
     */
    List<Account> getAccountsOfUser(@NotEmpty String userId);

    /**
     * Provides transactions which belong to an account of user.
     * Serves transactions endpoint (https://priora.saltedge.com/docs/aspsp/v2/connector_endpoints#accounts-transactions)
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
    TransactionsPage getTransactionsOfAccount(
            @NotEmpty String userId,
            @NotEmpty String accountId,
            @NotNull LocalDate fromDate,
            @NotNull LocalDate toDate,
            String fromId
    );

    /**
     * Provides card accounts information of user.
     * Serves accounts endpoint (https://priora.saltedge.com/docs/aspsp/v2/connector_endpoints#card-accounts-get)
     *
     * @param userId User identifier on Provider side
     * @return list of CardAccount objects
     * @see CardAccount
     */
    List<CardAccount> getCardAccountsOfUser(@NotEmpty String userId);

    /**
     * Provides transactions which belong to a card account of user.
     * Serves transactions endpoint (https://priora.saltedge.com/docs/aspsp/v2/connector_endpoints#card-accounts-transactions)
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
    CardTransactionsPage getTransactionsOfCardAccount(
            @NotEmpty String userId,
            @NotEmpty String accountId,
            @NotNull LocalDate fromDate,
            @NotNull LocalDate toDate,
            String fromId
    );

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
    String createPayment(
            @NotEmpty String creditorIban,
            @NotEmpty String creditorName,
            @NotEmpty String debtorIban,
            @NotEmpty String amount,
            @NotEmpty String currency,
            String description,
            @NotNull Map<String, String> extraData
    );

    /**
     * Provides url of provider's authorization page
     * designated for authorization session of new Payment Initiation Session
     *
     * @param paymentId unique identifier of payment order for which is required authorization
     * @return URL string
     */
    String getPaymentAuthorizationPageUrl(@NotEmpty String paymentId);
}
