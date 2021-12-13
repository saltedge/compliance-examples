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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.SDKConstants;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObAuthorizationType;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObRiskData;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObScaData;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * @see <a href="https://priora.saltedge.com/docs/aspsp/ob/pis#connector-endpoints-payments-payments-consent">Payment Consents Endpoints</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class PaymentConsentsCreateRequest extends CreateBaseRequest {

    /**
     * Specifies the type of payment associated with a preregistered template.
     * Allowed values: domestic_payment, international_payment
     */
    @NotBlank
    @JsonProperty("payment_type")
    public String paymentType;

    /**
     * Specifies the status of account resource in code form.
     * (e.g. AwaitingAuthorisation)
     */
    @NotBlank
    @JsonProperty("status")
    public String status;

    /**
     * Date and time at which the resource was created.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("creation_date_time")
    public Instant creationDateTime;

    /**
     * The Initiation payload is sent by the initiating party to the ASPSP.
     */
    @JsonProperty(SDKConstants.KEY_INITIATION)
    @NotNull
    @Valid
    public ObPaymentInitiationData paymentInitiation;

    /**
     * Date and time at which the resource status was updated.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("status_update_date_time")
    public Instant statusUpdateDateTime;

    /**
     * Type of authorisation flow requested.
     */
    @JsonProperty("authorisation")
    public ObAuthorizationType authorisation;

    /**
     * Supporting Data provided by TPP, when requesting SCA Exemption.
     */
    @JsonProperty("sca_support_data")
    public ObScaData scaData;

    /**
     * Data provided by TPP, used to specify additional details for risk scoring for Payments.
     */
    @JsonProperty("risk")
    public ObRiskData risk;
}
