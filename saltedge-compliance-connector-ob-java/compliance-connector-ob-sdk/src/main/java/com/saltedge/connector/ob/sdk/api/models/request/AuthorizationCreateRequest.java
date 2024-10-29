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

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class AuthorizationCreateRequest {

  /**
   * Authorize URL for TPP redirection.
   */
  @JsonProperty("authorize_url")
  @NotEmpty
  public String authorizeUrl;

  /**
   * Authorization code.
   * Allowed length: min: 16 characters
   */
  @JsonProperty(SDKConstants.KEY_AUTH_CODE)
  @NotEmpty
  @Size(min = 16)
  public String authCode;

  /**
   * Authorization code expiration datetime.
   * Default value: 1 minute from now
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @JsonProperty("auth_code_exp")
  public Instant authCodeExp;

  public AuthorizationCreateRequest(@NotEmpty String authorizeUrl, @Size(min = 16) String authCode) {
    this.authorizeUrl = authorizeUrl;
    this.authCode = authCode;
  }

  public AuthorizationCreateRequest(@NotEmpty String authorizeUrl, @Size(min = 16) String authCode, Instant authCodeExp) {
    this.authorizeUrl = authorizeUrl;
    this.authCode = authCode;
    this.authCodeExp = authCodeExp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthorizationCreateRequest that = (AuthorizationCreateRequest) o;
    return Objects.equals(authorizeUrl, that.authorizeUrl) && Objects.equals(authCode, that.authCode) && Objects.equals(authCodeExp, that.authCodeExp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authorizeUrl, authCode, authCodeExp);
  }
}
