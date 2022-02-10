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

import javax.validation.constraints.NotNull;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundsConfirmationResponse {
    @JsonProperty(SDKConstants.KEY_DATA)
    public Data data;

    public FundsConfirmationResponse() {
    }

    public FundsConfirmationResponse(boolean fundsAvailable, String fundsConfirmationId, Instant fundsConfirmationCreatedAt) {
        this.data = new Data(fundsAvailable, fundsConfirmationId, fundsConfirmationCreatedAt);
    }

    public FundsConfirmationResponse(boolean fundsAvailable) {
        this.data = new Data(fundsAvailable);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Data {
        @JsonProperty("funds_available")
        @NotNull
        public Boolean fundsAvailable;

        @JsonProperty("funds_confirmation_id")
        public String fundsConfirmationId;

        @JsonProperty("funds_confirmation_created_at")
        public Instant fundsConfirmationCreatedAt;

        public Data() {
        }

        public Data(boolean fundsAvailable) {
            this.fundsAvailable = fundsAvailable;
        }

        public Data(boolean fundsAvailable, String fundsConfirmationId, Instant fundsConfirmationCreatedAt) {
            this.fundsAvailable = fundsAvailable;
            this.fundsConfirmationId = fundsConfirmationId;
            this.fundsConfirmationCreatedAt = fundsConfirmationCreatedAt;
        }
    }
}
