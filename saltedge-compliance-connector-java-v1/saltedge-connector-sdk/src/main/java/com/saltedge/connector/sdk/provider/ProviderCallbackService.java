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

import com.saltedge.connector.sdk.api.services.payments.ConfirmPaymentService;
import com.saltedge.connector.sdk.api.services.tokens.ConfirmTokenService;
import com.saltedge.connector.sdk.api.services.tokens.RevokeTokenService;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.ConsentData;
import com.saltedge.connector.sdk.tools.TypeTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Implementation of ProviderCallback interface
 */
@Service
public class ProviderCallbackService implements ProviderCallback {
    @Autowired
    ConfirmTokenService confirmTokenService;
    @Autowired
    ConfirmPaymentService confirmPaymentService;
    @Autowired
    RevokeTokenService revokeTokenService;

    /**
     * Confirm token by session secret
     *
     * @param sessionSecret of Token create session
     * @param userId of authorized User
     * @return returnUrl from token
     */
    @Override
    public String authorizationOAuthSuccess(String sessionSecret, String userId, List<ConsentData> fetchConsents) {
        List<ConsentData> consents = fetchConsents.isEmpty() ? null : fetchConsents;
        Token token = confirmTokenService.confirmToken(sessionSecret, userId, consents);
        return (token == null) ? null : token.tppRedirectUrl;
    }

    @Override
    public String authorizationOAuthError(String sessionSecret, String errorMessage) {
        Token token = revokeTokenService.revokeTokenBySessionSecret(sessionSecret);
        return (token == null) ? null : token.tppRedirectUrl;
    }

    /**
     * Send payment success callback to Priora
     *
     * @param paymentId of confirmed Payment
     * @param extra data from Payment
     * @return returnUrl from Payment
     */
    @Override
    public String paymentConfirmationOAuthSuccess(String paymentId, Map<String, String> extra) {
        String sessionSecret = extra.get(Constants.KEY_SESSION_SECRET);
        String returnToUrl = extra.get(Constants.KEY_REDIRECT_URL);
        String prioraPaymentId = extra.get(Constants.KEY_PRIORA_PAYMENT_ID);
        confirmPaymentService.sendSuccessCallback(sessionSecret, paymentId, TypeTools.valueOf(prioraPaymentId));
        return returnToUrl;
    }
}
