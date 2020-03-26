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
package com.saltedge.connector.sdk.api.mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.models.PaymentOrder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties
public class CreatePaymentRequest extends PrioraBaseRequest {
    @JsonProperty("app_name")
    @NotEmpty
    public String appName;

    @JsonProperty("provider_code")
    @NotEmpty
    public String providerCode;

    @JsonProperty(SDKConstants.KEY_REDIRECT_URL)
    @NotEmpty
    public String returnToUrl;

    @JsonProperty("payment")
    @NotNull
    @Valid
    public PaymentOrder paymentOrder;

    public CreatePaymentRequest() {
    }

    public CreatePaymentRequest(@NotEmpty String appName, @NotEmpty String providerCode, @NotEmpty String returnToUrl, @NotNull @Valid PaymentOrder paymentOrder) {
        this.appName = appName;
        this.providerCode = providerCode;
        this.returnToUrl = returnToUrl;
        this.paymentOrder = paymentOrder;
    }
}
