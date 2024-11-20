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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.api.models.err.Unauthorized;
import com.saltedge.connector.sdk.api.models.responses.ErrorResponse;
import com.saltedge.connector.sdk.callback.SessionsCallbackService;
import com.saltedge.connector.sdk.callback.TokensCallbackService;
import com.saltedge.connector.sdk.callback.mapping.SessionSuccessCallbackRequest;
import com.saltedge.connector.sdk.callback.mapping.SessionUpdateCallbackRequest;
import com.saltedge.connector.sdk.models.ParticipantAccount;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import com.saltedge.connector.sdk.services.provider.ConfirmTokenService;
import com.saltedge.connector.sdk.services.provider.RevokeTokenByProviderService;
import com.saltedge.connector.sdk.services.provider.TokensCollectorService;
import com.saltedge.connector.sdk.tools.JsonTools;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Class for call back communication from Provider application to Connector SDK Module.
 * Implementation of ConnectorCallbackAbs interface.
 *
 * @see ConnectorCallbackAbs
 */
@Service
@Validated
public class ConnectorSDKCallbackService implements ConnectorCallbackAbs {
    private static final Logger log = LoggerFactory.getLogger(ConnectorSDKCallbackService.class);
    @Autowired
    private ConfirmTokenService confirmTokenService;
    @Autowired
    private TokensCollectorService tokensCollectorService;
    @Autowired
    private RevokeTokenByProviderService revokeTokenService;
    @Autowired
    private SessionsCallbackService sessionsCallbackService;
    @Autowired
    private TokensCallbackService tokensCallbackService;

    /**
     * Duplicate of isAccountSelectionRequired
     */
    @Override
    public boolean isUserConsentRequired(@NotEmpty String sessionSecret) {
        return isAccountSelectionRequired(sessionSecret);
    }

    /**
     * Check if User Consent (Bank Offered Consent) is required for AIS authorization session determined by sessionSecret.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return true if User Consent (Bank Offered Consent) is required
     */
    @Override
    public boolean isAccountSelectionRequired(@NotEmpty String sessionSecret) {
        AisToken token = tokensCollectorService.findAisTokenBySessionSecret(sessionSecret);
        return token != null && token.notGlobalConsent();
    }

    /**
     * Return AIS Consent model.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return AIS Consent model.
     */
    @Override
    public AisToken getAisToken(@NotEmpty String sessionSecret) {
        return tokensCollectorService.findAisTokenBySessionSecret(sessionSecret);
    }

    /**
     * Collect list of all AIS Consents by user id.
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @return list of AIS Consent models.
     */
    @Override
    public List<AisToken> getAisTokens(@NotEmpty String userId) {
        return tokensCollectorService.collectAisTokensByUserId(userId);
    }

    /**
     * Collect list of access tokens of active consents (AIS, PIIS).
     * Active - confirmed and non-expired consents.
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @return list of access tokens of active consents
     */
    @Override
    public List<String> getActiveAccessTokens(@NotEmpty String userId) {
        return tokensCollectorService.collectActiveAccessTokensByUserId(userId);
    }

    /**
     * ASPSP notifies Salt Edge Compliance Service that the AIS authorisation finished with success
     * and provides user consent for accounts (balances/transactions).
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @param userId        Unique PSU identifier issued by ASPSP.
     * @param accessToken   Unique token that will be used to access ASPSP data. Token is a unique value which is linked to authenticated user and consent.
     *                      Life period of accessToken can not be more than 180 days.
     * @param consents      bank offered consent with list of balances of accounts and transactions of accounts.
     *                      Can be null if bank offered consent is not required.
     * @return redirectUrl string for final redirection of Authorization session (in browser) back to TPP side.
     * @see ProviderServiceAbs#getAccountInformationAuthorizationPageUrl
     * @see ProviderConsents
     */
    @Override
    public String onAccountInformationAuthorizationSuccess(
            @NotEmpty String sessionSecret,
            @NotEmpty String userId,
            @NotEmpty String accessToken,
            ProviderConsents consents
    ) {
        return confirmTokenService.confirmAisToken(sessionSecret, userId, accessToken, consents);
    }

