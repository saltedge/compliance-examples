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
package com.saltedge.connector.sdk.api.mapping;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.SDKConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

public class TransactionsRequest extends PrioraBaseRequest {
    @NotEmpty
    @JsonProperty(SDKConstants.KEY_ACCOUNT_ID)
    public String accountId;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("from_date")
    public Instant fromDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("to_date")
    public Instant toDate;

    public TransactionsRequest() {
    }

    public TransactionsRequest(String accountId, Instant fromDate, Instant toDate, String sessionSecret) {
        this.accountId = accountId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.sessionSecret = sessionSecret;
    }
}
