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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.SDKConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import static com.saltedge.connector.sdk.SDKConstants.KEY_END_TO_END_ID;

/**
 * Transaction data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    /**
     * Transaction identifier on Provider side
     */
    @JsonProperty(SDKConstants.KEY_ID)
    @NotBlank
    private String id;

    /**
     * Transaction amount
     */
    @JsonProperty(SDKConstants.KEY_AMOUNT)
    @NotBlank
    private String amount;

    /**
     * Transaction currency code in ISO 4217
     */
    @JsonProperty(SDKConstants.KEY_CURRENCY)
    @NotBlank
    private String currencyCode;

    /**
     * Transaction status.
     * Allowed values: booked, pending
     */
    @JsonProperty(SDKConstants.KEY_STATUS)
    @NotBlank
    private String status;

    @JsonProperty(KEY_END_TO_END_ID)
    public String endToEndId;

    /**
     * The Date at which assets become available to the account owner in case of a credit.
     */
    @JsonProperty("value_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate valueDate;

    /**
     * The Date when an entry is posted to an account on the ASPSPs books.
     */
    @JsonProperty("booking_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    /**
     * Creditor details.
     */
    @JsonProperty("creditor_details")
    private ParticipantDetails creditorDetails;

    /**
     * Debtor details.
     */
    @JsonProperty("debtor_details")
    private ParticipantDetails debtorDetails;

    /**
     * List of currency exchange
     */
    @JsonProperty("currency_exchange")
    private List<CurrencyExchange> currencyExchange;

    /**
     * Reference as contained in the unstructured and structured remittance reference structure.
     */
    @JsonProperty("remittance_information")
    private TransactionRemittanceInformation remittanceInformation;

    /**
     * Additional information deemed relevant to a payment
     */
    @JsonProperty(SDKConstants.KEY_EXTRA)
    private TransactionExtra extra;

    public Transaction() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public ParticipantDetails getCreditorDetails() {
        return creditorDetails;
    }

    public void setCreditorDetails(ParticipantDetails creditorDetails) {
        this.creditorDetails = creditorDetails;
    }

    public ParticipantDetails getDebtorDetails() {
        return debtorDetails;
    }

    public void setDebtorDetails(ParticipantDetails debtorDetails) {
        this.debtorDetails = debtorDetails;
    }

    public List<CurrencyExchange> getCurrencyExchange() {
        return currencyExchange;
    }

    public void setCurrencyExchange(List<CurrencyExchange> currencyExchange) {
        this.currencyExchange = currencyExchange;
    }

    public TransactionExtra getExtra() {
        return extra;
    }

    public void setExtra(TransactionExtra extra) {
        this.extra = extra;
    }

    public TransactionRemittanceInformation getRemittanceInformation() {
        return remittanceInformation;
    }

    public void setRemittanceInformation(TransactionRemittanceInformation remittanceInformation) {
        this.remittanceInformation = remittanceInformation;
    }
}
