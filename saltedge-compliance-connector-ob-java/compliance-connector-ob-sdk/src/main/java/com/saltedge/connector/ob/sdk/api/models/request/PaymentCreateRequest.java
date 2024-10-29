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
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObRiskData;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @see <a href="https://priora.saltedge.com/docs/aspsp/ob/pis#connector-endpoints-payments-payments-payment">Payment Endpoints</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class PaymentCreateRequest extends CreateBaseRequest {

    /**
     * Payment order identifier on Connector side. Used to map Salt Edge PSD2 Compliance payments to Connector ones.
     */
    @JsonProperty(SDKConstants.KEY_PAYMENT_ID)
    @NotBlank
    public String compliancePaymentId;

    /**
     * Specifies the type of payment associated with a preregistered template.
     * Allowed values: domestic_payment, international_payment
     */
    @JsonProperty("payment_type")
    @NotBlank
    public String paymentType;

    /**
     * The Initiation payload is sent by the initiating party to the ASPSP.
     */
    @JsonProperty(SDKConstants.KEY_INITIATION)
    @NotNull
    @Valid
    public ObPaymentInitiationData paymentInitiation;

    /**
     * Data provided by TPP, used to specify additional details for risk scoring for Payments.
     */
    @JsonProperty("risk")
    @NotNull
    public ObRiskData risk;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentCreateRequest that = (PaymentCreateRequest) o;
        return Objects.equals(compliancePaymentId, that.compliancePaymentId) && Objects.equals(paymentType, that.paymentType) && Objects.equals(paymentInitiation, that.paymentInitiation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compliancePaymentId, paymentType, paymentInitiation);
    }
}
