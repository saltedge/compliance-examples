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
package com.saltedge.connector.sdk.api.services.payments;

import com.saltedge.connector.sdk.api.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.services.BaseService;
import com.saltedge.connector.sdk.callback.mapping.PaymentCallbackRequest;
import com.saltedge.connector.sdk.callback.services.PaymentCallbackService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

abstract class PaymentsBaseService extends BaseService {
    @Autowired
    PaymentCallbackService callbackService;

    public void sendSuccessCallback(String sessionSecret, String paymentId, Long prioraPaymentId) {
        PaymentCallbackRequest callbackParams = new PaymentCallbackRequest();
        callbackParams.paymentId = paymentId;
        callbackParams.prioraPaymentId = prioraPaymentId;
        callbackService.sendSuccessCallback(sessionSecret, callbackParams);
    }

    void sendFailCallback(@NotNull String sessionSecret, String paymentId, Long prioraPaymentId, @NotNull RuntimeException exception) {
        PaymentCallbackRequest callbackParams = new PaymentCallbackRequest();
        callbackParams.paymentId = paymentId;
        callbackParams.prioraPaymentId = prioraPaymentId;
        callbackParams.errorClass = ((HttpErrorParams) exception).getErrorClass();
        callbackParams.errorMessage = ((HttpErrorParams) exception).getErrorMessage();
        callbackService.sendFailCallback(sessionSecret, callbackParams);
    }
}
