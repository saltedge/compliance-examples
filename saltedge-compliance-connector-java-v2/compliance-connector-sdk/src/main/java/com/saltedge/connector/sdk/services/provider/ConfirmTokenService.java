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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
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

  public AisToken confirmAisToken(
      @NotEmpty String sessionSecret,
      @NotEmpty String userId,
      @NotEmpty String accessToken,
      ProviderConsents providerOfferedConsents
  ) {
    AisToken token = findAisTokenBySessionSecret(sessionSecret);
    if (token == null) return null;

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
      CompletableFuture<ErrorResponse> result = sendAisSessionSuccess(token);

      if (result == null || result.get() == null) {
        updateAisStatusSafe(token, ConsentStatus.CONFIRMED);
      } else {
        updateAisStatusSafe(token, ConsentStatus.FAILED);
      }
    } catch (Exception e) {
      log.error("confirmAisToken: ", e);
      updateAisStatusSafe(token, ConsentStatus.FAILED);
      callbackService.sendFailCallback(token.sessionSecret, e);
    }
    return token;
  }

  public PiisToken confirmPiisToken(
      @NotEmpty String sessionSecret,
      @NotEmpty String userId,
      @NotEmpty String accessToken
  ) {
    PiisToken token = piisTokensRepository.findFirstBySessionSecret(sessionSecret);
    if (token != null) {
      try {
        token.userId = userId;
        token.accessToken = accessToken;
        piisTokensRepository.save(token);

        // Sending callback to SE side
        sendPiisSessionSuccess(token);

        token.status = ConsentStatus.CONFIRMED;
        piisTokensRepository.save(token);
      } catch (Exception e) {
        log.error("confirmPiisToken: ", e);
        callbackService.sendFailCallback(token.sessionSecret, e);
      }
    }
    return token;
  }

  private CompletableFuture<ErrorResponse> sendAisSessionSuccess(AisToken token) {
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

  private void updateAisStatusSafe(AisToken token, ConsentStatus status) {
    try {
      if (token.status == status) return;

      token.status = status;
      aisTokensRepository.save(token);
    } catch (Exception e) {
      log.error("updateAisStatusSafe:", e);
    }
  }

  private void sendPiisSessionSuccess(PiisToken token) {
    try {
      SessionSuccessCallbackRequest params = new SessionSuccessCallbackRequest();
      params.token = token.accessToken;
      params.userId = token.userId;
      callbackService.sendSuccessCallback(token.sessionSecret, params);
    } catch (Exception ignored) {

    }
  }
}
