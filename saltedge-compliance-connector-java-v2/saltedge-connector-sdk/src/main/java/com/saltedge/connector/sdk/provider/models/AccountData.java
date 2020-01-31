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

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * Account information
 */
public class AccountData {
    /**
     * Account identifier on Provider
     */
    @NotBlank
    @JsonProperty(Constants.KEY_ID)
    public String id;

    /**
     * Human readable account name
     */
    @NotBlank
    @JsonProperty("name")
    public String name;

    @NotBlank
    @JsonProperty("balances")
    public List<BalanceData> balances;

    /**
     * ExternalCashAccountType1Code from ISO 20022 (https://www.iso20022.org/).
     * Allowed values: CACC, CASH, CISH, COMM, CPAC, LLSV, LOAN, MGLD, MOMA, NREX, ODFT, ONDP, OTHR, SACC, SLRY, SVGS, TAXE, TRAN, TRAS
     */
    @NotBlank
    @JsonProperty("cash_account_type")
    public String cashAccountType;

    /**
     * Currency code of account (ISO 4217).
     */
    @NotBlank
    @JsonProperty(Constants.KEY_CURRENCY)
    public String currencyCode;

    /**
     * Basic Bank Account Number
     */
    @JsonProperty("bban")
    public String bban;

    /**
     * Bank Identifier Code
     */
    @JsonProperty("bic")
    public String bic;

    /**
     * International Bank Account Number
     */
    @JsonProperty("iban")
    public String iban;

    /**
     * A number uniquely identifying a subscription in a Global System for Mobile communications or a Universal Mobile Telecommunications System mobile network.
     */
    @JsonProperty("msisdn")
    public String msisdn;

    /**
     * Product Name of the Bank for this account, proprietary definition.
     */
    @JsonProperty("product")
    public String product;

    /**
     * Current status of the account. Allowed values: enabled, deleted, blocked
     */
    @JsonProperty("status")
    public String status;

    /**
     * Any additional information deemed relevant to a account
     */
    @JsonProperty(Constants.KEY_EXTRA)
    public Map<String, String> extra;

    public AccountData() {
    }

    public AccountData(String id, String name, List<BalanceData> balances, String cashAccountType, String currencyCode) {
        this.id = id;
        this.name = name;
        this.balances = balances;
        this.cashAccountType = cashAccountType;
        this.currencyCode = currencyCode;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<BalanceData> getBalances() {
        return balances;
    }

    public String getCashAccountType() {
        return cashAccountType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getBban() {
        return bban;
    }

    public String getBic() {
        return bic;
    }

    public String getIban() {
        return iban;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getProduct() {
        return product;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, String> getExtra() {
        return extra;
    }
}
