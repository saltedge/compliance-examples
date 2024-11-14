/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2022 Salt Edge.
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
package com.saltedge.connector.sdk.services.provider;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.api.models.responses.ErrorResponse;
import com.saltedge.connector.sdk.callback.mapping.SessionSuccessCallbackRequest;
import com.saltedge.connector.sdk.models.ConsentStatus;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

/**
 * Service designated for consent confirmation.
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Validated
public class ConfirmTokenService extends BaseService {
  private static final Logger log = LoggerFactory.getLogger(ConfirmTokenService.class);

  // Return tppRedirectUrl url
  public String confirmAisToken(
      @NotEmpty String sessionSecret,
      @NotEmpty String userId,
      @NotEmpty String accessToken,
      ProviderConsents providerOfferedConsents
  ) {
    AisToken token = findAisTokenBySessionSecret(sessionSecret);
    if (token == null || token.id == null) {
      return null;
    } else {
      confirmAisTokenAsync(token.id, userId, accessToken, providerOfferedConsents);
      return token.tppRedirectUrl;
    }
  }

  // Return tppRedirectUrl url
  public String confirmPiisToken(
      @NotEmpty String sessionSecret,
      @NotEmpty String userId,
      @NotEmpty String accessToken
  ) {
    PiisToken token = piisTokensRepository.findFirstBySessionSecret(sessionSecret);
    if (token == null) {
      return null;
    } else {
      confirmPiisTokenAsync(token.id, userId, accessToken);
      return token.tppRedirectUrl;
    }
  }

  @Async
  private void confirmAisTokenAsync(
          Long consentId,
          @NotEmpty String userId,
          @NotEmpty String accessToken,
          ProviderConsents providerOfferedConsents
  ) {
    AisToken token = aisTokensRepository.findById(consentId).orElseThrow();
    try {
      token.userId = userId;
      token.status = ConsentStatus.CONFIRMED;
      token.accessToken = accessToken;
      if (token.tokenExpiresAt == null) {
        token.tokenExpiresAt = Instant.now().plus(SDKConstants.CONSENT_MAX_PERIOD, ChronoUnit.DAYS);
      }
      if (token.notGlobalConsent()) {
        token.providerOfferedConsents = (providerOfferedConsents == null) ? ProviderConsents.buildAllAccountsConsent() : providerOfferedConsents;
      }
      aisTokensRepository.save(token);

      // Sending callback to SE side
      ErrorResponse result = sendAisSessionSuccess(token);
      ConsentStatus status = (result == null) ? ConsentStatus.CONFIRMED : ConsentStatus.FAILED;
      updateAisStatusSafe(token, status);
    } catch (Exception e) {
      log.error("confirmAisTokenAsync: ", e);
      updateAisStatusSafe(token, ConsentStatus.FAILED);
      callbackService.sendFailCallbackAsync(token.sessionSecret, e);
    }
  }

  @Async
  private void confirmPiisTokenAsync(Long consentId, @NotEmpty String userId, @NotEmpty String accessToken) {
    PiisToken token = piisTokensRepository.findById(consentId).orElseThrow();
    try {
      token.userId = userId;
      token.accessToken = accessToken;
      piisTokensRepository.save(token);

      // Sending callback to SE side
      ErrorResponse result = sendPiisSessionSuccess(token);
      ConsentStatus status = (result == null) ? ConsentStatus.CONFIRMED : ConsentStatus.FAILED;
      updatePiisStatusSafe(token, status);
    } catch (Exception e) {
      log.error("confirmPiisToken: ", e);
      updatePiisStatusSafe(token, ConsentStatus.FAILED);
      callbackService.sendFailCallbackAsync(token.sessionSecret, e);
    }
  }

  private @Nullable ErrorResponse sendAisSessionSuccess(AisToken token) {
    try {
      SessionSuccessCallbackRequest params = new SessionSuccessCallbackRequest();
      params.providerOfferedConsents = token.providerOfferedConsents;
      params.token = token.accessToken;
      params.userId = token.userId;
      return callbackService.sendSuccessCallback(token.sessionSecret, params);
    } catch (Exception ignored) {
      return null;
    }
  }

  private @Nullable ErrorResponse sendPiisSessionSuccess(PiisToken token) {
    try {
      SessionSuccessCallbackRequest params = new SessionSuccessCallbackRequest();
      params.token = token.accessToken;
      params.userId = token.userId;
      return callbackService.sendSuccessCallback(token.sessionSecret, params);
    } catch (Exception ignored) {
      return null;
    }
  }

  private void updateAisStatusSafe(AisToken token, ConsentStatus status) {
    try {
      if (token.status == status) return;

      token.status = status;
      aisTokensRepository.save(token);
    } catch (Exception e) {
      log.error("updateAisStatusSafe:", e);
    }
  }

  private void updatePiisStatusSafe(PiisToken token, ConsentStatus status) {
    try {
      if (token.status == status) return;

      token.status = status;
      piisTokensRepository.save(token);
    } catch (Exception e) {
      log.error("updatePiisStatusSafe:", e);
    }
  }
}
