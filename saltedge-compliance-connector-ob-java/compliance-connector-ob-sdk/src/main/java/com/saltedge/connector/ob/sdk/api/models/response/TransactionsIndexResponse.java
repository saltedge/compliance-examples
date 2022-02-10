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
import com.saltedge.connector.ob.sdk.provider.dto.account.Meta;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObTransaction;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionsIndexResponse {
    @JsonProperty(SDKConstants.KEY_DATA)
    @NotNull
    public List<ObTransaction> data;

    @JsonProperty(SDKConstants.KEY_META)
    public Meta meta;

    public TransactionsIndexResponse() {}

    public TransactionsIndexResponse(List<ObTransaction> data, Meta meta) {
        this.data = data;
        this.meta = meta;
    }
}
