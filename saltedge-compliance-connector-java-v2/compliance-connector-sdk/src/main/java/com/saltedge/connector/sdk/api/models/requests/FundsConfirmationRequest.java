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
package com.saltedge.connector.sdk.api.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.api.models.Amount;
import com.saltedge.connector.sdk.api.models.validation.RequestAccountConstraint;
import com.saltedge.connector.sdk.models.ParticipantAccount;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.saltedge.connector.sdk.SDKConstants.*;

@JsonIgnoreProperties
public class FundsConfirmationRequest extends PrioraBaseRequest {
    /**
     * Account information
     */
    @JsonProperty(KEY_ACCOUNT)
    @NotNull
    @RequestAccountConstraint
    public ParticipantAccount account;

    /**
     * Amount and currency.
     */
    @JsonProperty(KEY_INSTRUCTED_AMOUNT)
    @NotNull
    @Valid
    public Amount instructedAmount;

    /**
     * Provider identifier.
     */
    @JsonProperty(KEY_PROVIDER_CODE)
    @NotEmpty
    public String providerCode;

    public FundsConfirmationRequest() {
    }

    public FundsConfirmationRequest(String providerCode, ParticipantAccount account, Amount instructedAmount) {
        this.providerCode = providerCode;
        this.account = account;
        this.instructedAmount = instructedAmount;
    }

    public String getAccountIdentifier() {
        if (account != null) {
            if (StringUtils.hasLength(account.getIban())) return account.getIban();
            if (StringUtils.hasLength(account.getBban())) return account.getBban();
            if (StringUtils.hasLength(account.getBic())) return account.getBic();
            if (StringUtils.hasLength(account.getSortCode())) return account.getSortCode();
            if (StringUtils.hasLength(account.getMaskedPan())) return account.getMaskedPan();
            if (StringUtils.hasLength(account.getMsisdn())) return account.getMsisdn();
        }
        return null;
    }

    public ParticipantAccount getAccount() {
        return account;
    }

    public Amount getInstructedAmount() {
        return instructedAmount;
    }

    public String getProviderCode() {
        return providerCode;
    }
}
