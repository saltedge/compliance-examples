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
import com.saltedge.connector.sdk.api.err.NotFound;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.AuthorizationType;
import com.saltedge.connector.sdk.provider.models.ConsentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConfirmTokenService extends TokensBaseService {
    private static Logger log = LoggerFactory.getLogger(ConfirmTokenService.class);

    public void confirmToken(String sessionSecret, Map<String, String> credentials) {
        Token token = findTokenBySessionSecret(sessionSecret);
        if (token == null) {
            super.failSessionAndThrow(sessionSecret, new NotFound.TokenNotFound());
        } else if (StringUtils.isEmpty(token.confirmationCode)
                || token.confirmationCode.equals(extractConfirmationCode(token, credentials))) {
            super.initConfirmedTokenAndSendSessionSuccess(token);
        } else {
            token.status = Token.Status.REVOKED;
            tokensRepository.save(token);
            super.failSessionAndThrow(sessionSecret, new BadRequest.InvalidConfirmationCode());
        }
    }

    public Token confirmToken(String sessionSecret, String userId, List<ConsentData> fetchConsents) {
        if (StringUtils.isEmpty(sessionSecret == null) || StringUtils.isEmpty(userId)) return null;
        Token token = findTokenBySessionSecret(sessionSecret);
        if (token != null) {
            token.userId = userId;
            super.initConfirmedTokenAndSendSessionSuccess(token, fetchConsents);
        }
        return token;
    }

    private String extractConfirmationCode(Token token, Map<String, String> credentials) {
        if (credentials == null) return null;
        AuthorizationType authType = super.getAuthorizationTypeByCode(token.authTypeCode);
        return (authType != null && authType.isInteractive()) ? credentials.get(authType.getInteractiveFieldCode()) : null;
    }
}
