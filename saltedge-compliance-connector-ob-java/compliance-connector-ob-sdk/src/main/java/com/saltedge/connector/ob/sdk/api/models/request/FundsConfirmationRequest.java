/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2021 Salt Edge.
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
package com.saltedge.connector.ob.sdk.api.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.SDKConstants;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAmount;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class FundsConfirmationRequest extends PrioraBaseRequest {

    /**
     * TPP application name.
     */
    @JsonProperty(SDKConstants.KEY_APP_NAME)
    @NotBlank
    public String tppAppName;

    /**
     * Human readable Provider identifier.
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_PROVIDER_CODE)
    public String providerCode;

    /**
     * Unique reference, as assigned by the TPP, to unambiguously refer to the request related to the fund confirmation request.
     */
    @NotBlank
    @JsonProperty("reference")
    public String reference;

    /**
     * Unique reference, as assigned by the TPP, to unambiguously refer to the request related to the fund confirmation request.
     */
    @NotNull
    @JsonProperty(SDKConstants.KEY_INSTRUCTED_AMOUNT)
    public ObAmount instructedAmount;
}
