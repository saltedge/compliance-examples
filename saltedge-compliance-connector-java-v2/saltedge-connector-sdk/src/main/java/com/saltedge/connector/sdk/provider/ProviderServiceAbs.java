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

import com.saltedge.connector.sdk.provider.models.*;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Interface for communication between Compliance Connector and Service Provider application.
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
     * Provides one of Authorization Types registered in `Dashboard/Settings/Authorization types` for Customer.
     * (https://priora.saltedge.com/providers/settings#authorization_types)
     * Authorization Type is selected by type code (e.g. `oauth`)
     *
     * @param authTypeCode of Authorization Type (e.g. `oauth`)
     * @return AuthorizationType registered AuthorizationType
     * @see AuthorizationType
     */
    AuthorizationType getAuthorizationTypeByCode(String authTypeCode);

    /**
     * Provides url of provider's authorization page designated for oAuth authorization
     *
     * @param sessionSecret consent session secret
     * @return URL string of Authorization page
     */
    String getAccountInformationAuthorizationPageUrl(@NotEmpty String sessionSecret);

    /**
     * Provides accounts information of user
     *
     * @param userId User identifier on Provider side
     * @return list of AccountData objects
     * @see Account
     */
    List<Account> getAccountsOfUser(@NotEmpty String userId);

    /**
     * Provides transactions which belong to an account of user
     *
     * @param userId User identifier on Provider side
     * @param accountId Account identifier on Provider side
     * @param fromDate Specifies the starting date, from which transactions should be fetched.
     *                 This value can be set to 90 days ago by default.
     * @param toDate Specifies the ending date, to which transactions should be fetched.
     *               This value will always be the today’s date.
     * @return list of TransactionData objects
     * @see Transaction
     */
    List<Transaction> getTransactionsOfAccount(
            @NotEmpty String userId,
            @NotEmpty String accountId,
            Instant fromDate,
            Instant toDate
    );

    /**
     * Provides card accounts information of user
     *
     * @param userId User identifier on Provider side
     * @return list of CardAccount objects
     * @see CardAccount
     */
    List<CardAccount> getCardAccountsOfUser(@NotEmpty String userId);

    /**
     * Provides transactions which belong to a card account of user
     *
     * @param userId User identifier on Provider side
     * @param accountId Account identifier on Provider side
     * @param fromDate Specifies the starting date, from which transactions should be fetched.
     *                 This value can be set to 90 days ago by default.
     * @param toDate Specifies the ending date, to which transactions should be fetched.
     *               This value will always be the today’s date.
     * @return list of CardTransaction objects
     * @see CardTransaction
     */
    List<CardTransaction> getTransactionsOfCardAccount(
            @NotEmpty String userId,
            @NotEmpty String accountId,
            Instant fromDate,
            Instant toDate
    );

    /**
     * Create a payment order
     *
     * @param creditorIban of payment order
     * @param creditorName of payment order
     * @param debtorIban of payment order
     * @param amount of payment order
     * @param currency of payment order
     * @param description of payment order
     * @param extraData hash object
     * @return unique payment id or null if payment not created
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
     * Provides url of provider's authorization page designated for oAuth authorization and payment confirmation
     *
     * @param paymentId unique of payment order
     * @return URL string of Authorization page
     */
    String getPaymentAuthorizationPageUrl(@NotEmpty String paymentId);
}
