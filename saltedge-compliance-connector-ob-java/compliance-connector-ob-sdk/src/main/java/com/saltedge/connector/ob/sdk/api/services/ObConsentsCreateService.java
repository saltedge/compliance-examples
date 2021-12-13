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

import com.saltedge.connector.ob.sdk.api.models.request.AccountsConsentsCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.FundsConsentsCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.PaymentConsentsCreateRequest;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Create new Consent object for AIS, PIS or PIIS flow.
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public
class ObConsentsCreateService extends ObBaseService {
    private static final Logger log = LoggerFactory.getLogger(ObConsentsCreateService.class);

    /**
     * Create new Consent object for AIS flow.
     * Function is asynchronous.
     *
     * @param consentsCreateParams params for new Consent
     */
    @Async
    public void createAisConsent(AccountsConsentsCreateRequest consentsCreateParams) {
        try {
            Consent result = Consent.createAisConsent(
              consentsCreateParams.tppAppName,
              consentsCreateParams.consentId,
              "AwaitingAuthorisation",
              consentsCreateParams.permissions,
              consentsCreateParams.expirationDateTime,
              consentsCreateParams.transactionFrom,
              consentsCreateParams.transactionTo
            );
            consentsRepository.save(result);
        } catch (Exception e) {
            log.error("ConsentsCreateService.createAisConsent:", e);
        }
    }

    /**
     * Create new Consent object for PIS flow.
     * Function is asynchronous.
     *
     * @param consentsCreateParams params for new Consent
     */
    @Async
    public void createPisConsent(PaymentConsentsCreateRequest consentsCreateParams) {
        try {
            Consent result = Consent.createPisConsent(
                consentsCreateParams.tppAppName,
                consentsCreateParams.consentId,
                consentsCreateParams.status,
                consentsCreateParams.paymentType,
                consentsCreateParams.paymentInitiation,
                consentsCreateParams.risk
            );
            consentsRepository.save(result);
        } catch (Exception e) {
            log.error("ConsentsCreateService.createPisConsent:", e);
        }
    }

    /**
     * Create new Consent object for PIIS flow.
     * Function is asynchronous.
     *
     * @param consentsCreateParams params for new Consent
     */
    @Async
    public void createPiisConsent(FundsConsentsCreateRequest consentsCreateParams) {
        try {
            Consent result = Consent.createPiisConsent(
              consentsCreateParams.tppAppName,
              consentsCreateParams.consentId,
              "AwaitingAuthorisation",
              consentsCreateParams.expirationDateTime,
              consentsCreateParams.debtorAccount
            );
            consentsRepository.save(result);
        } catch (Exception e) {
            log.error("ConsentsCreateService.createPiisConsent:", e);
        }
    }
}
