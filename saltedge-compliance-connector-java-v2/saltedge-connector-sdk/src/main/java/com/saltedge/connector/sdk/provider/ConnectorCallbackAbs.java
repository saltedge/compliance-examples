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

import com.saltedge.connector.sdk.provider.models.ProviderOfferedConsents;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Interface for call back communication from Provider application to Connector Module
 * @see ConnectorCallbackService
 */
public interface ConnectorCallbackAbs {
    /**
     * Provider notify Connector Module about oAuth success authentication and user consent for accounts
     *
     * @param sessionSecret of Token Create session
     * @param userId of authenticated User
     * @param consents list of balances of accounts and transactions of accounts
     * @return returnUrl from token
     */
    String onOAuthAccountsAuthorizationSuccess(
            @NotEmpty String sessionSecret,
            @NotEmpty String userId,
            @NotNull ProviderOfferedConsents consents
    );

    /**
     * Provider should notify Connector Module about oAuth authentication fail
     *
     * @param sessionSecret of Token Create session
     * @return returnUrl from token
     */
    String onOAuthAccountsAuthorizationError(@NotEmpty String sessionSecret);

    /**
     * Provider notify Connector Module about oAuth success authentication and user consent for payment
     *
     * @param paymentId of payment
     * @param userId of authenticated User
     * @param paymentExtra extra data of payment order
     * @return returnUrl for Payment authenticate session
     */
    String onOAuthPaymentAuthorizationSuccess(
            @NotEmpty String paymentId,
            @NotEmpty String userId,
            @NotEmpty Map<String, String> paymentExtra
    );

    /**
     * Provider should notify Connector Module about oAuth authentication fail or Payment confirmation deny
     *
     * @param paymentId of payment
     * @param paymentExtra extra data of payment order
     * @return returnUrl for Payment authenticate session
     */
    String onOAuthPaymentAuthorizationFail(@NotEmpty String paymentId, @NotEmpty Map<String, String> paymentExtra);
}
