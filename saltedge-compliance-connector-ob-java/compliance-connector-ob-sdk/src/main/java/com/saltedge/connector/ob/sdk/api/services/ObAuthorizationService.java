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

import com.saltedge.connector.ob.sdk.api.models.errors.NotFound;
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
import java.util.List;

/**
 * Service is responsible for handling Authorization flow
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ObAuthorizationService extends ObBaseService {
    private static final Logger log = LoggerFactory.getLogger(ObAuthorizationService.class);

    /**
     * Initiate Authorization creation on user redirect to authorization url (e.g. OpenID authentication page)
     *
     * @param authorizeUrl request url enriched with custom query (from authorization controller).
     * @param authCode random authorization session code. Minimal 16 characters.
     * @param authCodeExp authorization session code expiration datetime (optional).
     * @return error redirect URL or null, to return TPP back.
     * @throws com.saltedge.connector.ob.sdk.api.models.errors.NotFound.ConsentNotFound if consent model cannot be found
     */
    public String createAuthorization(@NotEmpty String authorizeUrl, @NotEmpty String authCode, Instant authCodeExp) throws NotFound.ConsentNotFound {
        AuthorizationsCreateResponse response = null;
        try {
            response = callbackService.createAuthorization(
              new AuthorizationCreateRequest(authorizeUrl, authCode, authCodeExp)
            );
        } catch (Exception e) {
            log.error("AuthorizationService.createAuthorization:", e);
        }
        if (response == null || response.data == null) {
            log.error("ObAuthorizationService.createAuthorization response is empty");
            throw new NotFound.ConsentNotFound("Consent with empty ID cannot be found");
        } else if (StringUtils.hasText(response.data.redirectUri)) {
            return response.data.redirectUri;
        } else {
            Consent consent = consentsRepository.findFirstByConsentId(response.data.consentId);
            if (consent == null) throw new NotFound.ConsentNotFound("Consent with ID:" + response.data.consentId + " not found");
            else {
                consent.authCode = authCode;
                consent.accessToken = response.data.accessToken;
                consent.authorizationId = response.data.authorizationId;
                consentsRepository.save(consent);
            }
        }
        return null;
    }

    /**
     * Update consent authorization status. Can be approved or denied.
     * Send authorization info to Salt Edge Compliance Service.
     *
     * @param authCode random authorization session code. Minimal 16 characters.
     * @param userId unique identifier of authorized user.
     * @param operationStatus current status of the operation. Allowed values: approved, denied.
     * @param accountIdentifiers collection of account identifiers selected by user on consent confirmation (optional).
     * @return final redirect URL or null, to return TPP back.
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
