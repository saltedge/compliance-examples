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
package com.saltedge.connector.ob.sdk.provider.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Supporting Data provided by TPP, when requesting SCA Exemption.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class ObScaData {
  /**
   * Specifies a character string with a maximum length of 40 characters.
   * Usage: This field indicates whether the PSU was subject to SCA performed by the TPP.
   */
  @JsonProperty("applied_authentication_approach")
  public String appliedAuthenticationApproach;

  /**
   * Specifies a character string with a maximum length of 140 characters.
   * Usage: If the payment is recurring then the transaction identifier of the previous payment occurrence so that the ASPSP can verify that the PISP, amount and the payee are the same as the previous occurrence.
   */
  @JsonProperty("reference_payment_order_id")
  public String referencePaymentOrderId;

  /**
   * This field allows a PISP to request specific SCA Exemption for a Payment Initiation.
   */
  @JsonProperty("requested_sca_exemption_type")
  public String requestedScaExemptionType;
}
