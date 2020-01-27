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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.config.Constants;

import java.util.List;
import java.util.Map;

/**
 * Data relevant to a payment including fees, status and other.
 */
public class PaymentData {
    /**
     * Payment identifier on Provider
     */
    @JsonProperty(Constants.KEY_ID)
    public String id;

    /**
     * Identifier of the corresponding payment on Salt Edge PSD2 Compliance side.
     * Provider should skip it initialization. Will be filled by Connector SDK.
     */
    @JsonProperty(Constants.KEY_PRIORA_PAYMENT_ID)
    public Long prioraPaymentId;

    /**
     * Payment's description.
     */
    @JsonProperty("description")
    public String description;

    /**
     * Payment's status.
     * Allowed values: processing, pending, redirect, waiting_confirmation, waiting_confirmation_code, successful, closed, failed, confirmed, executing
     */
    @JsonProperty("status")
    public String status;

    /**
     * Total amount paid except sum of applied fees amounts
     */
    @JsonProperty("amount")
    public double amount;

    /**
     * Total amount paid
     */
    @JsonProperty("total")
    public double total;

    /**
     * List of all fees applied to given payment
     */
    @JsonProperty(Constants.KEY_FEES)
    public List<FeeData> fees;

    /**
     * All attributes (required and optional) that belong to a payment template which customer fills before creating the payment order.
     */
    @JsonProperty("payment_attributes")
    public Map<String, String> paymentAttributes;

    /**
     * Any additional information deemed relevant to a payment.
     */
    @JsonProperty(Constants.KEY_EXTRA)
    public Map<String, String> extra;

    public PaymentData() {
    }

    public PaymentData(
            String id,
            String description,
            String status,
            double amount,
            double total,
            List<FeeData> fees,
            Map<String, String> paymentAttributes,
            Map<String, String> extra
    ) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.amount = amount;
        this.total = total;
        this.fees = fees;
        this.paymentAttributes = paymentAttributes;
        this.extra = extra;
    }
}
