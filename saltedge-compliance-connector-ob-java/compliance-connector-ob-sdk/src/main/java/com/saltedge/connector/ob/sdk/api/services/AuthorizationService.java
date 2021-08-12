/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2021 Salt Edge.
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
package com.saltedge.connector.ob.sdk.api.services;

import com.saltedge.connector.ob.sdk.api.models.request.AuthorizationCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.AuthorizationUpdateRequest;
import com.saltedge.connector.ob.sdk.api.models.response.AuthorizationsCreateResponse;
import com.saltedge.connector.ob.sdk.api.models.response.AuthorizationsUpdateResponse;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Service is responsible for handling Authorization flow
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuthorizationService extends BaseService {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    /**
     *
     * @param authorizeUrl
     * @param authCode
     * @param authCodeExp
     * @return redirectUri or null
     */
    public String createAuthorization(@NotEmpty String authorizeUrl, @NotEmpty String authCode, Instant authCodeExp) {
        try {
            AuthorizationsCreateResponse response = callbackService.createAuthorization(
              new AuthorizationCreateRequest(authorizeUrl, authCode, authCodeExp)
            );
            if (StringUtils.hasText(response.data.redirectUri)) {
                return response.data.redirectUri;
            } else {
                Consent consent = consentsRepository.findFirstByConsentId(response.data.consentId);
                consent.authCode = authCode;
                consent.accessToken = response.data.accessToken;
                consent.authorizationId = response.data.authorizationId;
                consentsRepository.save(consent);
            }
        } catch (Exception e) {
            log.error("AuthorizationService.createAuthorization:", e);
        }
        return null;
    }

    /**
     *
     * @param authCode
     * @param userId
     * @param operationStatus
     * @param accountIdentifiers
     * @return redirectUri or null
     */
    public String updateAuthorization(
      @NotEmpty String authCode,
      @NotEmpty String userId,
      @NotEmpty String operationStatus,
      List<String> accountIdentifiers
    ) {
        try {
            Consent consent = consentsRepository.findFirstByAuthCode(authCode);
            consent.userId = userId;
            consent.accountIdentifiers = accountIdentifiers;

            AuthorizationsUpdateResponse authorizationsUpdateResult = callbackService.updateAuthorization(
              consent.authorizationId,
              new AuthorizationUpdateRequest(userId, operationStatus, accountIdentifiers)
            );

            consent.status = authorizationsUpdateResult.data.status;
            consentsRepository.save(consent);
            return authorizationsUpdateResult.data.redirectUri;
        } catch (Exception e) {
            log.error("AuthorizationService.updateAuthorization:", e);
        }
        return null;
    }
}
