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
package com.saltedge.connector.sdk.api.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.err.HttpErrorParams;

import javax.validation.constraints.NotBlank;

public class ErrorResponse {
    @JsonProperty(SDKConstants.KEY_ERROR_CLASS)
    @NotBlank
    public String errorClass;

    @JsonProperty(SDKConstants.KEY_ERROR_MESSAGE)
    @NotBlank
    public String errorMessage;

    public ErrorResponse() { }

    public ErrorResponse(String errorClass, String errorMessage) {
        this.errorClass = errorClass;
        this.errorMessage = errorMessage;
    }

    public ErrorResponse(Exception ex) {
        errorClass = ex.getClass().getSimpleName();
        errorMessage = ex.getLocalizedMessage();
        if (ex instanceof HttpErrorParams) {
            errorClass = ((HttpErrorParams) ex).getErrorClass();
            errorMessage = ((HttpErrorParams) ex).getErrorMessage();
        }
    }

    public ErrorResponse(HttpErrorParams params) {
        errorClass = params.getErrorClass();
        errorMessage = params.getErrorMessage();
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errorClass='" + errorClass + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
