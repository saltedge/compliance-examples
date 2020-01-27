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

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Account information
 */
public class AccountData {
    /**
     * Account identifier on Provider
     */
    @JsonProperty(Constants.KEY_ID)
    public String id;

    /**
     * Amount available on account
     */
    @JsonProperty("available_amount")
    public double availableAmount;

    /**
     * Account balance
     */
    @JsonProperty("balance")
    public double balance;

    /**
     * The maximum amount of credit available on account
     */
    @JsonProperty("credit_limit")
    public double creditLimit;

    /**
     * Account’s currency in ISO 4217
     */
    @JsonProperty(Constants.KEY_CURRENCY_CODE)
    public String currencyCode;

    /**
     * Human readable account name
     */
    @JsonProperty("name")
    public String name;

    /**
     * Type of account.
     * Allowed values: account, debit_card, credit_card, checking, savings, investment, bonus, loan, credit, insurance, ewallet, mortgage, card
     */
    @JsonProperty("nature")
    public String nature;

    /**
     * International Bank Account Number
     */
    @JsonProperty("iban")
    public String iban;

    /**
     * Account number within Provider’s ecosystem
     */
    @JsonProperty("number")
    public String number;

    /**
     * Business Identifier Code
     */
    @JsonProperty("swift_code")
    public String swiftCode;

    /**
     * Bank codes used in British and Irish banking systems
     */
    @JsonProperty("sort_code")
    public String sortCode;

    /**
     * Flag representing whether account can be used to initiate payments
     */
    @JsonProperty("payment_account")
    public Boolean isPaymentAccount;

    /**
     * Conveys current status of the account.
     * Default value: active
     * Allowed values: active, inactive
     */
    @JsonProperty(Constants.KEY_STATUS)
    public String status;

    /**
     * Any additional information deemed relevant to a account
     */
    @JsonProperty(Constants.KEY_EXTRA)
    public Map<String, String> extra;

    public AccountData(@NotNull String id,
                       @NotNull double availableAmount,
                       @NotNull double balance,
                       @NotNull double creditLimit,
                       @NotNull String currencyCode,
                       @NotNull String iban,
                       @NotNull String name,
                       @NotNull String nature,
                       @NotNull String number,
                       @NotNull Boolean isPaymentAccount,
                       @NotNull String sortCode,
                       @NotNull String status,
                       @NotNull String swiftCode,
                       @NotNull Map<String, String> extra) {
        this.id = id;
        this.availableAmount = availableAmount;
        this.balance = balance;
        this.creditLimit = creditLimit;
        this.currencyCode = currencyCode;
        this.iban = iban;
        this.name = name;
        this.nature = nature;
        this.number = number;
        this.isPaymentAccount = isPaymentAccount;
        this.sortCode = sortCode;
        this.status = status;
        this.swiftCode = swiftCode;
        this.extra = extra;
    }

    public String getId() {
        return id;
    }

    public double getAvailableAmount() {
        return availableAmount;
    }

    public double getBalance() {
        return balance;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getName() {
        return name;
    }

    public String getNature() {
        return nature;
    }

    public String getIban() {
        return iban;
    }

    public String getNumber() {
        return number;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public Boolean isPaymentAccount() {
        return isPaymentAccount;
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    /**
     * Checks if `iban` or `number` or `sortCode` or `swiftCode` has provided code
     * @param code unique account code (can be iban or number or sortCode or swiftCode)
     * @return true if contains required code
     */
    public boolean hasAccountCode(@NotNull String code) {
        if (code.isEmpty()) return false;
        return code.equals(iban) || code.equals(number) || code.equals(sortCode) || code.equals(swiftCode);
    }
}
