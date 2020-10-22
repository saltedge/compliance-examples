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
package com.saltedge.connector.sdk.api.models.err;

import org.springframework.http.HttpStatus;

import java.util.Objects;

/**
 * Set of BadRequest (400) errors
 */
public abstract class BadRequest extends RuntimeException implements HttpErrorParams {
    @Override
    public HttpStatus getErrorStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorClass() {
        return getClass().getSimpleName();
    }

    /* BadRequest successors  */

    public static class WrongRequestFormat extends BadRequest {
        private String errorMessage;

        public WrongRequestFormat(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static class JWTExpiredSignature extends BadRequest {
        @Override
        public String getErrorMessage() {
            return "JWT Expired Signature.";
        }
    }

    public static class JWTDecodeError extends BadRequest {
        private String errorMessage;

        public JWTDecodeError(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static class AccessTokenMissing extends BadRequest {
        @Override
        public String getErrorMessage() {
            return "Access Token is missing.";
        }
    }

    public static class InvalidAuthorizationType extends BadRequest {
        @Override
        public String getErrorMessage() {
            return "Invalid authorization type.";
        }
    }

    public static class InvalidAttributeValue extends BadRequest {
        private String valueName = "";

        public InvalidAttributeValue(String valueName) {
            this.valueName = valueName;
        }

        @Override
        public String getErrorMessage() {
            String errorMessage = "Invalid Attribute Value";
            return errorMessage + ": " + valueName;
        }
    }

    public static class InvalidConfirmationCode extends BadRequest {
        @Override
        public String getErrorMessage() {
            return "Invalid confirmation code.";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BadRequest that = (BadRequest) o;
        return Objects.equals(getErrorStatus(), that.getErrorStatus()) &&
                Objects.equals(getErrorClass(), that.getErrorClass()) &&
                Objects.equals(getErrorMessage(), that.getErrorMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getErrorStatus(), getErrorClass(), getErrorMessage());
    }
}
