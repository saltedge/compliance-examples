/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2022 Salt Edge.
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

import jakarta.validation.constraints.NotBlank;

import static com.saltedge.connector.sdk.SDKConstants.KEY_APP_NAME;
import static com.saltedge.connector.sdk.SDKConstants.KEY_REDIRECT_URL;

@JsonIgnoreProperties
public class CreateTokenRequest extends PrioraBaseRequest {
    /**
     * Human readable Provider identifier.
     */
    @JsonProperty(SDKConstants.KEY_PROVIDER_CODE)
    @NotBlank
    public String providerCode;

    /**
     * TPP application name.
     */
    @JsonProperty(KEY_APP_NAME)
    @NotBlank
    public String tppAppName;

    /**
     * Specifies authorization type that was used for token creation.
     */
    @JsonProperty("authorization_type")
    @NotBlank
    public String authorizationType;

    /**
     * The URL that the customer will be redirected to after he finishes the authentication process on provider’s side.
     */
    @JsonProperty(KEY_REDIRECT_URL)
    @NotBlank
    public String redirectUrl;
}
