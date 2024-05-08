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
package com.saltedge.connector.sdk.models.domain;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.models.ConsentDataConverter;
import com.saltedge.connector.sdk.models.ConsentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * Database entity for saving data about connection between Connector and Salt Edge Compliance Solution
 */
@Entity(name = "Token")
public class AisToken extends BaseEntity implements Serializable {
  @Column(name = SDKConstants.KEY_SESSION_SECRET, nullable = false, length = 1024)
  public String sessionSecret;

  @Column(name = "provider_offered_consents", length = 4096)
  @Convert(converter = ConsentDataConverter.class)
  public ProviderConsents providerOfferedConsents;

  @Column(name = SDKConstants.KEY_STATUS, nullable = false)
  public ConsentStatus status = ConsentStatus.UNCONFIRMED;

  @Column(name = "access_token")
  public String accessToken;

  @Column(name = "expires_at")
  public Instant tokenExpiresAt;

  @Column(name = SDKConstants.KEY_USER_ID)
  public String userId;

  @Column(name = SDKConstants.KEY_CONFIRMATION_CODE)
  public String confirmationCode;

  @Column(name = "auth_type_code")
  public String authTypeCode;

  @Column(name = "tpp_name")
  @Size(min = 1, max = 4096)
  public String tppName;

  @Column(name = "tpp_redirect_url", length = 1024)
  public String tppRedirectUrl;

  public AisToken() {
  }

  public AisToken(String userId) {
    this.userId = userId;
  }

  public AisToken(
      @NotEmpty String sessionSecret,
      @NotEmpty String tppAppName,
      String authTypeCode,
      String tppRedirectUrl,
      Instant tokenExpiresAt
  ) {
    this.sessionSecret = sessionSecret;
    this.tppName = tppAppName;
    this.authTypeCode = authTypeCode;
    this.tppRedirectUrl = tppRedirectUrl;
    this.tokenExpiresAt = tokenExpiresAt;
  }

  public boolean isExpired() {
    return tokenExpiresAt == null || tokenExpiresAt.isBefore(Instant.now());
  }

  public boolean notGlobalConsent() {
    return this.providerOfferedConsents == null || !this.providerOfferedConsents.hasGlobalConsent();
  }

  public boolean isRevoked() {
    return status == ConsentStatus.REVOKED;
  }
}
