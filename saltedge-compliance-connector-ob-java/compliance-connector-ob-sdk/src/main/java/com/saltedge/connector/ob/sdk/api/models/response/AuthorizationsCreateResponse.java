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
package com.saltedge.connector.ob.sdk.api.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.SDKConstants;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationsCreateResponse {
    @JsonProperty(SDKConstants.KEY_DATA)
    public Data data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Data {
        @JsonProperty(SDKConstants.KEY_AUTHORIZATION_ID)
        @NotEmpty
        public String authorizationId;

        @JsonProperty(SDKConstants.KEY_CONSENT_ID)
        @NotEmpty
        public String consentId;

        @JsonProperty(SDKConstants.KEY_ACCESS_TOKEN)
        @NotEmpty
        public String accessToken;

        @JsonProperty(SDKConstants.KEY_REDIRECT_URI)
        public String redirectUri;

        public Data() {
        }

        public Data(String authorizationId, String consentId, String accessToken) {
            this.authorizationId = authorizationId;
            this.consentId = consentId;
            this.accessToken = accessToken;
        }
    }
}
