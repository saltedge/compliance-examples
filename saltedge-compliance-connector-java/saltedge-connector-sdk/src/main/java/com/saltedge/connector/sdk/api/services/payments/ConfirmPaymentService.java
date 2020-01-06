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

import com.saltedge.connector.sdk.api.err.BadRequest;
import com.saltedge.connector.sdk.api.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.err.NotFound;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConfirmPaymentService extends PaymentsBaseService {
    private static Logger log = LoggerFactory.getLogger(ConfirmPaymentService.class);

    @Async
    public void confirmPayment(
            @NotNull String sessionSecret,
            @NotNull String paymentId,
            @NotNull Long prioraPaymentId,
            @NotNull Map<String, String> credentials
    ) {
        try {
            boolean result = providerApi.confirmPayment(paymentId, credentials);
            if (result) super.sendSuccessCallback(sessionSecret, paymentId, prioraPaymentId);
            else throw new BadRequest.InvalidConfirmationCode();
        } catch (Exception e) {
            log.error("ConfirmPaymentService.confirmPayment:", e);
            RuntimeException failException = (e instanceof HttpErrorParams) ? (RuntimeException) e : new NotFound.PaymentNotConfirmed();
            super.sendFailCallback(sessionSecret, paymentId, prioraPaymentId, failException);
        }
    }
}
