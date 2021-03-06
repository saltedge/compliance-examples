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

import com.saltedge.connector.sdk.api.services.BaseService;
import com.saltedge.connector.sdk.callback.mapping.SessionCallbackRequest;
import com.saltedge.connector.sdk.callback.services.SessionsCallbackService;
import com.saltedge.connector.sdk.config.ApplicationProperties;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.models.persistence.TokensRepository;
import com.saltedge.connector.sdk.provider.models.ConsentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

abstract class TokensBaseService extends BaseService {
    private static Logger log = LoggerFactory.getLogger(TokensBaseService.class);
    @Autowired
    protected ApplicationProperties applicationProperties;
    @Autowired
    protected SessionsCallbackService callbackService;
    @Autowired
    protected TokensRepository tokensRepository;

    protected void initConfirmedTokenAndSendSessionSuccess(Token token) {
        initConfirmedTokenAndSendSessionSuccess(token, null);
    }

    protected void initConfirmedTokenAndSendSessionSuccess(Token token, List<ConsentData> fetchConsents) {
        token.initConfirmedToken();
        token.fetchConsents = fetchConsents;
        tokensRepository.save(token);
        sendSessionSuccess(token);
    }

    protected Token findTokenBySessionSecret(String sessionSecret) {
        return tokensRepository.findFirstBySessionSecret(sessionSecret);
    }

    protected void sendSessionSuccess(Token token) {
        SessionCallbackRequest params = new SessionCallbackRequest();
        params.userId = token.userId;
        params.providerCode = applicationProperties.getPrioraAppCode();
        params.token = token.accessToken;
        params.tokenExpiresAt = token.tokenExpiresAt;
        params.consent = token.fetchConsents;
        callbackService.sendSuccessCallback(token.sessionSecret, params);
    }

    protected void failSessionAndThrow(String sessionSecret, RuntimeException exception) {
        callbackService.sendFailCallback(sessionSecret, exception);
        throw exception;
    }
}
