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
package com.saltedge.connector.sdk.models.domain;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.models.*;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Database entity for saving data about connection between Connector and Salt Edge Compliance Solution
 */
@Entity(name="PiisToken")
public class PiisToken extends BaseEntity implements Serializable {
  @Column(name = SDKConstants.KEY_SESSION_SECRET, nullable = false, length = 1024)
  public String sessionSecret;

  @Column(name = "account", length = 16384)
  @Convert(converter = ParticipantAccountConverter.class)
  public ParticipantAccount account;

  @Column(name = "card", length = 16384)
  @Convert(converter = CardDataConverter.class)
  public CardData card;

  @Column(name = SDKConstants.KEY_STATUS, nullable = false)
  public ConsentStatus status = ConsentStatus.UNCONFIRMED;

  @Column(name = "access_token")
  public String accessToken;

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

  public PiisToken() {
  }

  public PiisToken(String userId) {
    this.userId = userId;
  }

  public PiisToken(
      @NotEmpty String sessionSecret,
      @NotEmpty String tppAppName,
      String authTypeCode,
      String tppRedirectUrl,
      ParticipantAccount account,
      CardData card
  ) {
    this.sessionSecret = sessionSecret;
    this.tppName = tppAppName;
    this.authTypeCode = authTypeCode;
    this.tppRedirectUrl = tppRedirectUrl;
    this.account = account;
    this.card = card;
  }

  public boolean isRevoked() {
    return status == ConsentStatus.REVOKED;
  }
}
