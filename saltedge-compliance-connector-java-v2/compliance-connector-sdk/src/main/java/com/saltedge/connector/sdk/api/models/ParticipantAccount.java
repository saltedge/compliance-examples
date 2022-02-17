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
import com.saltedge.connector.sdk.SDKConstants;
import org.springframework.util.StringUtils;

/**
 * Creditor/Debtor account information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantAccount {
    /**
     * Account currency code.
     */
    @JsonProperty(SDKConstants.KEY_CURRENCY)
    public String currencyCode;

    /**
     * Basic Bank Account Number
     */
    @JsonProperty(SDKConstants.KEY_BBAN)
    public String bban;

    /**
     * International Bank Account Number
     */
    @JsonProperty(SDKConstants.KEY_IBAN)
    public String iban;

    /**
     * Number code, which is used by British and Irish banks.
     */
    @JsonProperty(SDKConstants.KEY_SORT_CODE)
    private String sortCode;

    /**
     * International Bank Identifier Code
     */
    @JsonProperty(SDKConstants.KEY_BIC)
    public String bic;

    /**
     * Primary Account Number (PAN) of a card in a masked form.
     */
    @JsonProperty(SDKConstants.KEY_MASKED_PAN)
    public String maskedPan;

    /**
     * A number uniquely identifying a subscription
     */
    @JsonProperty(SDKConstants.KEY_MSISDN)
    public String msisdn;

    /**
     * Check if one of identifiers is not empty
     *
     * @return true if model contains at least one identifier
     */
    public boolean hasIdentifier() {
        return StringUtils.hasText(bban)
            || StringUtils.hasText(bic)
            || StringUtils.hasText(iban)
            || StringUtils.hasText(sortCode)
            || StringUtils.hasText(maskedPan)
            || StringUtils.hasText(msisdn);
    }

    public ParticipantAccount() {
    }

    public ParticipantAccount(String bban, String currencyCode, String iban, String maskedPan, String msisdn) {
        this.bban = bban;
        this.currencyCode = currencyCode;
        this.iban = iban;
        this.maskedPan = maskedPan;
        this.msisdn = msisdn;
    }

    public static ParticipantAccount createWithIbanAndName(String iban) {
        ParticipantAccount result = new ParticipantAccount();
        result.iban = iban;
        return result;
    }

    public static ParticipantAccount createWithIbanAndCurrency(String iban, String currencyCode) {
        ParticipantAccount result = new ParticipantAccount();
        result.iban = iban;
        result.currencyCode = currencyCode;
        return result;
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

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }
}
