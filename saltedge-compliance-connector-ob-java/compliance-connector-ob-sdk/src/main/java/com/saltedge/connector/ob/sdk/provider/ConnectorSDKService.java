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

import com.saltedge.connector.ob.sdk.api.services.ObAuthorizationService;
import com.saltedge.connector.ob.sdk.api.services.ObPaymentService;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.model.jpa.ConsentsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * SDK service with actions for callback communications from ASPSP to Compliance Service.
 */
@Lazy
@Service
@Validated
public class ConnectorSDKService {
  private static final Logger log = LoggerFactory.getLogger(ConnectorSDKService.class);
  @Autowired
  private ConsentsRepository consentsRepository;
  @Autowired
  private ObAuthorizationService authorizationService;
  @Autowired
  private ObPaymentService paymentService;

  /**
   * Initiate Authorization creation on user redirect to authorization url (e.g. OpenID authentication page)
   *
   * @param authorizeUrl request url enriched with custom query (from authorization controller).
   * @param authCode random authorization session code. Minimal 16 characters.
   * @param authCodeExp authorization session code expiration datetime (optional).
   * @return error redirect URL or null, to return TPP back.
   */
  public String onUserInitiateConsentAuthorization(@NotEmpty String authorizeUrl, @NotEmpty String authCode, Instant authCodeExp) {
    return authorizationService.createAuthorization(authorizeUrl, authCode, authCodeExp);
  }

  /**
   * Find first Consent object by authCode
   *
   * @param authCode random authorization session code. Minimal 16 characters.
   * @return Consent object or null
   */
  public Consent getConsent(String authCode) {
    return consentsRepository.findFirstByAuthCode(authCode);
  }

  /**
   * Update consent authorization status to Authorized.
   * Send authorization info to Salt Edge Compliance Service.
   *
   * @param authCode random authorization session code. Minimal 16 characters.
   * @param userId unique identifier of authorized user.
   * @return final redirect URL or null, to return TPP back.
   */
  public String onConsentApprove(@NotEmpty String authCode, @NotEmpty String userId) {
    return authorizationService.updateAuthorization(authCode, userId, "approved", null);
  }

  /**
   * Update consent authorization status to Authorized.
   * Send authorization info to Salt Edge Compliance Service.
   *
   * @param authCode random authorization session code. Minimal 16 characters.
   * @param userId unique identifier of authorized user.
   * @param accountIdentifiers collection of account identifiers selected by user on consent confirmation (optional).
   * @return final redirect URL or null, to return TPP back.
   */
  public String onConsentApprove(@NotEmpty String authCode, @NotEmpty String userId, List<String> accountIdentifiers) {
    return authorizationService.updateAuthorization(authCode, userId, "approved", accountIdentifiers);
  }

  /**
   * Update consent authorization status to Rejected.
   * Send authorization info to Salt Edge Compliance Service.
   *
   * @param authCode random authorization session code. Minimal 16 characters.
   * @param userId unique identifier of authorized user.
   * @return final redirect URL or null, to return TPP back.
   */
  public String onConsentDeny(@NotEmpty String authCode, @NotEmpty String userId) {
    return authorizationService.updateAuthorization(authCode, userId, "denied", null);
  }

  /**
   * Update the status of just created payment
   * Send new payment status to Salt Edge Compliance Service.
   *
   * @param paymentId unique payment identifier of Provider
   * @param status of payment. Allowed values: Pending, Rejected, AcceptedSettlementInProcess, AcceptedCreditSettlementCompleted
   */
  public void onPaymentStatusUpdate(@NotEmpty String paymentId, @NotEmpty String status) {
    if (VALID_PAYMENT_STATUSES.contains(status)) {
      paymentService.updatePayment(paymentId, status);
    } else {
      throw new InvalidParameterException("onPaymentStatusUpdate: status " + status + " is invalid.");
    }
  }

  public static final List<String> VALID_PAYMENT_STATUSES = Arrays.asList("Pending", "Rejected", "AcceptedSettlementInProcess", "AcceptedCreditSettlementCompleted");
}
