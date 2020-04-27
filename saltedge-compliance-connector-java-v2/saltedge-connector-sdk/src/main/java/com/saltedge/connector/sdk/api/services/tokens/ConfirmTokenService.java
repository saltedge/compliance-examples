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
package com.saltedge.connector.sdk.api.services.tokens;

import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.callback.mapping.SessionSuccessCallbackRequest;
import com.saltedge.connector.sdk.models.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Validated
public class ConfirmTokenService extends TokensBaseService {
    private static Logger log = LoggerFactory.getLogger(ConfirmTokenService.class);

    public Token confirmToken(
            @NotEmpty String sessionSecret,
            @NotEmpty String userId,
            @NotEmpty String accessToken,
            @NotNull Instant accessTokenExpiresAt,
            ProviderConsents providerOfferedConsents
    ) {
        Token token = findTokenBySessionSecret(sessionSecret);
        if (token != null) {
            try {
                token.userId = userId;
                token.status = Token.Status.CONFIRMED;
                token.accessToken = accessToken;
                token.tokenExpiresAt = accessTokenExpiresAt;
                if (!token.hasGlobalConsent()) {
                    token.providerOfferedConsents = (providerOfferedConsents == null)
                            ? ProviderConsents.buildAllAccountsConsent() : providerOfferedConsents;
                }
                tokensRepository.save(token);

                sendSessionSuccess(token);
            } catch (Exception e) {
                log.error("initConfirmedTokenAndSendSessionSuccess: ", e);
                callbackService.sendFailCallback(token.sessionSecret, e);
            }
        }
        return token;
    }

    private void sendSessionSuccess(Token token) {
        SessionSuccessCallbackRequest params = new SessionSuccessCallbackRequest(
                token.providerOfferedConsents,
                token.accessToken,
                token.tokenExpiresAt,
                token.userId
        );
        callbackService.sendSuccessCallback(token.sessionSecret, params);
    }
}