    /**
     * ASPSP notifies Salt Edge Compliance Service that the AIS authorisation has been cancelled by user or failed.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return redirectUrl string for final redirection of Authorization session (in browser) back to TPP side.
     */
    @Override
    public String onAccountInformationAuthorizationFail(@NotEmpty String sessionSecret) {
        AisToken token = revokeTokenService.revokeAisTokenBySessionSecret(sessionSecret);
        if (token == null) return null;

        sessionsCallbackService.sendFailCallbackAsync(sessionSecret, new Unauthorized.AccessDenied());
        return token.tppRedirectUrl;
    }

    /**
     * ASPSP notifies Salt Edge Compliance Service that the AIS authorisation has been cancelled by user or failed.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @param userId        Unique PSU identifier issued by ASPSP.
     * @return redirectUrl string for final redirection of Authorization session (in browser) back to TPP side.
     */
    @Override
    public String onAccountInformationAuthorizationFail(@NotEmpty String sessionSecret, String userId) {
        AisToken token = revokeTokenService.revokeAisTokenBySessionSecret(sessionSecret);
        if (token == null) return null;

        sessionsCallbackService.sendFailCallbackAsync(sessionSecret, new Unauthorized.AccessDenied());
        return token.tppRedirectUrl;
    }

    /**
     * Revoke Account information consent associated with userId and accessToken
     *
     * @param userId      Unique PSU identifier issued by ASPSP.
     * @param accessToken Unique token that will be used to access ASPSP data. Token is a unique value which is linked to authenticated user and consent.
     * @return true if revoke order is saved
     */
    @Override
    public boolean revokeAccountInformationConsent(@NotEmpty String userId, @NotEmpty String accessToken) {
        AisToken token = revokeTokenService.revokeAisTokenByUserIdAndAccessToken(userId, accessToken);
        boolean isRevoked = token != null && token.isRevoked();
        if (isRevoked) tokensCallbackService.sendRevokeAisTokenCallbackAsync(accessToken);
        return isRevoked;
    }

    /**
     * Get redirect url for final redirection of Authorization session of AIS Consent, back to TPP side.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return URL string for final redirection of Authorization session (in browser) back to TPP side.
     * @throws NotFound.TokenNotFound if Consent not found by sessionSecret.
     */
    public String aisTppRedirectUrl(@NotEmpty String sessionSecret) throws NotFound.TokenNotFound {
        AisToken token = tokensCollectorService.findAisTokenBySessionSecret(sessionSecret);
        if (token == null) throw new NotFound.TokenNotFound();
        return token.tppRedirectUrl;
    }

    /**
     * Get redirect url for final redirection of Authorization session of Payment, back to TPP side.
     *
     * @param paymentExtra Unique identifier of authorization session.
     * @return URL string for final redirection of Authorization session (in browser) back to TPP side.
     * @throws NotFound.PaymentNotFound if paymentExtra has invalid format.
     */
    public String pisTppRedirectUrl(@NotEmpty String paymentExtra) throws NotFound.PaymentNotFound {
        return extractReturnToUrl(paymentExtra);
    }

