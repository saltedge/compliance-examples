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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.api.models.response.ErrorResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * @see <a href="https://priora.saltedge.com/docs/aspsp/ob/pis#connector-endpoints-errors">Errors Endpoints</a>
 */
@JsonIgnoreProperties
public class ErrorsRequest extends PrioraBaseRequest {
    @JsonProperty("error")
    @NotNull
    public ErrorResponse error;

    @JsonProperty("request")
    @NotNull
    public RequestData request;

    @JsonIgnoreProperties
    public static class RequestData {
        @JsonProperty("headers")
        @NotBlank
        public Map<String, String> headers;

        @JsonProperty("method")
        @NotBlank
        public String method;

        @JsonProperty("url")
        @NotBlank
        public String url;

        @Override
        public String toString() {
            return "Request{" +
                    "headers=" + headers +
                    ", method='" + method + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ErrorsRequest{" +
                "error=" + error +
                ", request=" + request +
                '}';
    }
}
