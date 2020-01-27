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
package com.saltedge.connector.sdk.provider.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.config.Constants;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Transaction data
 */
public class TransactionData {
    /**
     * Transaction identifier on Provider side
     */
    @JsonProperty(Constants.KEY_ID)
    public String id;

    /**
     * Identifier of account to which transaction belongs
     */
    @JsonProperty(Constants.KEY_ACCOUNT_ID)
    public String accountId;

    /**
     * Transaction amount
     */
    @JsonProperty(Constants.KEY_AMOUNT)
    public String amount;

    /**
     * Transaction currency code in ISO 4217
     */
    @JsonProperty(Constants.KEY_CURRENCY_CODE)
    public String currencyCode;

    /**
     * Transaction description
     */
    @JsonProperty(Constants.KEY_DESCRIPTION)
    public String description;

    /**
     * Date on which transaction was initialized in ISO 8601
     */
    @JsonProperty("made_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public Date madeOn;

    /**
     * Transaction status.
     * Allowed values: posted, pending
     */
    @JsonProperty(Constants.KEY_STATUS)
    public String status;

    /**
     * List of all fees applied to given payment
     */
    @JsonProperty(Constants.KEY_FEES)
    public List<FeeData> fees;

    /**
     * Any additional information deemed relevant to a payment (e.g. MCC code)
     */
    @JsonProperty(Constants.KEY_EXTRA)
    public Map<String, String> extra;

    public TransactionData() { }

    public TransactionData(@NotNull String id,
                           @NotNull String accountId,
                           @NotNull String amount,
                           @NotNull String currencyCode,
                           @NotNull String description,
                           @NotNull Date madeOn,
                           @NotNull String status,
                           @NotNull List<FeeData> fees,
                           @NotNull Map<String, String> extra) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.description = description;
        this.madeOn = madeOn;
        this.status = status;
        this.fees = fees;
        this.extra = extra;
    }
}
