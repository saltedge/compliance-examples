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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.saltedge.connector.sdk.SDKConstants.PAYMENT_PRODUCT_FASTER_PAYMENT_SERVICE;
import static com.saltedge.connector.sdk.SDKConstants.PAYMENT_PRODUCT_INSTANT_SEPA_CREDIT_TRANSFERS;

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
     * Return Ais Consent model.
     *
     * @param sessionSecret Unique identifier of authorization session.
     * @return Ais Consent model.
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
     * ASPSP notifies Salt Edge Compliance service about funds availability and payment status changes.
     * Should not be performed after onPaymentInitiationAuthorizationSuccess or onPaymentInitiationAuthorizationFail.
     *
     * @param fundsAvailable a value that indicates whether we have enough funds to make a payment
     * @param paymentExtra Extra data of payment order, provided in `ProviderServiceAbs.createPayment(...)`
     * @param status intermediate status (RCVD, ACTC, ACSC, ACSP, ACWC, ACCP, PDNG, PATC, ACWP, ACFC)
     */
    @Override
    public void updatePaymentFundsInformation(Boolean fundsAvailable, String paymentExtra, String status) {
        Map<String, String> paymentExtraMap = parseExtra(paymentExtra);
        String sessionSecret = paymentExtraMap.get(SDKConstants.KEY_SESSION_SECRET);

        SessionUpdateCallbackRequest updateParams = new SessionUpdateCallbackRequest(fundsAvailable, status);
        if (StringUtils.hasText(sessionSecret)) sessionsCallbackService.sendUpdateCallbackAsync(sessionSecret, updateParams);
    }

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIS flow (payment authorisation and payment initiation) finished with success.
     * As result Salt Edge Compliance service will set final status as indicated ij ASPSP Dashboard (e.g. ACSC, ACCC)
     *
     * @param userId Unique PSU identifier issued by ASPSP.
     * @param paymentExtra Extra data of payment order, provided in `ProviderServiceAbs.createPayment(...)`
     * @param paymentProduct Payment product code (Allowed values: sepa-credit-transfers, instant-sepa-credit-transfers, target-2-payments, faster-payment-service, internal-transfer)
     * @return returnUrl string for final redirection of Payment Authorization session
     */
    @Override
    public String onPaymentInitiationAuthorizationSuccess(
            @NotEmpty String userId,
            @NotEmpty String paymentExtra,
            @NotEmpty String paymentProduct
    ) {
        Map<String, String> paymentExtraMap = parseExtra(paymentExtra);

        String sessionSecret = paymentExtraMap.get(SDKConstants.KEY_SESSION_SECRET);
        String status = getFinalStatusOfPaymentProduct(paymentProduct);
        SessionSuccessCallbackRequest params = new SessionSuccessCallbackRequest(userId, status);
        try {
            if (StringUtils.hasLength(sessionSecret)) sessionsCallbackService.sendSuccessCallbackAsync(sessionSecret, params);
        } catch (Exception ignored) {
        }

        return paymentExtraMap.getOrDefault(SDKConstants.KEY_RETURN_TO_URL, "");
    }

    /**
     * ASPSP notifies Salt Edge Compliance service that the PIS authorisation has been cancelled by user or failed.
     *
     * @param paymentExtra extra data of payment order
     * @return returnUrl string for final redirection of Payment Authorization session
     */
    @Override
    public String onPaymentInitiationAuthorizationFail(@NotEmpty String paymentExtra) {
        Map<String, String> paymentExtraMap = parseExtra(paymentExtra);

        String sessionSecret = paymentExtraMap.get(SDKConstants.KEY_SESSION_SECRET);
        if (StringUtils.hasLength(sessionSecret)) {
            sessionsCallbackService.sendFailCallbackAsync(sessionSecret, new NotFound.PaymentNotCreated());
        }

        return paymentExtraMap.getOrDefault(SDKConstants.KEY_RETURN_TO_URL, "");
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
        sessionsCallbackService.sendFailCallbackAsync(sessionSecret, new Unauthorized.AccessDenied(), userId);
        return (token == null) ? null : token.tppRedirectUrl;
    }

    private String getFinalStatusOfPaymentProduct(@NotEmpty String paymentProduct) {
        return switch (paymentProduct) {
            case PAYMENT_PRODUCT_FASTER_PAYMENT_SERVICE -> "ACSC";
            case PAYMENT_PRODUCT_INSTANT_SEPA_CREDIT_TRANSFERS -> "ACCC";
            default -> "ACTC";
        };
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
