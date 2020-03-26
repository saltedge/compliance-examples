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
import com.saltedge.connector.sdk.SDKConstants;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * Account information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {
    /**
     * Account identifier on Provider
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_ID)
    private String id;

    /**
     * Human readable account name
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_NAME)
    private String name;

    @NotBlank
    @JsonProperty("balances")
    private List<AccountBalance> balances;

    /**
     * ExternalCashAccountType1Code from ISO 20022 (https://www.iso20022.org/).
     * Allowed values: CACC, CASH, CISH, COMM, CPAC, LLSV, LOAN, MGLD, MOMA, NREX, ODFT, ONDP, OTHR, SACC, SLRY, SVGS, TAXE, TRAN, TRAS
     */
    @NotBlank
    @JsonProperty("cash_account_type")
    private String cashAccountType;

    /**
     * Currency code of account (ISO 4217).
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_CURRENCY)
    private String currencyCode;

    /**
     * Basic Bank Account Number
     */
    @JsonProperty("bban")
    private String bban;

    /**
     * Bank Identifier Code
     */
    @JsonProperty("bic")
    private String bic;

    /**
     * International Bank Account Number
     */
    @JsonProperty(SDKConstants.KEY_IBAN)
    private String iban;

    /**
     * A number uniquely identifying a subscription in a Global System for Mobile communications or a Universal Mobile Telecommunications System mobile network.
     */
    @JsonProperty("msisdn")
    private String msisdn;

    /**
     * Product Name of the Bank for this account, proprietary definition.
     */
    @JsonProperty("product")
    private String product;

    /**
     * Current status of the account. Allowed values: enabled, deleted, blocked
     */
    @JsonProperty("status")
    private String status;

    /**
     * Any additional information deemed relevant to a account
     */
    @JsonProperty(SDKConstants.KEY_EXTRA)
    private Map<String, String> extra;

    public Account() {
    }

    public Account(String id, String name, List<AccountBalance> balances, String cashAccountType, String currencyCode) {
        this.id = id;
        this.name = name;
        this.balances = balances;
        this.cashAccountType = cashAccountType;
        this.currencyCode = currencyCode;
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

    public List<AccountBalance> getBalances() {
        return balances;
    }

    public void setBalances(List<AccountBalance> balances) {
        this.balances = balances;
    }

    public String getCashAccountType() {
        return cashAccountType;
    }

    public void setCashAccountType(String cashAccountType) {
        this.cashAccountType = cashAccountType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getBban() {
        return bban;
    }

    public void setBban(String bban) {
        this.bban = bban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
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

    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }
}
