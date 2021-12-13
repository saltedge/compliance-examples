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
package com.saltedge.connector.ob.sdk.provider;

import com.saltedge.connector.ob.sdk.provider.dto.account.*;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObRiskData;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface for communication between Compliance Connector SDK and Provider/ASPSP application.
 *
 * Service Provider application should implement `@Service` which implements `ProviderServiceAbs`
 */
public interface ProviderServiceAbs {

  /**
   * Return accounts information of user.
   * Serves endpoint (https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-accounts-get)
   *
   * @param userId User identifier on Provider side
   * @return List of Account objects
   * @see ObAccount
   */
  List<ObAccount> getAccountsOfUser(@NotEmpty String userId);

  /**
   * Provides transactions which belong to an account of user.
   * Serves endpoint (https://priora.saltedge.com/docs/aspsp/v2/ais#ais-connector_endpoints-accounts-transactions)
   *
   * @param userId User identifier on Provider side
   * @param accountId Account identifier on Provider side
   * @param fromDate Specified start date and time for the transaction query period. If this is not populated, the start date will be open ended, and data will be returned from the earliest available transaction.
   * @param toDate Specified end date and time for the transaction query period. If this is not populated, the end date will be open ended, and data will be returned to the latest available transaction.
   * @param fromId ID of page for Pageable request.
   *
   * @return Page container with list of Transaction objects and next page id
   * @see ObTransaction
   * @see TransactionsPage
   */
  TransactionsPage getTransactionsOfAccount(
    @NotEmpty String userId,
    @NotEmpty String accountId,
    @NotNull LocalDate fromDate,
    @NotNull LocalDate toDate,
    String fromId
  );

  boolean confirmFunds(@NotEmpty String userId, @NotNull ObAccountIdentifier debtorAccount, @NotNull ObAmount amount);

  /**
   * Initiate a payment order, Domestic or International.
   * Serves payment endpoint (https://priora.saltedge.com/docs/aspsp/v2/pis#pis-connector_endpoints-payments)
   *
   * @param userId User identifier on Provider side
   * @param paymentType Specifies the type of payment associated with a preregistered template. Allowed values: domestic_payment, international_payment.
   * @param paymentInitiation Payment initiation data
   * @param risk Specify additional details for risk scoring for Payments.
   * @return Payment unique identifier.
   */
  String initiatePayment(
      @NotEmpty String userId,
      @NotEmpty String paymentType,
      @NotNull ObPaymentInitiationData paymentInitiation,
      ObRiskData risk
  );
}
