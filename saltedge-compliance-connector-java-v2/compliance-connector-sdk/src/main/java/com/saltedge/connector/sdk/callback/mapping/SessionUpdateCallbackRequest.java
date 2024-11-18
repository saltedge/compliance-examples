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

import java.time.Instant;

/**
 * Session Update callback request model
 * <a href="https://priora.saltedge.com/docs/aspsp/v2/ais#salt-edge-endpoints-sessions-sessions-update">...</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionUpdateCallbackRequest extends BaseCallbackRequest {
    @JsonProperty(SDKConstants.KEY_REDIRECT_URL)
    public String redirectUrl;

    @JsonProperty(SDKConstants.KEY_STATUS)
    public String status;

    @JsonProperty("sca_status")
    public String scaStatus;

    @JsonProperty("session_expires_at")
    public Instant sessionExpiresAt;

    @JsonProperty("funds_available")
    public Boolean fundsAvailable;

    public SessionUpdateCallbackRequest(String status) {
        this.status = status;
    }

    public SessionUpdateCallbackRequest(String redirectUrl, String status) {
        this.redirectUrl = redirectUrl;
        this.status = status;
    }

    public SessionUpdateCallbackRequest(Boolean fundsAvailable, String status) {
        this.fundsAvailable = fundsAvailable;
        this.status = status;
    }
}
