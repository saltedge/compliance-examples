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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface for communication between Compliance Connector SDK and Provider/ASPSP application.
 * Service Provider application should implement `@Service` which `implements ProviderServiceAbs`
 */
public interface ProviderServiceAbs {
    /**
     * Provides Authorization Types registered in `Dashboard/Settings/Authorization types` for Customer.
     * (<a href="https://priora.saltedge.com/providers/settings#authorization_types">...</a>)
     *
     * @return List of AuthorizationType objects
     * @see AuthorizationType
     */
    List<AuthorizationType> getAuthorizationTypes();

    /**
     * Provides current currencies exchange rates.
     *
     * @return List of ExchangeRate objects
     * @see ExchangeRate
     */
    List<ExchangeRate> getExchangeRates();

    /**
     * Provides url of provider's authorization page designated for authorization of new Consent to access Account Information data
     *
     * @param sessionSecret Secret of create consent session. Should be returned on authentication success or fail.
     * @param userConsentIsRequired Flag that indicates if user consent for Account Information (balances, transactions) is required and should be returned on authentication success.
     * @param psuIpAddress Ip Address of PSU. Optional.
     * @return URL string
     * @see ConnectorSDKCallbackService#onAccountInformationAuthorizationSuccess
     * @see ConnectorSDKCallbackService#onAccountInformationAuthorizationFail
     */
    String getAccountInformationAuthorizationPageUrl(
            @NotEmpty String sessionSecret,
            boolean userConsentIsRequired,
            String psuIpAddress
    );

    /**
     * Return accounts information of user.
     * Serves accounts endpoint (<a href="https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-accounts-get">...</a>)
     *
     * @param userId User identifier on Provider side
     * @return List of Account objects
     * @see Account
     */
    List<Account> getAccountsOfUser(@NotEmpty String userId);

    /**
     * In case the connector uses a different database from Core Banking, this endpoint enables the process of refreshing accounts and transactions on connector side before sending them to Salt Edge PSD2 Compliance Solution.
     *
     * @param providerCode Human readable Provider identifier.
     * @param sessionSecret Session identifier in Salt Edge PSD2 Compliance.
     */
    void refresh(String providerCode, String sessionSecret);

    /**
     * Provides transactions which belong to an account of user.
     * Serves transactions endpoint (<a href="https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-accounts-transactions">...</a>)
     *
     * @param userId User identifier on Provider side
     * @param accountId Account identifier on Provider side
     * @param fromDate Starting date, from which transactions should be fetched. Value can be set to 90 days ago by default.
     * @param toDate Ending date, to which transactions should be fetched. Value will always be the today’s date.
     * @param fromId ID of page for Pageable request.
     * @return Page object with list of Transaction objects and next page id
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
     * Serves card accounts endpoint (<a href="https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-card_accounts-get">...</a>)
     *
     * @param userId User identifier on Provider side
     * @return List of CardAccount objects
     * @see CardAccount
     */
    List<CardAccount> getCardAccountsOfUser(@NotEmpty String userId);

