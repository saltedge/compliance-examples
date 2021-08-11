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
package com.saltedge.connector.ob.sdk.provider;

import com.saltedge.connector.ob.sdk.api.services.AuthorizationService;
import com.saltedge.connector.ob.sdk.api.services.PaymentService;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.model.jpa.ConsentsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.List;

/**
 * SDK service with actions for .//TODO
 *
 */
@Service
@Validated
public class ConnectorSDKService {
  private static final Logger log = LoggerFactory.getLogger(ConnectorSDKService.class);
  @Autowired
  private ConsentsRepository consentsRepository;
  @Autowired
  private AuthorizationService authorizationService;
  @Autowired
  private PaymentService paymentService;

  /**
   *
   * @param authorizeUrl
   * @param authCode
   * @param authCodeExp
   * @return error redirect URL or null
   */
  public String onUserInitiateConsentAuthorization(@NotEmpty String authorizeUrl, @NotEmpty String authCode, Instant authCodeExp) {
    return authorizationService.createAuthorization(authorizeUrl, authCode, authCodeExp);
  }

  public Consent getConsent(String authCode) {
    return consentsRepository.findFirstByAuthCode(authCode);
  }

  /**
   * Provider notify Connector SDK Module about oAuth success authentication
   * and provides user consent for accounts (balances/transactions)
   *
   * @return returnUrl string for final redirection of Authorization session (in browser) back to TPP side.
   */
  public String onConsentApprove(@NotEmpty String authCode, @NotEmpty String userId) {
    return authorizationService.updateAuthorization(authCode, userId, "approved", null);
  }

  public String onConsentApprove(@NotEmpty String authCode, @NotEmpty String userId, List<String> accountIdentifiers) {
    return authorizationService.updateAuthorization(authCode, userId, "approved", accountIdentifiers);
  }

  public String onConsentDeny(@NotEmpty String authCode, @NotEmpty String userId) {
    return authorizationService.updateAuthorization(authCode, userId, "denied", null);
  }

  /**
   * Update the status of just created payment
   *
   * @param paymentId unique identifier of payment
   */
  public void onPaymentStatusUpdate(@NotEmpty String paymentId, @NotEmpty String status) {
    paymentService.updatePayment(paymentId, status);
  }
}
