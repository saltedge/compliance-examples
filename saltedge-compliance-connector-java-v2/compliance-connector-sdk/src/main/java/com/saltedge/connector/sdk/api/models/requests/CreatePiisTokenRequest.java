/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2022 Salt Edge.
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
package com.saltedge.connector.sdk.api.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.models.ParticipantAccount;

import jakarta.validation.constraints.NotNull;

import static com.saltedge.connector.sdk.SDKConstants.KEY_ACCOUNT;

/**
 * https://priora.saltedge.com/docs/aspsp/v2/piis#connector-endpoints-funds-confirmations-tokens-tokens
 */
@JsonIgnoreProperties
public class CreatePiisTokenRequest extends CreateTokenRequest {
    /**
     * Requested Account identifier.
     */
    @JsonProperty(KEY_ACCOUNT)
    @NotNull
    public ParticipantAccount account;

    /**
     * Credit card number
     */
    @JsonProperty("card_number")
    public String cardNumber;

    /**
     * Credit card expiration date
     */
    @JsonProperty("card_expiry_date")
    public String cardExpiryDate;

    /**
     * Card extra details
     */
    @JsonProperty("card_information")
    public String cardInformation;

    /**
     * Extra registration details
     */
    @JsonProperty("registration_information")
    public String registrationInformation;
}
