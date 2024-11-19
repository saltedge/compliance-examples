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
package com.saltedge.connector.sdk.services.priora;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.AuthMode;
import com.saltedge.connector.sdk.api.models.AuthorizationType;
import com.saltedge.connector.sdk.api.models.err.BadRequest;
import com.saltedge.connector.sdk.api.models.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.api.models.requests.CreatePiisTokenRequest;
import com.saltedge.connector.sdk.callback.mapping.SessionUpdateCallbackRequest;
import com.saltedge.connector.sdk.models.CardData;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import com.saltedge.connector.sdk.models.domain.PiisTokensRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service is responsible for implementing authentication and authorization of Customer.
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreatePiisTokenService extends BaseService {
  private static final Logger log = LoggerFactory.getLogger(CreatePiisTokenService.class);

  @Autowired
  protected PiisTokensRepository tokensRepository;

  @Async
  public void startAuthorization(CreatePiisTokenRequest params) {
    try {
      AuthorizationType type = getAuthorizationTypeByCode(params.authorizationType);
      if (type == null) throw new BadRequest.InvalidAuthorizationType();

      if (AuthMode.OAUTH == type.mode) {
        PiisToken token = initToken(type, params);
        tokensRepository.save(token);
        oAuthAuthorize(token);
      } else {
        //TODO replace with embeddedAuthorize() when embedded type will be supported by Salt Edge PSD2 Compliance
        throw new BadRequest.InvalidAuthorizationType();
      }
    } catch (Exception e) {
      log.error("startAuthorization:", e);
      if (e instanceof HttpErrorParams) sessionCallbackService.sendFailCallback(params.sessionSecret, e);
      else sessionCallbackService.sendFailCallback(params.sessionSecret, new NotFound.AccountNotFound());
    }
  }

  private void oAuthAuthorize(PiisToken token) {
    String url = providerService.getFundsConfirmationAuthorizationPageUrl(token.sessionSecret);
    SessionUpdateCallbackRequest params = new SessionUpdateCallbackRequest(url, SDKConstants.STATUS_RECEIVED);
    sessionCallbackService.sendUpdateCallback(token.sessionSecret, params);
  }

  private PiisToken initToken(AuthorizationType authType, CreatePiisTokenRequest requestParams) {
    return new PiisToken(
        requestParams.sessionSecret,
        requestParams.tppAppName,
        authType.code,
        requestParams.redirectUrl,
        requestParams.account,
        new CardData(
            requestParams.cardNumber,
            requestParams.cardExpiryDate,
            requestParams.cardInformation,
            requestParams.registrationInformation
        )
    );
  }

  private AuthorizationType getAuthorizationTypeByCode(String authTypeCode) {
    if (!StringUtils.hasLength(authTypeCode)) return null;
    return providerService.getAuthorizationTypes().stream()
        .filter(type -> authTypeCode.equals(type.code))
        .findFirst()
        .orElse(null);
  }

  private void embeddedAuthorize() throws RuntimeException {
    //TODO Implement when embedded type will be supported by Salt Edge PSD2 Compliance
  }
}
