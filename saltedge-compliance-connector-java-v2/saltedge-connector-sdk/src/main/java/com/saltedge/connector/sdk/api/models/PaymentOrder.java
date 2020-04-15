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
package com.saltedge.connector.sdk.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.api.models.validation.RequestAccountConstraint;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.saltedge.connector.sdk.SDKConstants.KEY_END_TO_END_IDENTIFICATION;
import static com.saltedge.connector.sdk.SDKConstants.KEY_INSTRUCTED_AMOUNT;

/**
 * Payment data.
 */
@JsonIgnoreProperties
public class PaymentOrder {
    /**
     * Creditor data.
     */
    @JsonProperty("creditor_account")
    @RequestAccountConstraint
    public Account creditorAccount;

    /**
     * Creditor full name.
     */
    @JsonProperty("creditor_name")
    @NotEmpty
    public String creditorName;

    /**
     * Debtor data.
     */
    @JsonProperty("debtor_account")
    @RequestAccountConstraint
    public Account debtorAccount;

    /**
     * Amount and currency.
     */
    @JsonProperty(KEY_INSTRUCTED_AMOUNT)
    @NotNull
    @Valid
    public Amount instructedAmount;

    /**
     * Payment identifier on TPP side.
     */
    @JsonProperty(KEY_END_TO_END_IDENTIFICATION)
    public String endToEndIdentification;

    /**
     * Payment description.
     */
    @JsonProperty("remittance_information_unstructured")
    public String remittanceInformationUnstructured;

    public PaymentOrder() {
    }

    public PaymentOrder(
            @NotNull Account creditorAccount,
            @NotEmpty String creditorName,
            @NotNull Account debtorAccount,
            @NotNull Amount instructedAmount,
            String endToEndIdentification,
            String remittanceInformationUnstructured
    ) {
        this.creditorAccount = creditorAccount;
        this.creditorName = creditorName;
        this.debtorAccount = debtorAccount;
        this.instructedAmount = instructedAmount;
        this.endToEndIdentification = endToEndIdentification;
        this.remittanceInformationUnstructured = remittanceInformationUnstructured;
    }
}
