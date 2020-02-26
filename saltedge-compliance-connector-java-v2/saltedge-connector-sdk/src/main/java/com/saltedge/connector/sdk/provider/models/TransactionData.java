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

import java.util.Date;
import java.util.List;

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
     * Transaction amount
     */
    @JsonProperty(Constants.KEY_AMOUNT)
    public String amount;

    /**
     * Transaction currency code in ISO 4217
     */
    @JsonProperty(Constants.KEY_CURRENCY)
    public String currencyCode;

    /**
     * Transaction status.
     * Allowed values: booked, pending
     */
    @JsonProperty(Constants.KEY_STATUS)
    public String status;

    /**
     * The Date at which assets become available to the account owner in case of a credit.
     */
    @JsonProperty("value_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public Date valueDate;

    /**
     * The Date when an entry is posted to an account on the ASPSPs books.
     */
    @JsonProperty("booking_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public Date bookingDate;

    /**
     * Creditor details.
     */
    @JsonProperty("creditor_details")
    public ParticipantDetails creditorDetails;

    /**
     * Debtor details.
     */
    @JsonProperty("debtor_details")
    public ParticipantDetails debtorDetails;

    /**
     * List of currency exchange
     */
    @JsonProperty("currency_exchange")
    public List<CurrencyExchange> currencyExchange;

    /**
     * Reference as contained in the unstructured and structured remittance reference structure.
     */
    @JsonProperty("remittance_information")
    public TransactionRemittanceInformation remittanceInformation;

    /**
     * Additional information deemed relevant to a payment
     */
    @JsonProperty(Constants.KEY_EXTRA)
    public TransactionExtra extra;

    public TransactionData() {
    }

    public TransactionData(String id, String amount, String currencyCode, String status, Date valueDate) {
        this.id = id;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.status = status;
        this.valueDate = valueDate;
    }
}
