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

/**
 * Creditor details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantDetails {
    /**
     * Wrapper of creditor's account data
     */
    @JsonProperty("account")
    public Account account;

    public ParticipantDetails() {
    }

    public ParticipantDetails(Account account) {
        this.account = account;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Account {
        /**
         * Basic Bank Account Number
         */
        @JsonProperty("bban")
        public String bban;

        /**
         * Account currency code.
         */
        @JsonProperty(Constants.KEY_CURRENCY)
        public String currencyCode;

        /**
         * International Bank Account Number
         */
        @JsonProperty(Constants.KEY_IBAN)
        public String iban;

        /**
         * Primary Account Number (PAN) of a card in a masked form.
         */
        @JsonProperty("masked_pan")
        public String maskedPan;

        /**
         * A number uniquely identifying a subscription
         */
        @JsonProperty("msisdn")
        public String msisdn;

        /**
         * Primary Account Number (PAN) of a card, can be tokenized by the ASPSP due to PCI DSS requirements.
         */
        @JsonProperty("pan")
        public String pan;

        /**
         * Creditor name.
         */
        @JsonProperty(Constants.KEY_NAME)
        public String name;

        public Account() {
        }

        public Account(String iban, String name) {
            this.iban = iban;
            this.name = name;
        }

        public Account(String bban, String currencyCode, String iban, String maskedPan, String msisdn, String pan, String name) {
            this.bban = bban;
            this.currencyCode = currencyCode;
            this.iban = iban;
            this.maskedPan = maskedPan;
            this.msisdn = msisdn;
            this.pan = pan;
            this.name = name;
        }
    }
}
