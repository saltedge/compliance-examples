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
package com.saltedge.connector.ob.sdk.api.services;

import com.saltedge.connector.ob.sdk.api.models.request.PaymentCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.PaymentUpdateRequest;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotEmpty;

/**
 * Perform Payment initiation and update operations.
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public
class ObPaymentService extends ObBaseService {
    private static final Logger log = LoggerFactory.getLogger(ObPaymentService.class);

    /**
     * Start payment initiation.
     * Update Consent object. Send payment initiation to Bank Core.
     * Function is asynchronous.
     *
     * @param consentId unique identifier of consent in SaltEdge Compliance Service.
     * @param params of payment.
     */
    @Async
    public void initiatePayment(@NotNull String consentId, @NotNull PaymentCreateRequest params) {
        try {
            //prepare consent
            Consent consent = consentsRepository.findFirstByConsentId(consentId);
            consent.paymentInitiation = params.paymentInitiation;
            consent.compliancePaymentId = params.compliancePaymentId;
            //request payment initiation
            consent.paymentId = providerService.initiatePayment(
                consent.userId,
                params.paymentType,
                params.paymentInitiation,
                consent.risk
            );
            //save consent
            consentsRepository.save(consent);
        } catch (Exception e) {
            log.error("PaymentCreateService.createPayment:", e);
        }
    }

    /**
     * Update the status of just created payment
     * Send new payment status to Salt Edge Compliance Service.
     * Function is asynchronous.
     *
     * @param paymentId unique payment identifier of Core
     * @param status of payment. Allowed values: Pending, Rejected, AcceptedSettlementInProcess, AcceptedCreditSettlementCompleted
     */
    @Async
    public void updatePayment(@NotEmpty String paymentId, @NotEmpty String status) {
        try {
            Consent consent = consentsRepository.findFirstByPaymentId(paymentId);
            if (consent != null) {
                callbackService.updatePayment(
                    consent.compliancePaymentId,
                    new PaymentUpdateRequest(consent.consentId, status, consent.tppName)
                );
            } else {
                log.error("PaymentService.updatePayment: Consent by paymentId " + paymentId + " not found");
            }
        } catch (Exception e) {
            log.error("PaymentService.updatePayment:", e);
        }
    }
}
