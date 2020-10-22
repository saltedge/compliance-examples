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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.SDKConstants;

import java.util.Objects;

@JsonIgnoreProperties
public class ProviderOfferedConsent {
    /**
     * International Bank Account Number
     */
    @JsonProperty(SDKConstants.KEY_IBAN)
    public String iban;

    /**
     * Basic Bank Account Number
     */
    @JsonProperty(SDKConstants.KEY_BBAN)
    public String bban;

    /**
     * Bank Identifier Code
     */
    @JsonProperty(SDKConstants.KEY_BIC)
    public String bic;

    /**
     * A number uniquely identifying a subscription in a Global System for Mobile communications
     * or a Universal Mobile Telecommunications System mobile network.
     */
    @JsonProperty(SDKConstants.KEY_MSISDN)
    public String msisdn;

    /**
     * Primary Account Number (PAN) of a card in a masked form.
     */
    @JsonProperty(SDKConstants.KEY_MASKED_PAN)
    public String maskedPan;

    public ProviderOfferedConsent() {
    }

    public ProviderOfferedConsent(String iban, String bban, String bic, String msisdn, String maskedPan) {
        this.iban = iban;
        this.bban = bban;
        this.bic = bic;
        this.msisdn = msisdn;
        this.maskedPan = maskedPan;
    }

    public static ProviderOfferedConsent createAccountConsent(String iban) {
        ProviderOfferedConsent result = new ProviderOfferedConsent();
        result.iban = iban;
        return result;
    }

    public static ProviderOfferedConsent createCardConsent(String maskedPan) {
        ProviderOfferedConsent result = new ProviderOfferedConsent();
        result.maskedPan = maskedPan;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderOfferedConsent that = (ProviderOfferedConsent) o;
        return Objects.equals(iban, that.iban) &&
                Objects.equals(bban, that.bban) &&
                Objects.equals(bic, that.bic) &&
                Objects.equals(msisdn, that.msisdn) &&
                Objects.equals(maskedPan, that.maskedPan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iban, bban, bic, msisdn, maskedPan);
    }

    @Override
    public String toString() {
        return "Consent{" +
                "iban='" + iban + '\'' +
                ", bban='" + bban + '\'' +
                ", bic='" + bic + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", maskedPan='" + maskedPan + '\'' +
                '}';
    }
}
