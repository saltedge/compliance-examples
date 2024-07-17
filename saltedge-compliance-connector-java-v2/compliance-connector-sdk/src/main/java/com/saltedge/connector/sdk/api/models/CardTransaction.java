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

/**
 * Transaction data of Card Account
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardTransaction {
    /**
     * Transaction identifier on Provider side
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_ID)
    private String id;

    /**
     * Transaction amount
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_AMOUNT)
    private String amount;

    /**
     * Transaction currency code in ISO 4217
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_CURRENCY)
    private String currencyCode;

    /**
     * Transaction status.
     * Allowed values: booked, pending
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_STATUS)
    private String status;

    /**
     * Date of the actual card transaction in ISO 8601: “yyyy-mm-dd” format.
     */
    @NotBlank
    @NotNull
    @JsonProperty("transaction_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    /**
     * Additional details given for the related card transactions.
     */
    @NotNull
    @NotBlank
    @JsonProperty("transaction_details")
    private String transactionDetails;

    /**
     * The Date when an entry is posted to an account on the ASPSPs books in ISO 8601: “yyyy-mm-dd” format.
     */
    @JsonProperty("booking_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    /**
     * Address of the Card Acceptor as given in the related card transaction.
     */
    @JsonProperty("card_acceptor_address")
    private AcceptorAddress cardAcceptorAddress;

    /**
     * Identification of the Card Acceptor (e.g. merchant) as given in the related card transaction.
     */
    @JsonProperty("card_acceptor_id")
    private String cardAcceptorId;

    /**
     * List of currency exchange
     */
    @JsonProperty("currency_exchange")
    private List<CurrencyExchange> currencyExchange;

    /**
     * Flag indicating whether the underlying card transaction is already invoiced.
     */
    @JsonProperty("invoiced")
    private Boolean invoiced;

    /**
     * Any fee related to the transaction in billing currency.
     */
    @JsonProperty("markup_fee")
    private Amount markupFee;

    /**
     * Percentage of the involved transaction fee in relation to the billing amount, e.g. "0.3" for 0,3%.
     */
    @JsonProperty("markup_fee_percentage")
    private String markupFeePercentage;

    /**
     * The masked PAN of the card used in the transaction.
     */
    @JsonProperty("masked_pan")
    private String maskedPan;

    /**
     * The masked PAN of the card used in the transaction.
     */
    @JsonProperty("merchant_category_code")
    private String merchantCategoryCode;

    /**
     * Original amount of the transaction at the Point of Interaction in original currency.
     * The amount of the transaction as billed to the card account.
     */
    @JsonProperty("original_amount")
    private Amount originalAmount;

    /**
     * Proprietary bank transaction code as used within a community or within an ASPSP e.g. for MT94x based transaction reports.
     */
    @JsonProperty("proprietary_bank_transaction_code")
    private String proprietaryBankTransactionCode;

    /**
     * Identification of the Terminal, where the card has been used.
     */
    @JsonProperty("terminal_id")
    private String terminalId;

    public CardTransaction() {
    }

    public CardTransaction(
            @NotBlank String id,
            @NotBlank String amount,
            @NotBlank String currencyCode,
            @NotBlank String status,
            @NotBlank LocalDate transactionDate,
            @NotNull String transactionDetails,
            LocalDate bookingDate,
            AcceptorAddress cardAcceptorAddress,
            String cardAcceptorId,
            List<CurrencyExchange> currencyExchange,
            Boolean invoiced,
            Amount markupFee,
            String markupFeePercentage,
            String maskedPan,
            String merchantCategoryCode,
            Amount originalAmount,
            String proprietaryBankTransactionCode,
            String terminalId
    ) {
        this.id = id;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.status = status;
        this.transactionDate = transactionDate;
        this.transactionDetails = transactionDetails;
        this.bookingDate = bookingDate;
        this.cardAcceptorAddress = cardAcceptorAddress;
        this.cardAcceptorId = cardAcceptorId;
        this.currencyExchange = currencyExchange;
        this.invoiced = invoiced;
        this.markupFee = markupFee;
        this.markupFeePercentage = markupFeePercentage;
        this.maskedPan = maskedPan;
        this.merchantCategoryCode = merchantCategoryCode;
        this.originalAmount = originalAmount;
        this.proprietaryBankTransactionCode = proprietaryBankTransactionCode;
        this.terminalId = terminalId;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AcceptorAddress {
        /**
         * Building number of Card Acceptor.
         */
        @JsonProperty("building_number")
        private String buildingNumber;

        /**
         * City of Card Acceptor.
         */
        @JsonProperty("city")
        private String city;

        /**
         * Country code of Card Acceptor.
         */
        @JsonProperty("country")
        private String country;

        /**
         * Postal code of Card Acceptor.
         */
        @JsonProperty("postal_code")
        private String postalCode;

        /**
         * Street of Card Acceptor.
         */
        @JsonProperty("street")
        private String street;

        public AcceptorAddress() {
        }

        public AcceptorAddress(String buildingNumber, String city, String country, String postalCode, String street) {
            this.buildingNumber = buildingNumber;
            this.city = city;
            this.country = country;
            this.postalCode = postalCode;
            this.street = street;
        }

        public String getBuildingNumber() {
            return buildingNumber;
        }

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getStreet() {
            return street;
        }
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

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(String transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public AcceptorAddress getCardAcceptorAddress() {
        return cardAcceptorAddress;
    }

    public void setCardAcceptorAddress(AcceptorAddress cardAcceptorAddress) {
        this.cardAcceptorAddress = cardAcceptorAddress;
    }

    public String getCardAcceptorId() {
        return cardAcceptorId;
    }

    public void setCardAcceptorId(String cardAcceptorId) {
        this.cardAcceptorId = cardAcceptorId;
    }

    public List<CurrencyExchange> getCurrencyExchange() {
        return currencyExchange;
    }

    public void setCurrencyExchange(List<CurrencyExchange> currencyExchange) {
        this.currencyExchange = currencyExchange;
    }

    public Boolean getInvoiced() {
        return invoiced;
    }

    public void setInvoiced(Boolean invoiced) {
        this.invoiced = invoiced;
    }

    public Amount getMarkupFee() {
        return markupFee;
    }

    public void setMarkupFee(Amount markupFee) {
        this.markupFee = markupFee;
    }

    public String getMarkupFeePercentage() {
        return markupFeePercentage;
    }

    public void setMarkupFeePercentage(String markupFeePercentage) {
        this.markupFeePercentage = markupFeePercentage;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public String getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    public void setMerchantCategoryCode(String merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    public Amount getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Amount originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getProprietaryBankTransactionCode() {
        return proprietaryBankTransactionCode;
    }

    public void setProprietaryBankTransactionCode(String proprietaryBankTransactionCode) {
        this.proprietaryBankTransactionCode = proprietaryBankTransactionCode;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}
