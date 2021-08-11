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
package com.saltedge.connector.ob.sdk.api.models.errors;

import org.springframework.http.HttpStatus;

import java.util.Objects;

/**
 * Set of Unauthorized errors
 */
public abstract class Unauthorized extends RuntimeException implements HttpErrorParams {
    @Override
    public HttpStatus getErrorStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getErrorClass() {
        return getClass().getSimpleName();
    }

    public static class ConsentExpired extends Unauthorized {
        String value = "";

        public ConsentExpired() {}

        public ConsentExpired(String value) {
            this.value = value;
        }

        @Override
        public String getErrorMessage() {
            return "Consent expired. "  + value;
        }
    }

    public static class ConsentUnauthorized extends Unauthorized {
        @Override
        public String getErrorMessage() {
            return "Unauthorized Consent.";
        }
    }

    public static class InvalidClientId extends Unauthorized {
        @Override
        public String getErrorMessage() {
            return "Invalid Client Id.";
        }
    }

    public static class AccessDenied extends Unauthorized {
        @Override
        public String getErrorMessage() {
            return "Access denied.";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unauthorized that = (Unauthorized) o;
        return Objects.equals(getErrorStatus(), that.getErrorStatus()) &&
                Objects.equals(getErrorClass(), that.getErrorClass()) &&
                Objects.equals(getErrorMessage(), that.getErrorMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getErrorStatus(), getErrorClass(), getErrorMessage());
    }
}
