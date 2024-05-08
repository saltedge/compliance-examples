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
package com.saltedge.connector.sdk.services.priora;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.PaymentOrder;
import com.saltedge.connector.sdk.api.models.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.api.models.requests.CreatePaymentRequest;
import com.saltedge.connector.sdk.callback.mapping.SessionUpdateCallbackRequest;
import com.saltedge.connector.sdk.models.ParticipantAccount;
import com.saltedge.connector.sdk.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Optional;

import static com.saltedge.connector.sdk.SDKConstants.PAYMENT_PRODUCT_FASTER_PAYMENT_SERVICE;

/**
 * Service designated for creating payment orders.
 * https://priora.saltedge.com/docs/aspsp/v2/pis#pis-connector_endpoints-payments
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PaymentsService extends BaseService {
  private static final Logger log = LoggerFactory.getLogger(PaymentsService.class);

  @Async
  public void createPayment(@NotNull CreatePaymentRequest paymentRequest) {
    try {
      PaymentOrder order = paymentRequest.getPaymentOrder();
      String extraData = createExtraData(
        paymentRequest.sessionSecret,
        paymentRequest.returnToUrl,
        order.endToEndIdentification
      );

      ParticipantAccount debtorAccount = order.getDebtorAccount();
      Optional<ParticipantAccount> optDebtorAccount = Optional.ofNullable(debtorAccount);

      String paymentAuthenticationUrl;
      if (paymentRequest.getPaymentProduct().equals(PAYMENT_PRODUCT_FASTER_PAYMENT_SERVICE)) {
        paymentAuthenticationUrl = providerService.createFPSPayment(
            paymentRequest.getPaymentProduct(),
            order.getCreditorAccount().getBban(),
            order.getCreditorAccount().getSortCode(), //add sort code
            order.getCreditorName(),
            order.getCreditorAddress(),
            order.getCreditorAgentName(),
            optDebtorAccount.map(ParticipantAccount::getBban).orElse(null),
            optDebtorAccount.map(ParticipantAccount::getSortCode).orElse(null),
            order.getInstructedAmount().getAmount(),
            order.getInstructedAmount().getCurrency(),
            order.getRemittanceInformationUnstructured(),
            extraData,
            paymentRequest.psuIpAddress
        );
      } else {
        paymentAuthenticationUrl = providerService.createPayment(
            paymentRequest.getPaymentProduct(),
            order.getCreditorAccount().getIban(),
            order.getCreditorAccount().getBic(),
            order.getCreditorName(),
            order.getCreditorAddress(),
            order.getCreditorAgentName(),
            optDebtorAccount.map(ParticipantAccount::getIban).orElse(null),
            optDebtorAccount.map(ParticipantAccount::getBic).orElse(null),
            order.getInstructedAmount().getAmount(),
            order.getInstructedAmount().getCurrency(),
            order.getRemittanceInformationUnstructured(),
            extraData,
            paymentRequest.psuIpAddress
        );
      }

      if (StringUtils.hasLength(paymentAuthenticationUrl)) {
        SessionUpdateCallbackRequest params = new SessionUpdateCallbackRequest(
            paymentAuthenticationUrl,
            SDKConstants.STATUS_RCVD
        );
        sessionCallbackService.sendUpdateCallback(paymentRequest.sessionSecret, params);
      } else {
        sessionCallbackService.sendFailCallback(paymentRequest.sessionSecret, new NotFound.PaymentNotCreated());
      }
    } catch (Exception e) {
      log.error("PaymentsService.createPayment:", e);
      if (e instanceof HttpErrorParams) sessionCallbackService.sendFailCallback(paymentRequest.sessionSecret, e);
      else sessionCallbackService.sendFailCallback(paymentRequest.sessionSecret, new NotFound.PaymentNotCreated());
    }
  }

  private String createExtraData(
    @NotEmpty String sessionSecret,
    String returnToUrl,
    String endToEndIdentification
  ) throws JsonProcessingException {
    HashMap<String, String> result = new HashMap<>();
    result.put(SDKConstants.KEY_SESSION_SECRET, sessionSecret);
    if (StringUtils.hasLength(returnToUrl)) result.put(SDKConstants.KEY_RETURN_TO_URL, returnToUrl);
    if (StringUtils.hasLength(endToEndIdentification)) {
      result.put(SDKConstants.KEY_END_TO_END_IDENTIFICATION, endToEndIdentification);
    }
    return JsonTools.createDefaultMapper().writeValueAsString(result);
  }
}
