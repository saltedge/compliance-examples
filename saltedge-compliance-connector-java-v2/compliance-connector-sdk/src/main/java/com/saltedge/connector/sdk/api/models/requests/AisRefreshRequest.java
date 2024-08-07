/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2023 Salt Edge.
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

import jakarta.validation.constraints.NotEmpty;

import static com.saltedge.connector.sdk.SDKConstants.KEY_PROVIDER_CODE;

/**
 * Model of Ais refresh request from Salt Edge to Connector
 */
@JsonIgnoreProperties
public class AisRefreshRequest extends PrioraBaseRequest {
  /**
   * Provider identifier.
   */
  @JsonProperty(KEY_PROVIDER_CODE)
  @NotEmpty
  public String providerCode;

  /**
   * Constructor
   */
  public AisRefreshRequest() {
  }

  public AisRefreshRequest(@NotEmpty String providerCode) {
    this.providerCode = providerCode;
  }

  /**
   * Provider code getter
   */
  public String getProviderCode() {
    return providerCode;
  }

  /**
   * Provider code setter
   */
  public void setProviderCode(String providerCode) {
    this.providerCode = providerCode;
  }
}
