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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

/**
 * Interface for call back communication from Provider application to Connector SDK Module
 * @see ConnectorSDKCallbackService
 */
public interface ConnectorCallbackAbs {

    /**
     * Check if User Consent (Bank Offered Consent) is required for authorization session determined by sessionSecret.
     *
     * @param sessionSecret unique identifier of authorization session
     * @return true if User Consent (Bank Offered Consent) is required
     */
    boolean isUserConsentRequired(@NotEmpty String sessionSecret);

    /**
     * Provider notify Connector SDK Module about oAuth success authentication
     * and provides user consent for accounts (balances/transactions)
     *
     * @param sessionSecret of User authorization session.
     * @param userId of authenticated User.
     * @param accessToken is an unique string that identifies a user access.
     *                    life period of accessToken is set by TPP and can not be more than 90 days.
     * @param consents list of balances of accounts and transactions of accounts.
     *                 Can be null if bank offered consent is not required.
     *
     * @return returnUrl string for final redirection of Authorization session (in browser) back to TPP side.
     *
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
     * @deprecated
     * This method is expected to be retained only for back compatibility.
     * Replaced by {@link #onAccountInformationAuthorizationSuccess(String, String, String, ProviderConsents)}
     *
     * Provider notify Connector SDK Module about oAuth success authentication
     * and provides user consent for accounts (balances/transactions)
     *
     * @param sessionSecret of User authorization session.
     * @param userId of authenticated User.
     * @param accessToken is an unique string that identifies a user.
     * @param accessTokenExpiresAt expiration time of accessToken (UTC time).
     * @param consents list of balances of accounts and transactions of accounts.
     *                 Can be null if bank offered consent is not required.
     *
     * @return returnUrl string for final redirection of Authorization session (in browser) back to TPP side.
     *
     * @see ProviderServiceAbs#getAccountInformationAuthorizationPageUrl
     * @see ProviderConsents
     */
    @Deprecated
    String onAccountInformationAuthorizationSuccess(
            @NotEmpty String sessionSecret,
            @NotEmpty String userId,
            @NotEmpty String accessToken,
            @NotNull Instant accessTokenExpiresAt,
            ProviderConsents consents
    );

    /**
     * Provider notifies Connector SDK Module about oAuth authentication fail
     *
     * @param sessionSecret of Token Create session
     * @return returnUrl string for final redirection of Authorization session (in browser) back to TPP side.
     */
    String onAccountInformationAuthorizationFail(@NotEmpty String sessionSecret);

    /**
     * Revoke Account information consent associated with userId and accessToken
     *
     * @param userId unique identifier of authenticated User
     * @param accessToken unique string that identifies a user
     *
     * @return operation result
     */
    boolean revokeAccountInformationConsent(
            @NotEmpty String userId,
            @NotEmpty String accessToken
    );

    /**
     * Provider notify Connector Module about oAuth success authentication and user consent for payment
     *
     * @param paymentId of payment
     * @param userId of authenticated User
     * @param paymentExtra extra data of payment order
     * @return returnUrl string for final redirection of Payment Authorization session
     */
    String onPaymentInitiationAuthorizationSuccess(
            @NotEmpty String paymentId,
            @NotEmpty String userId,
            @NotEmpty Map<String, String> paymentExtra
    );

    /**
     * Provider should notify Connector Module about oAuth authentication fail or Payment confirmation deny
     *
     * @param paymentId of payment
     * @param paymentExtra extra data of payment order
     * @return returnUrl string for final redirection of Payment Authorization session
     */
    String onPaymentInitiationAuthorizationFail(@NotEmpty String paymentId, @NotEmpty Map<String, String> paymentExtra);
}
