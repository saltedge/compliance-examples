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

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.err.BadRequest;
import com.saltedge.connector.sdk.api.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.err.NotFound;
import com.saltedge.connector.sdk.api.mapping.CreateTokenRequest;
import com.saltedge.connector.sdk.callback.mapping.SessionUpdateCallbackRequest;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.AuthMode;
import com.saltedge.connector.sdk.provider.models.AuthorizationType;
import com.saltedge.connector.sdk.tools.UrlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service is responsible for implementing authentication and authorization of Customer.
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateTokenService extends TokensBaseService {
    private static Logger log = LoggerFactory.getLogger(CreateTokenService.class);

    @Async
    public void startAuthorization(CreateTokenRequest params) {
        try {
            AuthorizationType type = super.getAuthorizationTypeByCode(params.authorizationType);
            if (type == null) throw new BadRequest.InvalidAuthorizationType();
            else {
                Token token = createToken(type, params);
                if (AuthMode.OAUTH == type.mode) {
                    oAuthAuthorize(token);
                } else {
                    throw new BadRequest.InvalidAuthorizationType();//TODO remove when embedded type will be supported by Priora Compliance
//                    embeddedAuthorize(...);
                }
            }
        } catch (Exception e) {
            log.error("AuthorizeTokenService.startAuthorization:", e);
            RuntimeException failException = (e instanceof HttpErrorParams) ? (RuntimeException) e : new NotFound.AccountNotFound();
            callbackService.sendFailCallback(params.sessionSecret, failException);
        }
    }

    private void oAuthAuthorize(Token token) {
        tokensRepository.save(token);
        SessionUpdateCallbackRequest params = new SessionUpdateCallbackRequest(
                providerService.getAccountInformationAuthorizationPageUrl(token.sessionSecret),
                SDKConstants.STATUS_REDIRECT
        );
        callbackService.sendUpdateCallback(token.sessionSecret, params);
    }

    private void embeddedAuthorize() throws RuntimeException {
        //TODO Implement when embedded type will be supported by Priora Compliance
    }

    private Token createToken(AuthorizationType authType, CreateTokenRequest request) {
        return new Token(
                request.sessionSecret,
                request.tppAppName,
                authType.code,
                request.redirectUrl
        );
    }
}