    /**
     * Provides transactions which belong to a card account of user.
     * Serves card transactions endpoint (<a href="https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-card_accounts-transactions">...</a>)
     *
     * @param userId User identifier on Provider side
     * @param accountId An account identifier on Provider side
     * @param fromDate Starting date, from which transactions should be fetched. Value can be set to 90 days ago by default.
     * @param toDate Ending date, to which transactions should be fetched. Value will always be the today’s date.
     * @param fromId ID of page for Pageable request.
     * @return Page object with list of CardTransaction objects and next page id
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
     * Serves payment endpoint (<a href="https://priora.saltedge.com/docs/aspsp/v2/pis#pis-connector_endpoints-payments">...</a>)
     *
     * @param paymentProduct The addressed payment product.
     *                       Allowed values: sepa-credit-transfers, instant-sepa-credit-transfers, target-2-payments, faster-payment-service, cross-border-credit-transfers, credit-transfers, combined-payment, internal-transfer, automated-clearing-house-service, claim-payments, outbound-sepa-transfer, chaps-transfer, instant-credit-transfers, p2p-card-transfer, fedwire-credit-transfers, masav, zahav, fp, mia-instant-credit-transfers.
     * @param creditorIban International Bank Account Number of creditor
     * @param creditorBic Bank Identifier Code of creditor
     * @param creditorName Name of creditor
     * @param creditorAddress Address of creditor
     * @param creditorAgentName Creditor bank name
     * @param debtorIban International Bank Account Number of debtor. Parameter is optional, depends on payment product.
     * @param debtorBic Bank Identifier Code of debtor. Parameter is optional, depends on payment product.
     * @param amount Amount of payment order
     * @param currency Currency code of payment order
     * @param description Description of payment order
     * @param extraData Extra data of payment order. Attention: should be saved in payment model.
     * @param psuIpAddress IP address of PSU.
     * @param tppRedirectUrl The URL that the PSU will be redirected to after he finishes the authentication process on provider’s side.
     *
     * @return URL string of provider's authorization page designated for authorization of new Payment Initiation Session. If payment product not supported return null.
     */
    String createPayment(
            @NotEmpty String paymentProduct,
            @NotEmpty String creditorIban,
            String creditorBic,
            @NotEmpty String creditorName,
            ParticipantAddress creditorAddress,
            String creditorAgentName,
            String debtorIban,
            String debtorBic,
            @NotEmpty String amount,
            @NotEmpty String currency,
            String description,
            @NotNull String extraData,
            @NotEmpty String psuIpAddress,
            @NotEmpty String tppRedirectUrl
    );

    /**
     * Initiate a FPS (Faster Payment Service) payment order. Designated for providers under UK TPR.
     * Serves payment endpoint (<a href="https://priora.saltedge.com/docs/aspsp/v2/pis#pis-connector_endpoints-payments">...</a>)
     *
     * @param paymentProduct The addressed payment product. Allowed values: faster-payment-service
     * @param creditorBban Basic Bank Account Number of creditor
     * @param creditorSortCode Number code (which is used by British and Irish banks) of creditor.
     * @param creditorName Name of creditor
     * @param creditorAddress Address of creditor
     * @param creditorAgentName Creditor bank name
     * @param debtorBban Basic Bank Account Number of debtor
     * @param debtorSortCode Number code (which is used by British and Irish banks) of debtor.
     * @param amount Amount of payment order
     * @param currency Currency code of payment order
     * @param description Description of payment order
     * @param extraData Extra data of payment order. Attention: should be saved in payment model.
     * @param psuIpAddress IP address of PSU.
     * @param tppRedirectUrl The URL that the PSU will be redirected to after he finishes the authentication process on provider’s side.
     *
     * @return URL string of provider's authorization page designated for authorization of new Payment Initiation Session. If FPS payment not supported return null.
     */
    String createFPSPayment(
            @NotEmpty String paymentProduct,
            @NotEmpty String creditorBban,
            @NotEmpty String creditorSortCode,
            @NotEmpty String creditorName,
            ParticipantAddress creditorAddress,
            String creditorAgentName,
            String debtorBban,
            String debtorSortCode,
            @NotEmpty String amount,
            @NotEmpty String currency,
            String description,
            @NotNull String extraData,
            @NotEmpty String psuIpAddress,
            @NotEmpty String tppRedirectUrl
    );

    /**
     * Provides url of provider's authorization page designated for authorization of new Consent to access FundsConfirmation data
     *
     * @param sessionSecret Secret of create consent session. Should be returned on authentication success or fail.
     * @return URL string
     * @see ConnectorSDKCallbackService#onAccountInformationAuthorizationSuccess
     * @see ConnectorSDKCallbackService#onAccountInformationAuthorizationFail
     */
    String getFundsConfirmationAuthorizationPageUrl(@NotEmpty String sessionSecret);
}
