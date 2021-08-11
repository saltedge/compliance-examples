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

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class PaymentUpdateRequest {

    /**
     * PSU identifier on Connector side. Used to map PSU resource on Salt Edge PSD2 Compliance side to Connector one
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_USER_ID)
    public String consentId;

    /**
     * Conveys current status of the operation.
     * Allowed values: approved, denied
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_STATUS)
    public String status;

    /**
     * TPP application name.
     */
    @NotBlank
    @JsonProperty(SDKConstants.KEY_APP_NAME)
    public String tppAppName;

    public PaymentUpdateRequest() {
    }

    public PaymentUpdateRequest(String consentId, String status, String tppAppName) {
        this.consentId = consentId;
        this.status = status;
        this.tppAppName = tppAppName;
    }
}