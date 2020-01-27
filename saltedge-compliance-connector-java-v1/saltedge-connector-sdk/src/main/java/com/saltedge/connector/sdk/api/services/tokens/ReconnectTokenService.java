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

import com.saltedge.connector.sdk.api.err.BadRequest;
import com.saltedge.connector.sdk.api.err.HttpErrorParams;
import com.saltedge.connector.sdk.callback.mapping.SessionCallbackRequest;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.tools.UrlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReconnectTokenService extends TokensBaseService {
    private static Logger log = LoggerFactory.getLogger(ReconnectTokenService.class);

    @Async
    public void reconnect(Token token, String sessionSecret) {
        try {
            SessionCallbackRequest callbackParams = new SessionCallbackRequest();
            switch (providerApi.getReconnectPolicyType(token.userId)) {
                case GRANT:
                    token.sessionSecret = sessionSecret;
                    token.regenerateTokenAndExpiresAt();
                    super.sendSessionSuccess(token);
                    break;
                case MFA:
                    token.sessionSecret = sessionSecret;
                    token.status = Token.Status.UNCONFIRMED;

                    callbackParams.userId = token.userId;
                    callbackParams.providerCode = applicationProperties.getPrioraAppCode();
                    HashMap<String, String> extra = new HashMap<>();
                    extra.put(Constants.KEY_REDIRECT_URL, createRedirectUrl(token.sessionSecret));
                    callbackParams.extra = extra;
                    callbackParams.status = Constants.STATUS_REDIRECT;
                    callbackService.sendUpdateCallback(token.sessionSecret, callbackParams);
                    break;
                case DENY:
                    callbackParams.userId = token.userId;
                    callbackParams.providerCode = applicationProperties.getPrioraAppCode();
                    callbackParams.errorClass = "CannotReconnectToken";
                    callbackParams.errorMessage = "This PSP cannot reconnect token";
                    callbackService.sendFailCallback(token.sessionSecret, callbackParams);
                    break;
            }
            tokensRepository.save(token);
        } catch (Exception e) {
            log.error("ReconnectTokenService.reconnect:", e);
            RuntimeException failException = (e instanceof HttpErrorParams) ? (RuntimeException) e : new BadRequest.WrongRequestFormat();
            callbackService.sendFailCallback(sessionSecret, failException);
        }
    }

    private String createRedirectUrl(String sessionSecret) {
        return UrlTools.appendParam(providerApi.getAuthorizationPageUrl(), Constants.KEY_SESSION_SECRET, sessionSecret);
    }
}
