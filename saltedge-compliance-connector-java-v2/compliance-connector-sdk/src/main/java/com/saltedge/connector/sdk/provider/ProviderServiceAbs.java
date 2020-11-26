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

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface for communication between Compliance Connector SDK and Provider/ASPSP application.
 * Service Provider application should implement `@Service` which `implements ProviderServiceAbs`
 */
public interface ProviderServiceAbs {
  /**
   * Provides Authorization Types registered in `Dashboard/Settings/Authorization types` for Customer.
   * (https://priora.saltedge.com/providers/settings#authorization_types)
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
   * @return URL string
   * @see ConnectorSDKCallbackService#onAccountInformationAuthorizationSuccess
   * @see ConnectorSDKCallbackService#onAccountInformationAuthorizationFail
   */
  String getAccountInformationAuthorizationPageUrl(
    @NotEmpty String sessionSecret,
    boolean userConsentIsRequired
  );

  /**
   * Return accounts information of user.
   * Serves accounts endpoint (https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-accounts-get)
   *
   * @param userId User identifier on Provider side
   * @return List of Account objects
   * @see Account
   */
  List<Account> getAccountsOfUser(@NotEmpty String userId);

  /**
   * Provides transactions which belong to an account of user.
   * Serves transactions endpoint (https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-accounts-transactions)
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
   * Serves accounts endpoint (https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-card_accounts-get)
   *
   * @param userId User identifier on Provider side
   * @return List of CardAccount objects
   * @see CardAccount
   */
  List<CardAccount> getCardAccountsOfUser(@NotEmpty String userId);

  /**
   * Provides transactions which belong to a card account of user.
   * Serves transactions endpoint (https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-card_accounts-transactions)
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
   * Initiate a SEPA payment order.
   * Serves payment endpoint (https://priora.saltedge.com/docs/aspsp/v2/pis#pis-connector_endpoints-payments)
   * If SEPA payment not supported return null.
   *
   * @param paymentProduct The addressed payment product. Allowed values: sepa-credit-transfers, instant-sepa-credit-transfers, target-2-payments, internal-transfer
   * @param creditorIban International Bank Account Number of creditor
   * @param creditorBic Bank Identifier Code of creditor
   * @param creditorName Name of creditor
   * @param debtorIban International Bank Account Number of debtor
   * @param debtorBic Bank Identifier Code of debtor
   * @param amount Amount of payment order
   * @param currency Currency code of payment order
   * @param description Description of payment order
   * @param extraData Extra data of payment order
   * @return Unique identifier of payment or null if payment is not initiated
   */
  String createPayment(
    @NotEmpty String paymentProduct,
    @NotEmpty String creditorIban,
    String creditorBic,
    @NotEmpty String creditorName,
    ParticipantAddress creditorAddress,
    @NotEmpty String debtorIban,
    String debtorBic,
    @NotEmpty String amount,
    @NotEmpty String currency,
    String description,
    @NotNull Map<String, String> extraData
  );


  /**
   * Initiate a FPS (Faster Payment Service) payment order.
   * Serves payment endpoint (https://priora.saltedge.com/docs/aspsp/v2/pis#pis-connector_endpoints-payments)
   * If FPS payment not supported return null.
   *
   * @param paymentProduct The addressed payment product. Allowed values: faster-payment-service
   * @param creditorBban Basic Bank Account Number of creditor
   * @param creditorSortCode Number code (which is used by British and Irish banks) of creditor.
   * @param creditorName Name of creditor
   * @param debtorBban Basic Bank Account Number of debtor
   * @param debtorSortCode Number code (which is used by British and Irish banks) of debtor.
   * @param amount Amount of payment order
   * @param currency Currency code of payment order
   * @param description Description of payment order
   * @param extraData Extra data of payment order
   * @return Unique identifier of payment or null if payment is not initiated
   */
  String createFPSPayment(
    @NotEmpty String paymentProduct,
    @NotEmpty String creditorBban,
    @NotEmpty String creditorSortCode,
    @NotEmpty String creditorName,
    ParticipantAddress creditorAddress,
    @NotEmpty String debtorBban,
    @NotEmpty String debtorSortCode,
    @NotEmpty String amount,
    @NotEmpty String currency,
    String description,
    @NotNull Map<String, String> extraData
  );

  /**
   * Provides url of provider's authorization page
   * designated for authorization session of new Payment Initiation Session
   *
   * @param paymentId Unique identifier of payment order for which is required authorization
   * @return URL string
   */
  String getPaymentAuthorizationPageUrl(@NotEmpty String paymentId);
}
