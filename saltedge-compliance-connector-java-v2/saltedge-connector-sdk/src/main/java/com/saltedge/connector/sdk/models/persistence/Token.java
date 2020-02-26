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
package com.saltedge.connector.sdk.models.persistence;

import com.saltedge.connector.sdk.config.ApplicationProperties;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.provider.models.ProviderOfferedConsents;
import com.saltedge.connector.sdk.tools.ConsentDataConverter;
import com.saltedge.connector.sdk.tools.KeyTools;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Database entity for saving data about connection between Connector and Salt Edge Compliance Solution
 */
@Entity
public class Token extends BaseEntity implements Serializable {
    @Column(name = Constants.KEY_SESSION_SECRET, nullable = false)
    public String sessionSecret;

    @Column(name = "provider_offered_consents")
    @Convert(converter = ConsentDataConverter.class)
    public ProviderOfferedConsents providerOfferedConsents;

    @Column(name = Constants.KEY_STATUS, nullable = false)
    public Status status = Status.UNCONFIRMED;

    @Column(name = "access_token")
    public String accessToken;

    @Column(name = "expires_at")
    public Date tokenExpiresAt;

    @Column(name = Constants.KEY_USER_ID)
    public String userId;

    @Column(name = Constants.KEY_CONFIRMATION_CODE)
    public String confirmationCode;

    @Column(name = "auth_type_code")
    public String authTypeCode;

    @Column(name = "tpp_name")
    @Size(min = 1, max = 4096)
    public String tppName;

    @Column(name = "tpp_redirect_url")
    public String tppRedirectUrl;

    public Token() {
    }

    public Token(String userId) {
        this.userId = userId;
    }

    public Token(String sessionSecret, String tppAppName, String authTypeCode, String tppRedirectUrl) {
        this.sessionSecret = sessionSecret;
        this.tppName = tppAppName;
        this.authTypeCode = authTypeCode;
        this.tppRedirectUrl = tppRedirectUrl;
    }

    public LocalDateTime getTokenExpiresAt() {
        if (tokenExpiresAt == null) return null;
        return Instant.ofEpochMilli(tokenExpiresAt.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) {
        if (tokenExpiresAt == null) this.tokenExpiresAt = null;
        else this.tokenExpiresAt = Date.from(tokenExpiresAt.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void initConfirmedToken() {
        status = Status.CONFIRMED;
        regenerateTokenAndExpiresAt();
    }

    public void regenerateTokenAndExpiresAt() {
        accessToken = KeyTools.generateToken(32);
        setTokenExpiresAt(LocalDateTime.now().plusMinutes(ApplicationProperties.connectionExpiresInMinutes));
    }

    public boolean isExpired() {
        LocalDateTime dateTime = getTokenExpiresAt();
        return dateTime == null || dateTime.isBefore(LocalDateTime.now());
    }

    public enum Status {
        UNCONFIRMED, CONFIRMED, REVOKED
    }
}
