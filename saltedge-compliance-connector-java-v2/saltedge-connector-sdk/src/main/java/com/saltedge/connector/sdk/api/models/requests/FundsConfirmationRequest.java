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
import com.saltedge.connector.sdk.api.models.Account;
import com.saltedge.connector.sdk.api.models.Amount;
import com.saltedge.connector.sdk.api.models.validation.RequestAccountConstraint;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.saltedge.connector.sdk.SDKConstants.KEY_ACCOUNT;
import static com.saltedge.connector.sdk.SDKConstants.KEY_INSTRUCTED_AMOUNT;

@JsonIgnoreProperties
public class FundsConfirmationRequest {
    /**
     * Account information
     */
    @JsonProperty(KEY_ACCOUNT)
    @RequestAccountConstraint
    public Account account;

    /**
     * Amount and currency.
     */
    @JsonProperty(KEY_INSTRUCTED_AMOUNT)
    @NotNull
    @Valid
    public Amount instructedAmount;

    public FundsConfirmationRequest() {
    }

    public FundsConfirmationRequest(Account account, @NotNull @Valid Amount instructedAmount) {
        this.account = account;
        this.instructedAmount = instructedAmount;
    }

    public String getAccountIdentifier() {
        if (account != null) {
            if (!StringUtils.isEmpty(account.getIban())) return account.getIban();
            if (!StringUtils.isEmpty(account.getBban())) return account.getBban();
            if (!StringUtils.isEmpty(account.getBic())) return account.getBic();
        }
        return null;
    }
}
