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
import com.saltedge.connector.sdk.api.models.AuthMode;
import com.saltedge.connector.sdk.api.models.AuthorizationType;
import com.saltedge.connector.sdk.api.models.err.BadRequest;
import com.saltedge.connector.sdk.api.models.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.api.models.requests.CreateTokenRequest;
import com.saltedge.connector.sdk.callback.mapping.SessionUpdateCallbackRequest;
import com.saltedge.connector.sdk.models.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Service is responsible for implementing authentication and authorization of Customer.
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateTokenService extends TokensBaseService {
    private static final Logger log = LoggerFactory.getLogger(CreateTokenService.class);

    @Async
    public void startAuthorization(CreateTokenRequest params) {
        try {
            AuthorizationType type = getAuthorizationTypeByCode(params.authorizationType);
            if (type == null) throw new BadRequest.InvalidAuthorizationType();
            else {
                Token token = createToken(type, params);
                if (params.requestedConsent.hasGlobalConsent()) {
                    token.providerOfferedConsents = params.requestedConsent;
                }

                if (AuthMode.OAUTH == type.mode) {
                    oAuthAuthorize(token, params.psuIpAddress);
                } else {
                    throw new BadRequest.InvalidAuthorizationType();//TODO remove when embedded type will be supported by Salt Edge PSD2 Compliance
//                    embeddedAuthorize(...);
                }
            }
        } catch (Exception e) {
            log.error("CreateTokenService.startAuthorization:", e);
            if (e instanceof HttpErrorParams) callbackService.sendFailCallback(params.sessionSecret, e);
            else callbackService.sendFailCallback(params.sessionSecret, new NotFound.AccountNotFound());
        }
    }

    private void oAuthAuthorize(Token token, String psuIpAddress) {
        tokensRepository.save(token);
        SessionUpdateCallbackRequest params = new SessionUpdateCallbackRequest(
                providerService.getAccountInformationAuthorizationPageUrl(
                    token.sessionSecret,
                    token.notGlobalConsent(),
                    psuIpAddress
                ),
                SDKConstants.STATUS_RECEIVED,
                false
        );
        callbackService.sendUpdateCallback(token.sessionSecret, params);
    }

    private Token createToken(AuthorizationType authType, CreateTokenRequest request) {
        LocalDate validUntil = request.validUntil;
//        if (validUntil == null) validUntil = LocalDate.now().plus(SDKConstants.CONSENT_MAX_PERIOD, ChronoUnit.DAYS);
        Instant tokenExpiresAt = validUntil.atStartOfDay().toInstant(ZoneOffset.UTC).plus(1, ChronoUnit.DAYS).minusMillis(1);
        return new Token(
                request.sessionSecret,
                request.tppAppName,
                authType.code,
                request.redirectUrl,
                tokenExpiresAt
        );
    }

    private AuthorizationType getAuthorizationTypeByCode(String authTypeCode) {
        if (StringUtils.isEmpty(authTypeCode)) return null;
        return providerService.getAuthorizationTypes().stream()
                .filter(type -> authTypeCode.equals(type.code))
                .findFirst()
                .orElse(null);
    }

    private void embeddedAuthorize() throws RuntimeException {
        //TODO Implement when embedded type will be supported by Salt Edge PSD2 Compliance
    }
}
