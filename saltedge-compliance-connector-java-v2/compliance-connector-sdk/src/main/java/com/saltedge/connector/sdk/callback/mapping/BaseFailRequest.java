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
package com.saltedge.connector.sdk.callback.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.SDKConstants;

/**
 * Base Fail callback request model
 * <a href="https://priora.saltedge.com/docs/aspsp/v2/ais#salt-edge-endpoints-sessions-sessions-fail">...</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseFailRequest extends BaseCallbackRequest {
    /**
     * Class of failure.
     */
    @JsonProperty(SDKConstants.KEY_ERROR_CLASS)
    public String errorClass;

    /**
     * Conveys the reason of failure in human-readable text.
     */
    @JsonProperty(SDKConstants.KEY_ERROR_MESSAGE)
    public String errorMessage;

    /**
     * PSU identifier on Connector side. Used to map PSU resource on Salt Edge PSD2 Compliance side to Connector one.
     */
    @JsonProperty(SDKConstants.KEY_USER_ID)
    public String userId;

    /**
     * Conveys current status of the operation.
     * Allowed values: RJCT, CANC
     */
    @JsonProperty(SDKConstants.KEY_STATUS)
    public String status;

    public BaseFailRequest() { }

    public BaseFailRequest(String errorClass, String errorMessage) {
        this.errorClass = errorClass;
        this.errorMessage = errorMessage;
    }

    public BaseFailRequest(String errorClass, String errorMessage, String userId) {
        this.errorClass = errorClass;
        this.errorMessage = errorMessage;
        this.userId = userId;
    }
}
