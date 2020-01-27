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
import com.saltedge.connector.sdk.callback.mapping.InteractiveStep;
import com.saltedge.connector.sdk.callback.mapping.PaymentCallbackRequest;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.PaymentTemplate;
import com.saltedge.connector.sdk.tools.UrlTools;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreatePaymentService extends PaymentsBaseService {
    private static Logger log = LoggerFactory.getLogger(CreatePaymentService.class);

    @Async
    public void createPayment(
            @NotNull Token token,
            String sessionSecret,
            Long prioraPaymentId,
            String paymentType,
            String redirectUrl,
            Map<String, String> paymentAttributes,
            Map<String, String> extra
    ) {
        try {
            PaymentTemplate template = findTemplateByCodeOrThrowError(token.userId, paymentType);
            String paymentId = providerApi.createPayment(
                    token.userId,
                    template,
                    paymentAttributes,
                    updateExtraData(extra, sessionSecret, prioraPaymentId, redirectUrl)
            );

            if (providerApi.isPaymentConfirmed(paymentId)) {
                super.sendSuccessCallback(sessionSecret, paymentId, prioraPaymentId);
            } else if (isOAuthAuthorizationType(token.authTypeCode)) {
                callbackService.sendUpdateCallback(sessionSecret, createOAuthCallbackParams(paymentId, prioraPaymentId));
            } else if (template.isInteractive()) {
                callbackService.sendUpdateCallback(sessionSecret, createInteractiveEmbeddedCallbackParams(paymentId, prioraPaymentId, template));
            } else {
                callbackService.sendUpdateCallback(sessionSecret, createEmbeddedCallbackParams(paymentId, prioraPaymentId));
            }
        } catch (Exception e) {
            log.error("CreatePaymentService.createPayment:", e);
            RuntimeException failException = (e instanceof HttpErrorParams) ? (RuntimeException) e : new NotFound.PaymentNotCreated();
            super.sendFailCallback(sessionSecret, null, prioraPaymentId, failException);
        }
    }

    private PaymentTemplate findTemplateByCodeOrThrowError(@NotNull String userId, @NotNull String paymentType) throws RuntimeException {
        PaymentTemplate template = providerApi.getPaymentTemplateByCode(userId, paymentType);
        if (template == null) throw new BadRequest.InvalidPaymentType();
        else return template;
    }

    private PaymentCallbackRequest createOAuthCallbackParams(String paymentId, Long prioraPaymentId) {
        PaymentCallbackRequest result = new PaymentCallbackRequest(Constants.STATUS_REDIRECT, paymentId, prioraPaymentId);
        result.extra = new PaymentCallbackRequest.Extra(createRedirectUrl(paymentId));
        return result;
    }

    private PaymentCallbackRequest createEmbeddedCallbackParams(String paymentId, Long prioraPaymentId) {
        return new PaymentCallbackRequest(Constants.STATUS_WAITING_CONFIRMATION_CODE, paymentId, prioraPaymentId);
    }

    private PaymentCallbackRequest createInteractiveEmbeddedCallbackParams(
            String paymentId,
            Long prioraPaymentId,
            PaymentTemplate template
    ) {
        PaymentCallbackRequest result = new PaymentCallbackRequest(
                Constants.STATUS_WAITING_CONFIRMATION_CODE,
                String.valueOf(paymentId),
                prioraPaymentId
        );
        result.interactiveStep = new InteractiveStep(
                template.interactiveInstruction,
                template.interactiveField.code
        );
        return result;
    }

    private Map<String, String> updateExtraData(
            Map<String, String> extra,
            String sessionSecret,
            Long prioraPaymentId,
            String redirectUrl
    ) {
        HashMap<String, String> result = (extra == null) ? new HashMap<>() : new HashMap<>(extra);
        result.put(Constants.KEY_PRIORA_PAYMENT_ID, String.valueOf(prioraPaymentId));
        if (!StringUtils.isEmpty(redirectUrl)) {
            result.put(Constants.KEY_REDIRECT_URL, redirectUrl);
            result.put(Constants.KEY_SESSION_SECRET, sessionSecret);
        }
        return result;
    }

    private String createRedirectUrl(String paymentId) {
        String id = (paymentId == null) ? "" : paymentId;
        return UrlTools.appendParam(providerApi.getPaymentConfirmationPageUrl(id), Constants.KEY_PAYMENT_ID, id);
    }
}
