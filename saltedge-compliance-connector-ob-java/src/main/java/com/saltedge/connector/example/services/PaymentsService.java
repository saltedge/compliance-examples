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

import com.saltedge.connector.example.model.repository.PaymentsRepository;
import com.saltedge.connector.ob.sdk.provider.ConnectorSDKService;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PaymentsService {
  private static final Logger log = LoggerFactory.getLogger(PaymentsService.class);
  @Autowired
  private PaymentsRepository paymentsRepository;
  @Autowired
  private ConnectorSDKService connectorSDKService;

  @Async
  public void initPayment(@NotNull Long paymentId, @NotNull ObPaymentInitiationData params) {
    try {
      connectorSDKService.onPaymentStatusUpdate(String.valueOf(paymentId), "AcceptedSettlementCompleted");
//    Double amountValue = ConnectorServiceTools.getAmountValue(amount);
//    if (amountValue == null) throw new BadRequest.InvalidAttributeValue("amount");
//    AccountEntity debtorAccount = ConnectorServiceTools.findDebtorAccount(accountsRepository, debtorIban);
//    if (debtorAccount == null) throw new BadRequest.InvalidAttributeValue("debtor account");
//
//    PaymentEntity paymentEntity = new PaymentEntity();
//    paymentEntity.status = PaymentStatus.PENDING;
//    paymentEntity.amount = amountValue;
//    paymentEntity.currency = currency;
//    paymentEntity.description = description;
//    paymentEntity.extra = extraData;
//    paymentEntity.accountId = debtorAccount.id;
//    paymentEntity.toAccountName = creditorName;
//    paymentEntity.paymentProduct = paymentProduct;
//
//    paymentEntity.fromIban = debtorIban;
//    paymentEntity.fromBic = debtorBic;
//    paymentEntity.toIban = creditorIban;
//    paymentEntity.toBic = creditorBic;
//
//    PaymentEntity payment = paymentsRepository.save(paymentEntity);
//    return getPaymentAuthorizationPageUrl(payment.id.toString());
    } catch (Exception e) {
      log.error("PaymentsService.initPayment:", e);
    }
  }
}
