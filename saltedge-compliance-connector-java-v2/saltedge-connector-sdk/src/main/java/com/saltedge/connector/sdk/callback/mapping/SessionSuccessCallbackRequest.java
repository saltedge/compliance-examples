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
package com.saltedge.connector.sdk.callback.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.ProviderOfferedConsents;

import java.time.Instant;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionSuccessCallbackRequest extends BaseCallbackRequest {
    @JsonProperty("consent")
    public ProviderOfferedConsents providerOfferedConsents;

    @JsonProperty("token")
    public String token;

    @JsonProperty("token_expires_at")
    public Instant tokenExpiresAt;

    @JsonProperty(SDKConstants.KEY_USER_ID)
    public String userId;

    @JsonProperty(SDKConstants.KEY_STATUS)
    public String status;

    public SessionSuccessCallbackRequest() {
    }

    public SessionSuccessCallbackRequest(
            ProviderOfferedConsents providerOfferedConsents,
            String token,
            Instant tokenExpiresAt,
            String userId
    ) {
        this.providerOfferedConsents = providerOfferedConsents;
        this.token = token;
        this.tokenExpiresAt = tokenExpiresAt;
        this.userId = userId;
    }

    public SessionSuccessCallbackRequest(String userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionSuccessCallbackRequest that = (SessionSuccessCallbackRequest) o;
        return Objects.equals(providerOfferedConsents, that.providerOfferedConsents) &&
                Objects.equals(token, that.token) &&
                Objects.equals(tokenExpiresAt, that.tokenExpiresAt) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerOfferedConsents, token, tokenExpiresAt, userId);
    }

    @Override
    public String toString() {
        return "SessionSuccessCallbackRequest{" +
                "providerOfferedConsents=" + providerOfferedConsents +
                ", token='" + token + '\'' +
                ", tokenExpiresAt=" + tokenExpiresAt +
                ", userId='" + userId + '\'' +
                ", sessionSecret='" + sessionSecret + '\'' +
                ", extra=" + extra +
                '}';
    }
}
