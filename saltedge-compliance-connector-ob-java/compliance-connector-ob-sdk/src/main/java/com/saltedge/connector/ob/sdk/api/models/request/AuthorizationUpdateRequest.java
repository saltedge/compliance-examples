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
package com.saltedge.connector.ob.sdk.api.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.SDKConstants;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccountIdentifier;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class AuthorizationUpdateRequest {

    /**
     * PSU identifier on Connector side. Used to map PSU resource on Salt Edge PSD2 Compliance side to Connector one
     */
    @JsonProperty(SDKConstants.KEY_USER_ID)
    @NotBlank
    public String userId;

    /**
     * Conveys current status of the operation.
     * Allowed values: approved, denied
     */
    @JsonProperty(SDKConstants.KEY_STATUS)
    @NotBlank
    public String status;

    /**
     * Array containing the list of payments identifiers (optional).
     */
    @JsonProperty("access")
    List<String> accountIdentifiers;

    /**
     * Unambiguous identification of the account of the debtor, in the case of a credit transaction.
     */
    @JsonProperty("debtor_account")
    public ObAccountIdentifier debtorAccount;

    public AuthorizationUpdateRequest() {
    }

    public AuthorizationUpdateRequest(@NotBlank String userId, @NotBlank String status, List<String> accountIdentifiers) {
        this.userId = userId;
        this.status = status;
        this.accountIdentifiers = accountIdentifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationUpdateRequest that = (AuthorizationUpdateRequest) o;
        return Objects.equals(userId, that.userId) && Objects.equals(status, that.status) && Objects.equals(accountIdentifiers, that.accountIdentifiers) && Objects.equals(debtorAccount, that.debtorAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, status, accountIdentifiers, debtorAccount);
    }
}