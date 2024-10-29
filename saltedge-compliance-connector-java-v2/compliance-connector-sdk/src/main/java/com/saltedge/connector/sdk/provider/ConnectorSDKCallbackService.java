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

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.saltedge.connector.sdk.SDKConstants.PAYMENT_PRODUCT_FASTER_PAYMENT_SERVICE;
import static com.saltedge.connector.sdk.SDKConstants.PAYMENT_PRODUCT_INSTANT_SEPA_CREDIT_TRANSFERS;

/**
 * Class for call back communication from Provider application to Connector SDK Module.
 * Implementation of ProviderCallback interface.
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
     * @param sessionSecret unique identifier of authorization session
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
     * @param sessionSecret Unique identifier of authorization session
     * @return Ais Consent
     */
    @Override
    public AisToken getAisToken(@NotEmpty String sessionSecret) {
        return tokensCollectorService.findAisTokenBySessionSecret(sessionSecret);
    }

    /**
     * Collect list of all AIS Consents by user id.
     *
     * @param userId unique identifier of authenticated User
     * @return list of AIS Consents
     */
    @Override
    public List<AisToken> getAisTokens(@NotEmpty String userId) {
        return tokensCollectorService.collectAisTokensByUserId(userId);
    }

    /**
     * Collect list of all PIIS Consents by user id.
     *
     * @param userId unique identifier of authenticated User
     * @return list of PIIS Consents
     */
    @Override
    public List<AisToken> getPiisTokens(@NotEmpty String userId) {
        return tokensCollectorService.collectAisTokensByUserId(userId);
    }

    /**
     * Collect list of access tokens of active consents (AIS, PIIS).
     * Active - confirmed and non-expired consents.
     *
     * @param userId unique identifier of authenticated User
     * @return list of access tokens of active consents
     */
    @Override
    public List<String> getActiveAccessTokens(@NotEmpty String userId) {
        return tokensCollectorService.collectActiveAccessTokensByUserId(userId);
    }

    /**
     * Provider notify Connector SDK Module about oAuth success authentication and authorization of consent.
     *
     *
     * @param sessionSecret of User authorization session.
     * @param userId        of authenticated User.
     * @param accessToken   is an unique string that identifies a user access.
     *                      life period of accessToken is set by TPP and can not be more than 180 days.
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
     * Provider notifies Connector SDK Module about oAuth authentication fail
     * and SDK send fail callback request
     *
     * @param sessionSecret of Token Create session
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
     * Revoke Account information consent associated with userId and accessToken
     *
     * @param userId      unique identifier of User
     * @param accessToken unique string that identifies current access to Account Information of an User
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
     * Provider notify Connector Module about oAuth success authentication and user consent for payment
     *
     * @param userId Unique identifier of authenticated User
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

    @Override
    public void updatePaymentFundsInformation(Boolean fundsAvailable, String paymentExtra, String status) {
        Map<String, String> paymentExtraMap = parseExtra(paymentExtra);
        String sessionSecret = paymentExtraMap.get(SDKConstants.KEY_SESSION_SECRET);

        SessionUpdateCallbackRequest updateParams = new SessionUpdateCallbackRequest(fundsAvailable, status);
        if (StringUtils.hasText(sessionSecret)) sessionsCallbackService.sendUpdateCallbackAsync(sessionSecret, updateParams);
    }

    /**
     * Provider should notify Connector Module about oAuth authentication fail or Payment confirmation deny
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
     * Collect Account identifiers of PIIS consents
     *
     * @param sessionSecret unique identifier of consent authentication session
     * @return Account identifiers data
     */
    @Override
    public ParticipantAccount getFundsConfirmationConsentData(@NotEmpty String sessionSecret) {
        return tokensCollectorService.collectFundsConfirmationConsentData(sessionSecret);
    }

    /**
     * Provider notify Connector SDK Module about oAuth success authentication
     * and provides user consent for accounts (balances/transactions)
     *
     * @param sessionSecret of User authorization session.
     * @param userId        of authenticated User.
     * @param accessToken   is a unique string that identifies a user access.
     *                      life period of accessToken is set by TPP and can not be more than 180 days.
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
     * Provider notifies Connector SDK Module about oAuth authentication fail
     * and SDK send fail callback request
     *
     * @param sessionSecret of Token Create session
     * @return redirectUrl string for final redirection of Authorization session (in browser) back to TPP side.
     */
    @Override
    public String onFundsConfirmationConsentAuthorizationFail(@NotEmpty String sessionSecret) {
        PiisToken token = revokeTokenService.revokePiisTokenBySessionSecret(sessionSecret);
        sessionsCallbackService.sendFailCallbackAsync(sessionSecret, new Unauthorized.AccessDenied());
        return (token == null) ? null : token.tppRedirectUrl;
    }

    private String getFinalStatusOfPaymentProduct(@NotEmpty String paymentProduct) {
        switch (paymentProduct) {
            case PAYMENT_PRODUCT_FASTER_PAYMENT_SERVICE:
                return "ACSC";
            case PAYMENT_PRODUCT_INSTANT_SEPA_CREDIT_TRANSFERS:
                return "ACCC";
            default:
                return "ACTC";
        }
    }

    private Map<String, String> parseExtra(String paymentExtraJson) {
        try {
            return JsonTools.createDefaultMapper().readValue(paymentExtraJson, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            log.error("ConnectorSDKCallbackService.parseExtra", e);
        }
        return new HashMap<>();
    }
}
