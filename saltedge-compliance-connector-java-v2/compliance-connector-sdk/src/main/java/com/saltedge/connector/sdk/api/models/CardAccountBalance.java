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

/**
 * Account's balance information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardAccountBalance {
    /**
     * ExternalBalanceType1code from ISO 20022 (https://www.iso20022.org/).
     * Allowed values: closingBooked, expected, openingBooked, interimAvailable, forwardAvailable, interimBooked, openingAvailable, previouslyClosedBooked, closingAvailable, information
     */
    @JsonProperty("balance_type")
    public String type;

    /**
     * Wrapper for balance amount.
     */
    @JsonProperty("balance_amount")
    public Amount amount;

    public CardAccountBalance() {
    }

    public CardAccountBalance(String amount, String currency, String type) {
        this.amount = new Amount(amount, currency);
        this.type = type;
    }
}