    /**
     * ASPSP notifies Salt Edge Compliance service about funds availability and payment status changes.
     * NOTE: Should be performed only before onPaymentInitiationAuthorizationSuccess or onPaymentInitiationAuthorizationFail.
     *
     * @param paymentExtra Service data of payment order, provided in `ProviderServiceAbs.createPayment(...)`.
     * @param status intermediate payment status (RCVD, ACTC, ACSC, ACSP, ACWC, ACCP, PDNG, PATC, ACWP, ACFC).
     * @throws NotFound.PaymentNotFound if paymentExtra has invalid format.
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    @Override
    public CompletableFuture<ErrorResponse> updatePaymentStatus(
            @NotEmpty String paymentExtra,
            String status
    ) throws NotFound.PaymentNotFound, InterruptedException {
        String sessionSecret = extractSessionSecret(paymentExtra);
        SessionUpdateCallbackRequest updateParams = new SessionUpdateCallbackRequest(status);
        return sessionsCallbackService.sendUpdateCallbackAsync(sessionSecret, updateParams);
    }

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
    @Override
    public CompletableFuture<ErrorResponse> onPaymentInitiateSuccess(
            @NotEmpty String userId,
            @NotEmpty String paymentExtra,
            ParticipantAccount debtorAccount
    ) throws NotFound.PaymentNotFound, InterruptedException {
        String sessionSecret = extractSessionSecret(paymentExtra);
        SessionSuccessCallbackRequest params = SessionSuccessCallbackRequest.successPisCallback(userId, debtorAccount);
        return sessionsCallbackService.sendSuccessCallbackAsync(sessionSecret, params);
    }

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
    @Override
    public CompletableFuture<ErrorResponse> onPaymentInitiateFail(
            @NotEmpty String userId,
            @NotEmpty String paymentExtra,
            String status
    ) throws NotFound.PaymentNotFound, InterruptedException {
        String sessionSecret = extractSessionSecret(paymentExtra);
        return sessionsCallbackService.sendFailCallbackAsync(sessionSecret, new NotFound.PaymentNotCreated(), userId, status);
    }

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
    @Override
    public CompletableFuture<ErrorResponse> updatePaymentFundsInformation(
            Boolean fundsAvailable,
            @NotEmpty String paymentExtra,
            String status
    ) throws NotFound.PaymentNotFound, InterruptedException {
        String sessionSecret = extractSessionSecret(paymentExtra);
        SessionUpdateCallbackRequest updateParams = new SessionUpdateCallbackRequest(fundsAvailable, status);
        return sessionsCallbackService.sendUpdateCallbackAsync(sessionSecret, updateParams);
    }

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIS flow (payment authorisation and payment initiation) finished with success.
     * As result Salt Edge Compliance service will set final status as indicated ij ASPSP Dashboard (e.g. ACSC, ACCC).
     * After calling onPaymentInitiationAuthorizationSuccess(...), PSU should be redirected back to TPP application.
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @param paymentExtra Service data of payment order, provided in `ProviderServiceAbs.createPayment(...)`.
     * @param paymentProduct Payment product code (Allowed values: sepa-credit-transfers, instant-sepa-credit-transfers, target-2-payments, faster-payment-service, internal-transfer)
     * @return returnUrl string for final redirection of Payment Authorization session.
     * @throws NotFound.PaymentNotFound if paymentExtra has invalid format.
     */
    @Override
    public String onPaymentInitiationAuthorizationSuccess(
            @NotEmpty String userId,
            @NotEmpty String paymentExtra,
            String paymentProduct
    ) throws NotFound.PaymentNotFound {
        String sessionSecret = extractSessionSecret(paymentExtra);
        SessionSuccessCallbackRequest params = SessionSuccessCallbackRequest.successPisCallback(userId, null);
        try {
            sessionsCallbackService.sendSuccessCallbackAsync(sessionSecret, params);
        } catch (Exception ignored) {
        }

        return extractReturnToUrl(paymentExtra);
    }

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIS authorisation has been cancelled by user or failed.
     *
     * @param paymentExtra Service data of payment order, provided in `ProviderServiceAbs.createPayment(...)`.
     * @return returnUrl string for final redirection of Payment Authorization session.
     * @throws NotFound.PaymentNotFound if paymentExtra has invalid format.
     */
    @Override
    public String onPaymentInitiationAuthorizationFail(@NotEmpty String paymentExtra) throws NotFound.PaymentNotFound {
        String sessionSecret = extractSessionSecret(paymentExtra);
        sessionsCallbackService.sendFailCallbackAsync(sessionSecret, new NotFound.PaymentNotCreated());
        return extractReturnToUrl(paymentExtra);
    }

    /**
     * Collect list of all PIIS Consents by user id.
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @return list of PIIS Consent models.
     */
    @Override
    public List<PiisToken> getPiisTokens(@NotEmpty String userId) {
        return tokensCollectorService.collectPiisTokensByUserId(userId);
    }

    /**
     * Collect Account identifiers of PIIS consents
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return Account identifiers data
     */
    @Override
    public ParticipantAccount getFundsConfirmationConsentData(@NotEmpty String sessionSecret) {
        return tokensCollectorService.collectFundsConfirmationConsentData(sessionSecret);
    }

