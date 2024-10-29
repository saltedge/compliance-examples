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

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationsUpdateResponse {
    @JsonProperty(SDKConstants.KEY_DATA)
    @NotNull
    public Data data;

    public AuthorizationsUpdateResponse() {}

    public AuthorizationsUpdateResponse(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AuthorizationsUpdateResponse{" +
          "data=" + data +
          '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationsUpdateResponse that = (AuthorizationsUpdateResponse) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Data {
        @JsonProperty(SDKConstants.KEY_AUTHORIZATION_ID)
        @NotEmpty
        public String authorizationId;

        @JsonProperty(SDKConstants.KEY_STATUS)
        @NotEmpty
        public String status;

        @JsonProperty(SDKConstants.KEY_REDIRECT_URI)
        @NotEmpty
        public String redirectUri;

        public Data() {
        }

        public Data(String authorizationId, String status) {
            this.authorizationId = authorizationId;
            this.status = status;
        }

        @Override
        public String toString() {
            return "Data{" +
                "authorizationId='" + authorizationId + '\'' +
                ", status='" + status + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return Objects.equals(authorizationId, data.authorizationId) && Objects.equals(status, data.status) && Objects.equals(redirectUri, data.redirectUri);
        }

        @Override
        public int hashCode() {
            return Objects.hash(authorizationId, status, redirectUri);
        }
    }
}
