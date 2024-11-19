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

import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.api.models.responses.ErrorResponse;
import com.saltedge.connector.sdk.models.ParticipantAccount;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.models.domain.PiisToken;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for call back communication from Provider application to Connector SDK Module
 *
 * @see ConnectorSDKCallbackService
 */
public interface ConnectorCallbackAbs {
    /**
     * Duplicate of isAccountSelectionRequired
     *
     * @see ConnectorCallbackAbs#isAccountSelectionRequired
     */
    boolean isUserConsentRequired(@NotEmpty String sessionSecret);

    /**
     * Check if User Consent (Bank Offered Consent) is required for authorization session determined by sessionSecret.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return True if User Consent (Bank Offered Consent) is required
     */
    boolean isAccountSelectionRequired(@NotEmpty String sessionSecret);

    /**
     * Return Ais Consent model.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return Ais Consent
     */
    AisToken getAisToken(@NotEmpty String sessionSecret);

    /**
     * Collect list of all AIS Consents by user id.
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @return list of AIS Consents
     */
    List<AisToken> getAisTokens(@NotEmpty String userId);

    /**
     * Collect list of access tokens of active consents (AIS, PIIS)
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @return List of access tokens of active consents
     */
    List<String> getActiveAccessTokens(@NotEmpty String userId);

    /**
     * ASPSP notifies Salt Edge Compliance service that the AIS authorisation finished with success
     * and provides user consent for accounts (balances/transactions).
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @param userId Unique PSU identifier issued by ASPSP.
     * @param accessToken Unique token that will be used to access ASPSP data. Token is a unique value which is linked to authenticated user and consent.
     *                    Life period of accessToken can not be more than 180 days.
     * @param consents List of balances and transactions of accounts for which is offered consent. Can be null if bank offered consent is not required.
     * @return returnUrl URL string for final redirection of Authorization session (in browser) back to TPP side.
     * @see ProviderServiceAbs#getAccountInformationAuthorizationPageUrl
     * @see ProviderConsents
     */
    String onAccountInformationAuthorizationSuccess(
            @NotEmpty String sessionSecret,
            @NotEmpty String userId,
            @NotEmpty String accessToken,
            ProviderConsents consents
    );

    /**
     * ASPSP notifies Salt Edge Compliance service that the AIS authorisation has been cancelled by user or failed.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return URL string for final redirection of Authorization session (in browser) back to TPP side.
     */
    String onAccountInformationAuthorizationFail(@NotEmpty String sessionSecret);

    /**
     * ASPSP notifies Salt Edge Compliance service that the AIS authorisation has been cancelled by user or failed.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @param userId        Unique PSU identifier issued by ASPSP.
     * @return redirectUrl string for final redirection of Authorization session (in browser) back to TPP side.
     */
    String onAccountInformationAuthorizationFail(@NotEmpty String sessionSecret, String userId);

    /**
     * Revoke Account information consent associated with userId and accessToken
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @param accessToken Unique token that will be used to access ASPSP data. Token is a unique value which is linked to authenticated user and consent.
     * @return Operation result, `true` if successful
     */
    boolean revokeAccountInformationConsent(@NotEmpty String userId, @NotEmpty String accessToken);

    /**
     * Get redirect url for final redirection of Authorization session of AIS Consent, back to TPP side.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return URL string for final redirection of Authorization session (in browser) back to TPP side.
     * @throws NotFound.TokenNotFound if Consent not found by sessionSecret.
     */
    String aisTppRedirectUrl(@NotEmpty String sessionSecret) throws NotFound.TokenNotFound;

    /**
     * Get redirect url for final redirection of Authorization session of Payment, back to TPP side.
     *
     * @param paymentExtra Unique identifier of authorization session.
     * @return URL string for final redirection of Authorization session (in browser) back to TPP side.
     * @throws NotFound.PaymentNotFound if paymentExtra has invalid format.
     */
    String pisTppRedirectUrl(@NotEmpty String paymentExtra) throws NotFound.PaymentNotFound;

    /**
     * ASPSP notifies Salt Edge Compliance service about funds availability and payment status changes.
     * NOTE: Should be performed only before onPaymentInitiationAuthorizationSuccess or onPaymentInitiationAuthorizationFail.
     *
     * @param paymentExtra Service data of payment order, provided in `ProviderServiceAbs.createPayment(...)`.
     * @param status intermediate payment status (RCVD, ACTC, ACSC, ACSP, ACWC, ACCP, PDNG, PATC, ACWP, ACFC).
     * @throws NotFound.PaymentNotFound if paymentExtra has invalid format.
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    CompletableFuture<ErrorResponse> updatePaymentStatus(
            @NotEmpty String paymentExtra,
            String status
    ) throws NotFound.PaymentNotFound, InterruptedException;

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIS flow (payment authorisation and payment initiation) finished with success.
     * As result Salt Edge Compliance service will set final status as indicated ij ASPSP Dashboard (e.g. ACSC, ACCC)
     * After calling onPaymentInitiateSuccess(...), PSU should be redirected back to TPP application.
     * tppRedirectUrl is passed in `ProviderServiceAbs.createPayment(...)`, or can be got from tppRedirectUrl(...).
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @param paymentExtra Service data of payment order, provided in `ProviderServiceAbs.createPayment(...)`.
     * @param debtorAccount If the debtor account was selected on ASPSP side this object must be indicated in request, containing the same debtor account identifiers as displayed to the end user in ASPSP interfaces.
     * @return CompletableFuture (with ErrorResponse) Result of Async job. Can be used for retry if an error occurs.
     * @throws NotFound.PaymentNotFound if paymentExtra has invalid format.
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     * @see ErrorResponse
     */
    CompletableFuture<ErrorResponse> onPaymentInitiateSuccess(
            @NotEmpty String userId,
            @NotEmpty String paymentExtra,
            ParticipantAccount debtorAccount
    ) throws NotFound.PaymentNotFound, InterruptedException;

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIS authorisation has been cancelled by user or failed.
     * After calling onPaymentInitiateSuccess(...), PSU should be redirected back to TPP application.
     * tppRedirectUrl is passed in `ProviderServiceAbs.createPayment(...)`, or can be got from tppRedirectUrl(...).
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @param paymentExtra Service data of payment order, provided in `ProviderServiceAbs.createPayment(...)`.
     * @param status Fail status of payment, Allowed: CANC, RJCT.
     * @return CompletableFuture (with ErrorResponse) Result of Async job. Can be used for retry if an error occurs.
     * @throws NotFound.PaymentNotFound if paymentExtra has invalid format.
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     * @see ErrorResponse
     */
    CompletableFuture<ErrorResponse> onPaymentInitiateFail(
            @NotEmpty String userId,
            @NotEmpty String paymentExtra,
            String status
    ) throws NotFound.PaymentNotFound, InterruptedException;

