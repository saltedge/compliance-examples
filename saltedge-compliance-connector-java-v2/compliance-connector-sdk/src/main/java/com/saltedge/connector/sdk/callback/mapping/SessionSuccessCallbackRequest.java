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
import com.saltedge.connector.sdk.api.models.ProviderConsents;

import javax.validation.constraints.NotEmpty;

import com.saltedge.connector.sdk.models.ParticipantAccount;
import java.util.Objects;

import static com.saltedge.connector.sdk.SDKConstants.DEBTOR_ACCOUNT;

/**
 * Session Success callback request model
 * <a href="https://priora.saltedge.com/docs/aspsp/v2/ais#salt-edge-endpoints-sessions-sessions-success">...</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionSuccessCallbackRequest extends BaseCallbackRequest {
    /**
     * Consent info confirmed by PSU.
     */
    @JsonProperty("consent")
    public ProviderConsents providerOfferedConsents;

    /**
     * Access token that will be used to access ASPSP data. Token is a unique value which is linked to authenticated user.
     */
    @JsonProperty("token")
    public String token;

    /**
     * PSU identifier on ASPSP side.
     */
    @JsonProperty(SDKConstants.KEY_USER_ID)
    public String userId;

    /**
     * Debtor account data selected by PSU for payment initiation.
     */
    @JsonProperty(DEBTOR_ACCOUNT)
    public ParticipantAccount debtorAccount;

    public static SessionSuccessCallbackRequest successAisCallback(
            @NotEmpty String userId,
            @NotEmpty String accessToken,
            ProviderConsents providerOfferedConsents
    ) {
        SessionSuccessCallbackRequest params = new SessionSuccessCallbackRequest();
        params.providerOfferedConsents = providerOfferedConsents;
        params.token = accessToken;
        params.userId = userId;
        return params;
    }

    public static SessionSuccessCallbackRequest successPiisCallback(
            @NotEmpty String userId,
            @NotEmpty String accessToken
    ) {
        SessionSuccessCallbackRequest params = new SessionSuccessCallbackRequest();
        params.token = accessToken;
        params.userId = userId;
        return params;
    }

    public static SessionSuccessCallbackRequest successPisCallback(
            @NotEmpty String userId,
            ParticipantAccount debtorAccount
    ) {
        SessionSuccessCallbackRequest params = new SessionSuccessCallbackRequest();
        params.debtorAccount = debtorAccount;
        params.userId = userId;
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionSuccessCallbackRequest that = (SessionSuccessCallbackRequest) o;
        return Objects.equals(providerOfferedConsents, that.providerOfferedConsents) &&
                Objects.equals(token, that.token) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerOfferedConsents, token, userId);
    }

    @Override
    public String toString() {
        return "SessionSuccessCallbackRequest{" +
                "providerOfferedConsents=" + providerOfferedConsents +
                ", token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", sessionSecret='" + sessionSecret + '\'' +
                ", extra=" + extra +
                '}';
    }
}
