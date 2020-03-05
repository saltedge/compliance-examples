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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.Constants;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Card account information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardAccount {
    /**
     * Card account identifier on Provider
     */
    @NotBlank
    @JsonProperty(Constants.KEY_ID)
    private String id;

    /**
     * Human readable card account name
     */
    @NotBlank
    @JsonProperty(Constants.KEY_NAME)
    private String name;

    /**
     * Primary Account Number (PAN) of a card in a masked form.
     */
    @NotBlank
    @JsonProperty(Constants.KEY_MASKED_PAN)
    private String maskedPan;

    /**
     * Currency code of card account (ISO 4217).
     */
    @NotBlank
    @JsonProperty(Constants.KEY_CURRENCY)
    private String currencyCode;

    /**
     * Product Name of the Bank for card account, proprietary definition.
     */
    @NotBlank
    @JsonProperty("product")
    private String product;

    /**
     * Current status of the account. Allowed values: enabled, deleted, blocked
     */
    @NotBlank
    @JsonProperty("status")
    private String status;

    /**
     * List of balances
     */
    @NotBlank
    @JsonProperty("balances")
    private List<CardAccountBalance> balances;

    /**
     * Account's credit limit
     */
    @JsonProperty("credit_limit")
    private Amount creditLimit;

    /**
     * Additional information deemed relevant to a account
     */
    @JsonProperty(Constants.KEY_EXTRA)
    public CardAccountExtra extra;

    public CardAccount() {
    }

    public CardAccount(
            @NotBlank String id,
            @NotBlank String name,
            @NotBlank String maskedPan,
            @NotBlank String currencyCode,
            @NotBlank String product,
            @NotBlank String status,
            List<CardAccountBalance> balances,
            Amount creditLimit,
            CardAccountExtra extra
    ) {
        this.id = id;
        this.name = name;
        this.maskedPan = maskedPan;
        this.currencyCode = currencyCode;
        this.product = product;
        this.status = status;
        this.balances = balances;
        this.creditLimit = creditLimit;
        this.extra = extra;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CardAccountBalance> getBalances() {
        return balances;
    }

    public void setBalances(List<CardAccountBalance> balances) {
        this.balances = balances;
    }

    public Amount getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Amount creditLimit) {
        this.creditLimit = creditLimit;
    }

    public CardAccountExtra getExtra() {
        return extra;
    }

    public void setExtra(CardAccountExtra extra) {
        this.extra = extra;
    }
}
