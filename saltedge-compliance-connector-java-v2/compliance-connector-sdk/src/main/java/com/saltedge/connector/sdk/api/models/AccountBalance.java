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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Account's balance information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountBalance extends Amount {
    /**
     * ExternalBalanceType1code from ISO 20022 (https://www.iso20022.org/).
     * Allowed values: closingBooked, expected, openingBooked, interimAvailable, forwardAvailable, interimBooked, openingAvailable, previouslyClosedBooked, closingAvailable, information
     */
    @NotBlank
    @JsonProperty("type")
    public String type;

    /**
     * A flag indicating if the credit limit of the corresponding account is included in the calculation of the balance, where applicable.
     */
    @JsonProperty("credit_limit_included")
    public Boolean creditLimitIncluded;

    /**
     * Balance last change time.
     */
    @JsonProperty("last_change_date_time")
    public Instant lastChangeDateTime;

    /**
     * Reference of the last committed transaction to support the TPP in identifying whether all PSU transactions are already known.
     */
    @JsonProperty("last_committed_transaction")
    public String lastCommittedTransaction;

    /**
     * Reference date of the balance.
     */
    @JsonProperty("reference_date")
    public LocalDate referenceDate;

    public AccountBalance(String amount, String currency, String type) {
        this.amount = amount;
        this.currency = currency;
        this.type = type;
    }

    @Override
    public String toString() {
        return "AccountBalance{" +
            "type='" + type + '\'' +
            ", creditLimitIncluded=" + creditLimitIncluded +
            ", lastChangeDateTime=" + lastChangeDateTime +
            ", lastCommittedTransaction='" + lastCommittedTransaction + '\'' +
            ", referenceDate=" + referenceDate +
            ", amount='" + amount + '\'' +
            ", currency='" + currency + '\'' +
            '}';
    }
}