    /**
     * ASPSP notifies Salt Edge Compliance Service that the PIIS authorisation finished with success.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @param userId        Unique PSU identifier issued by ASPSP.
     * @param accessToken   Unique token that will be used to access ASPSP data. Token is a unique value which is linked to authenticated user and consent.
     *                      Life period of accessToken for PIIS consent is unlimited.
     * @return redirectUrl string for final redirection of Authorization session (in browser) back to TPP side.
     * @see ProviderServiceAbs#getAccountInformationAuthorizationPageUrl
     * @see ProviderConsents
     */
    @Override
    public String onFundsConfirmationConsentAuthorizationSuccess(
            @NotEmpty String sessionSecret,
            @NotEmpty String userId,
            @NotEmpty String accessToken
    ) {
        return confirmTokenService.confirmPiisToken(sessionSecret, userId, accessToken);
    }

    /**
     * ASPSP notifies Salt Edge Compliance Service that the PIIS authorisation has been cancelled by user or failed.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return redirectUrl string for final redirection of Authorization session (in browser) back to TPP side.
     */
    @Override
    public String onFundsConfirmationConsentAuthorizationFail(@NotEmpty String sessionSecret) {
        PiisToken token = revokeTokenService.revokePiisTokenBySessionSecret(sessionSecret);
        sessionsCallbackService.sendFailCallbackAsync(sessionSecret, new Unauthorized.AccessDenied());
        return (token == null) ? null : token.tppRedirectUrl;
    }

    /**
     * ASPSP notifies Salt Edge Compliance Service that the PIIS authorisation has been cancelled by user or failed.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @param userId        Unique PSU identifier issued by ASPSP.
     * @return redirectUrl string for final redirection of Authorization session (in browser) back to TPP side.
     */
    @Override
    public String onFundsConfirmationConsentAuthorizationFail(@NotEmpty String sessionSecret, String userId) {
        PiisToken token = revokeTokenService.revokePiisTokenBySessionSecret(sessionSecret);
        try {
            sessionsCallbackService.sendFailCallbackAsync(sessionSecret, new Unauthorized.AccessDenied(), userId);
        } catch (InterruptedException ignored) {
        }
        return (token == null) ? null : token.tppRedirectUrl;
    }

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIIS authorisation has been revoked by user on ASPSP side.
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @param accessToken Unique token that will be used to access ASPSP data. Token is a unique value which is linked to authenticated user and consent.
     * @return Operation result, `true` if successful
     */
    @Override
    public boolean revokeFundsConfirmationConsent(@NotEmpty String userId, @NotEmpty String accessToken) {
        PiisToken token = revokeTokenService.revokePiisTokenByUserIdAndAccessToken(userId, accessToken);
        boolean isRevoked = token != null && token.isRevoked();
        if (isRevoked) tokensCallbackService.sendRevokePiisTokenCallbackAsync(accessToken);
        return isRevoked;
    }

    /**
     * Get redirect url for final redirection of Authorization session of PIIS Consent, back to TPP side.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return URL string for final redirection of Authorization session (in browser) back to TPP side.
     * @throws NotFound.TokenNotFound if Consent not found by sessionSecret.
     */
    public String piisTppRedirectUrl(@NotEmpty String sessionSecret) throws NotFound.TokenNotFound {
        PiisToken token = tokensCollectorService.findPiisTokenBySessionSecret(sessionSecret);
        if (token == null) throw new NotFound.TokenNotFound();
        return token.tppRedirectUrl;
    }

    private String extractSessionSecret(String paymentExtraJson) throws NotFound.PaymentNotFound {
        Map<String, String> paymentExtraMap = parseExtra(paymentExtraJson);
        String sessionSecret = paymentExtraMap.get(SDKConstants.KEY_SESSION_SECRET);
        if (!StringUtils.hasLength(sessionSecret)) {
            throw new NotFound.PaymentNotFound("Invalid paymentExtra. Not found session_secret.");
        }
        return sessionSecret;
    }

    private String extractReturnToUrl(String paymentExtraJson) throws NotFound.PaymentNotFound {
        Map<String, String> paymentExtraMap = parseExtra(paymentExtraJson);
        String url = paymentExtraMap.get(SDKConstants.KEY_RETURN_TO_URL);
        if (!StringUtils.hasLength(url)) {
            throw new NotFound.PaymentNotFound("Invalid paymentExtra. Not found return_to_url.");
        }
        return url;
    }

    private Map<String, String> parseExtra(String paymentExtraJson) {
        try {
            return JsonTools.createDefaultMapper().readValue(paymentExtraJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("ConnectorSDKCallbackService.parseExtra", e);
        }
        return new HashMap<>();
    }
}
