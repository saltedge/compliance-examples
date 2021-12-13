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
package com.saltedge.connector.example.services;

import com.saltedge.connector.example.model.PaymentEntity;
import com.saltedge.connector.example.model.PaymentStatus;
import com.saltedge.connector.example.model.repository.PaymentsRepository;
import com.saltedge.connector.ob.sdk.provider.ConnectorSDKService;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObRiskData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PaymentsService {
  private static final Logger log = LoggerFactory.getLogger(PaymentsService.class);
  @Lazy
  @Autowired
  private ConnectorSDKService connectorSDKService;
  @Autowired
  private PaymentsRepository paymentsRepository;

  public Long initiatePayment(String paymentType, ObPaymentInitiationData params, ObRiskData risk) {
    try {
      PaymentEntity paymentEntity = new PaymentEntity();
      paymentEntity.status = PaymentStatus.PENDING;
      paymentEntity.amount = params.instructedAmount.amount;
      paymentEntity.currency = params.instructedAmount.currency;
      paymentEntity.description = params.remittanceInformation.unstructured;
      paymentEntity.paymentProduct = params.creditorAccount.schemeName;

      paymentEntity.fromAccountNumber = params.debtorAccount.identification;
      paymentEntity.fromSortCode = params.debtorAccount.secondaryIdentification;
      paymentEntity.toAccountNumber = params.creditorAccount.identification;
      paymentEntity.toSortCode = params.creditorAccount.secondaryIdentification;

      PaymentEntity savedPayment = paymentsRepository.save(paymentEntity);
      return savedPayment.id;
    } catch (Exception e) {
      log.error("PaymentsService", e);
      return null;
    }
  }

  @Async
  public void processPayment(@NotNull Long paymentId) {
    try {
      Thread.sleep(1000);
      connectorSDKService.onPaymentStatusUpdate(String.valueOf(paymentId), "AcceptedCreditSettlementCompleted");
    } catch (Exception e) {
      log.error("PaymentsService.processPayment:", e);
    }
  }
}