    /**
     * ASPSP notifies Salt Edge Compliance service about funds availability and payment status changes.
     * Should be performed only before onPaymentInitiationAuthorizationSuccess or onPaymentInitiationAuthorizationFail.
     *
     * @param fundsAvailable a value that indicates whether we have enough funds to make a payment
     * @param paymentExtra Service data of payment order, provided in `ProviderServiceAbs.createPayment(...)`.
     * @param status intermediate payment status (RCVD, ACTC, ACSC, ACSP, ACWC, ACCP, PDNG, PATC, ACWP, ACFC).
     * @throws NotFound.PaymentNotFound if paymentExtra has invalid format.
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    CompletableFuture<ErrorResponse> updatePaymentFundsInformation(
            Boolean fundsAvailable,
            @NotEmpty String paymentExtra,
            String status
    ) throws NotFound.PaymentNotFound, InterruptedException;

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIS flow (payment authorisation and payment initiation) finished with success.
     * As result Salt Edge Compliance service will set final status as indicated ij ASPSP Dashboard (e.g. ACSC, ACCC)
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @param paymentExtra Extra data of payment order, provided in `ProviderServiceAbs.createPayment(...)`
     * @param paymentProduct Payment product code (Allowed values: sepa-credit-transfers, instant-sepa-credit-transfers, target-2-payments, faster-payment-service, internal-transfer)
     * @return URL as string for final redirection of Payment Authorization session
     */
    String onPaymentInitiationAuthorizationSuccess(
            @NotEmpty String userId,
            @NotEmpty String paymentExtra,
            String paymentProduct
    ) throws NotFound.PaymentNotFound;

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIS authorisation has been cancelled by user or failed.
     *
     * @param paymentExtra Extra data of payment order, provided in `ProviderServiceAbs.createPayment(...)`
     * @return URL string for final redirection of Payment Authorization session
     */
    String onPaymentInitiationAuthorizationFail(@NotEmpty String paymentExtra) throws NotFound.PaymentNotFound;

    /**
     * Collect list of all PIIS Consents by user id.
     *
     * @param userId unique identifier of authenticated User
     * @return list of PIIS Consents
     */
    List<PiisToken> getPiisTokens(@NotEmpty String userId);

    /**
     * Collect Account identifiers of PIIS consents
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return Account identifiers data
     */
    ParticipantAccount getFundsConfirmationConsentData(@NotEmpty String sessionSecret);

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIIS authorisation finished with success.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @param userId Unique PSU identifier issued by ASPSP.
     * @param accessToken Unique token that will be used to access ASPSP data. Token is a unique value which is linked to authenticated user and consent.
     *                    Life period of accessToken for PIIS consent is unlimited.
     * @return returnUrl URL string for final redirection of Authorization session (in browser) back to TPP side.
     * @see ProviderServiceAbs#getAccountInformationAuthorizationPageUrl
     * @see ProviderConsents
     */
    String onFundsConfirmationConsentAuthorizationSuccess(
            @NotEmpty String sessionSecret,
            @NotEmpty String userId,
            @NotEmpty String accessToken
    );

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIIS authorisation has been cancelled by user or failed.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return returnUrl URL string for final redirection of Authorization session (in browser) back to TPP side.
     */
    String onFundsConfirmationConsentAuthorizationFail(@NotEmpty String sessionSecret);

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIIS authorisation has been cancelled by user or failed.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @param userId        Unique PSU identifier issued by ASPSP.
     * @return returnUrl URL string for final redirection of Authorization session (in browser) back to TPP side.
     */
    String onFundsConfirmationConsentAuthorizationFail(@NotEmpty String sessionSecret, String userId);

    /**
     * Get redirect url for final redirection of Authorization session of PIIS Consent, back to TPP side.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return URL string for final redirection of Authorization session (in browser) back to TPP side.
     * @throws NotFound.TokenNotFound if Consent not found by sessionSecret.
     */
    String piisTppRedirectUrl(@NotEmpty String sessionSecret) throws NotFound.TokenNotFound;
}
