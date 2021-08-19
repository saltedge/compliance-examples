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
package com.saltedge.connector.ob.sdk.provider.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.SDKConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Account's balance information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class ObBalance {

    /**
     * Amount of balance.
     */
    @NotNull
    @JsonProperty(SDKConstants.KEY_AMOUNT)
    public ObAmount amount;

    /**
     * Indicates whether the balance is a credit or a debit balance.
     * Usage: A zero balance is considered to be a credit balance.
     */
    @NotEmpty
    @JsonProperty("credit_debit_indicator")
    public String creditDebitIndicator;

    /**
     * Name values of ExternalBalanceType1code from ISO 20022.
     */
    @NotEmpty
    @JsonProperty(SDKConstants.KEY_TYPE)
    public String type;

    /**
     * Date of the balance.
     */
    @NotNull
    @JsonProperty("date_time")
    public Instant dateTime;

    /**
     * Details on the credit line.
     */
    @JsonProperty("credit_line")
    public CreditLine creditLine;

    public ObBalance(@NotNull ObAmount amount, @NotEmpty String creditDebitIndicator, @NotEmpty String type, @NotNull Instant dateTime) {
        this.amount = amount;
        this.creditDebitIndicator = creditDebitIndicator;
        this.type = type;
        this.dateTime = dateTime;
    }

    public ObBalance(@NotNull ObAmount amount, @NotEmpty String creditDebitIndicator, @NotEmpty String type, @NotNull Instant dateTime, CreditLine creditLine) {
        this.amount = amount;
        this.creditDebitIndicator = creditDebitIndicator;
        this.type = type;
        this.dateTime = dateTime;
        this.creditLine = creditLine;
    }

    public static class CreditLine {

        /**
         * Indicates whether or not the credit line is included in the balance of the account.
         * Usage: If not present, credit line is not included in the balance amount of the account.
         */
        @NotNull
        @JsonProperty("included")
        public Boolean included;

        /**
         * Amount of money of the credit line
         */
        @NotNull
        @JsonProperty(SDKConstants.KEY_AMOUNT)
        public ObAmount amount;

        /**
         * Limit type, in a coded form.
         */
        @JsonProperty(SDKConstants.KEY_TYPE)
        public String type;

        public CreditLine() {
        }

        public CreditLine(Boolean included) {
            this.included = included;
        }

        public CreditLine(Boolean included, ObAmount amount, String type) {
            this.included = included;
            this.amount = amount;
            this.type = type;
        }
    }
}
