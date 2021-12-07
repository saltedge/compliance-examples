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
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAddress;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * Data provided by TPP, used to specify additional details for risk scoring for Payments.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class ObRiskData {
  /**
   * Specifies the payment context.
   */
  @JsonProperty("payment_context_code")
  public String paymentContextCode;

  /**
   * Category code conform to ISO 18245, related to the type of services or goods the merchant provides for the transaction.
   */
  @JsonProperty("merchant_category_code")
  public String merchantCategoryCode;

  /**
   * The unique customer identifier of the PSU with the merchant.
   */
  @JsonProperty("merchant_customer_identification")
  public String merchant_customer_identification;

  /**
   * Information that locates and identifies a specific address, as defined by postal services or in free format text.
   */
  @JsonProperty("delivery_address")
  public ObAddress deliveryAddress;

  public ObRiskData() {
  }

  public ObRiskData(String paymentContextCode) {
    this.paymentContextCode = paymentContextCode;
  }

  public ObRiskData(String paymentContextCode, String merchantCategoryCode) {
    this.paymentContextCode = paymentContextCode;
    this.merchantCategoryCode = merchantCategoryCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ObRiskData that = (ObRiskData) o;
    return Objects.equals(paymentContextCode, that.paymentContextCode) && Objects.equals(merchantCategoryCode, that.merchantCategoryCode) && Objects.equals(merchant_customer_identification, that.merchant_customer_identification) && Objects.equals(deliveryAddress, that.deliveryAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentContextCode, merchantCategoryCode, merchant_customer_identification, deliveryAddress);
  }
}
