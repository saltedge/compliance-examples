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
package com.saltedge.connector.sdk.api.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.PaymentOrder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import static com.saltedge.connector.sdk.SDKConstants.KEY_APP_NAME;
import static com.saltedge.connector.sdk.SDKConstants.KEY_PROVIDER_CODE;

/**
 * Model of Payment order request from Salt Edge to Connector
 */
@JsonIgnoreProperties
public class CreatePaymentRequest extends PrioraBaseRequest {
  /**
   * TPP application name.
   */
  @JsonProperty(KEY_APP_NAME)
  @NotEmpty
  public String appName;

  /**
   * Provider identifier.
   */
  @JsonProperty(KEY_PROVIDER_CODE)
  @NotEmpty
  public String providerCode;

  /**
   * Ip Address of PSU.
   */
  @JsonProperty("psu_ip_address")
  @NotEmpty
  public String psuIpAddress;

  /**
   * The addressed payment product.
   * Allowed values: sepa-credit-transfers, instant-sepa-credit-transfers, target-2-payments, faster-payment-service, internal-transfer
   */
  @JsonProperty("payment_product")
  @NotEmpty
  public String paymentProduct;

  /**
   * Wrapper for payment data.
   */
  @JsonProperty("payment")
  @NotNull
  @Valid
  public PaymentOrder paymentOrder;

  /**
   * The URL that the PSU will be redirected to after he finishes the authentication process on provider’s side.
   */
  @JsonProperty(SDKConstants.KEY_REDIRECT_URL)
  @NotEmpty
  public String returnToUrl;

  public CreatePaymentRequest() {
  }

  public CreatePaymentRequest(
    @NotEmpty String appName,
    @NotEmpty String providerCode,
    @NotEmpty String returnToUrl,
    @NotNull @Valid PaymentOrder paymentOrder,
    @NotEmpty String paymentProduct,
    @NotEmpty String psuIpAddress
  ) {
    this.appName = appName;
    this.providerCode = providerCode;
    this.paymentProduct = paymentProduct;
    this.paymentOrder = paymentOrder;
    this.returnToUrl = returnToUrl;
    this.psuIpAddress = psuIpAddress;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getProviderCode() {
    return providerCode;
  }

  public void setProviderCode(String providerCode) {
    this.providerCode = providerCode;
  }

  public String getPaymentProduct() {
    return paymentProduct;
  }

  public void setPaymentProduct(String paymentProduct) {
    this.paymentProduct = paymentProduct;
  }

  public PaymentOrder getPaymentOrder() {
    return paymentOrder;
  }

  public void setPaymentOrder(PaymentOrder paymentOrder) {
    this.paymentOrder = paymentOrder;
  }

  public String getReturnToUrl() {
    return returnToUrl;
  }

  public void setReturnToUrl(String returnToUrl) {
    this.returnToUrl = returnToUrl;
  }

  public String getPsuIpAddress() {
    return psuIpAddress;
  }

  public void setPsuIpAddress(String psuIpAddress) {
    this.psuIpAddress = psuIpAddress;
  }
}
