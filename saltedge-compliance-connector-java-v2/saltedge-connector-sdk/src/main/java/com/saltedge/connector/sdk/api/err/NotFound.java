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
package com.saltedge.connector.sdk.api.err;

import org.springframework.http.HttpStatus;

/**
 * Set of NotFound errors
 */
public abstract class NotFound extends RuntimeException implements HttpErrorParams {
    @Override
    public HttpStatus getErrorStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorClass() {
        return getClass().getSimpleName();
    }

    public static class UserNotFound extends NotFound {
        @Override
        public String getErrorMessage() {
            return "User not found.";
        }
    }

    public static class TokenNotFound extends NotFound {
        @Override
        public String getErrorMessage() {
            return "Token not found.";
        }
    }

    public static class AccountNotFound extends NotFound {
        @Override
        public String getErrorMessage() {
            return "Account not found.";
        }
    }

    public static class PaymentNotFound extends NotFound {
        @Override
        public String getErrorMessage() {
            return "Payment not found.";
        }
    }

    public static class PaymentNotCreated extends NotFound {
        @Override
        public String getErrorMessage() {
            return "Payment not created.";
        }
    }

    public static class PaymentNotConfirmed extends NotFound {
        @Override
        public String getErrorMessage() {
            return "Payment not confirmed.";
        }
    }

    public static class PaymentNotCanceled extends NotFound {
        @Override
        public String getErrorMessage() {
            return "Payment not canceled.";
        }
    }
}
