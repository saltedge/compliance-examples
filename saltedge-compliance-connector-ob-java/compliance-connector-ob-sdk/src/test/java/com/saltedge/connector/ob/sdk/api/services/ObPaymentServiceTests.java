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
import com.saltedge.connector.ob.sdk.callback.ConnectorCallbackService;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.model.jpa.ConsentsRepository;
import com.saltedge.connector.ob.sdk.provider.ProviderServiceAbs;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccountIdentifier;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAmount;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObRiskData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class ObPaymentServiceTests {
	@MockBean
	protected ConsentsRepository mockConsentsRepository;
	@MockBean
	protected ProviderServiceAbs mockProviderServiceAbs;
	@MockBean
	protected ConnectorCallbackService mockCallbackService;
	@Autowired
	protected ObPaymentService testService;

	@BeforeEach
	public void setUp() throws Exception {
		Consent initialConsent = new Consent("tppName", "consentId1", "AwaitingAuthorization");
		initialConsent.authorizationId = "authorizationId";
		initialConsent.authCode = "authCode";
		initialConsent.accessToken = "accessToken";
		initialConsent.userId = "userId";
		initialConsent.risk = new ObRiskData("BillPayment");
		given(mockConsentsRepository.findFirstByConsentId("consentId1")).willReturn(initialConsent);

		Consent pisConsent = new Consent("tppName", "consentId2", "AwaitingAuthorization");
		pisConsent.authorizationId = "authorizationId";
		pisConsent.authCode = "authCode";
		pisConsent.accessToken = "accessToken";
		pisConsent.paymentId = "paymentId";
		pisConsent.compliancePaymentId = "compliancePaymentId";
		pisConsent.userId = "userId";
		given(mockConsentsRepository.findFirstByPaymentId("paymentId")).willReturn(pisConsent);
	}

	@Test
	public void whenInitiatePayment_thenNotifyCoreAndUpdateConsent() {
		//given
		ObPaymentInitiationData paymentData = new ObPaymentInitiationData();
		paymentData.instructionIdentification = "instructionIdentification";
		paymentData.endToEndIdentification = "endToEndIdentification";
		paymentData.instructedAmount = new ObAmount("1.0", "GBP");
		paymentData.creditorAccount = new ObAccountIdentifier("scheme", "identifier");

		ObRiskData risk = new ObRiskData("BillPayment");

		PaymentCreateRequest request = new PaymentCreateRequest();
		request.tppAppName = "tppAppName";
		request.consentId = "consentId1";
		request.paymentType = "domestic_payment";
		request.compliancePaymentId = "compliancePaymentId";
		request.paymentInitiation = paymentData;

		given(mockProviderServiceAbs.initiatePayment("userId", "domestic_payment", paymentData, risk))
				.willReturn("paymentId");

		//when
		testService.initiatePayment("consentId1", request);

		//then
		Mockito.verify(mockProviderServiceAbs).initiatePayment("userId", "domestic_payment", paymentData, risk);

		final ArgumentCaptor<Consent> captor = ArgumentCaptor.forClass(Consent.class);
		Mockito.verify(mockConsentsRepository).save(captor.capture());
		assertThat(captor.getValue().compliancePaymentId).isEqualTo("compliancePaymentId");
		assertThat(captor.getValue().paymentId).isEqualTo("paymentId");
		assertThat(captor.getValue().isPisConsent()).isTrue();
	}

	@Test
	public void givenValidPaymentId_whenUpdatePayment_thenNotifyCoreAndUpdateConsent() {
		//given
		String paymentId = "paymentId";

		//when
		testService.updatePayment(paymentId, "AcceptedCreditSettlementCompleted");

		//then
		final ArgumentCaptor<PaymentUpdateRequest> captor = ArgumentCaptor.forClass(PaymentUpdateRequest.class);
		Mockito.verify(mockCallbackService).updatePayment(eq("compliancePaymentId"), captor.capture());
		assertThat(captor.getValue().consentId).isEqualTo("consentId2");
		assertThat(captor.getValue().status).isEqualTo("AcceptedCreditSettlementCompleted");
	}

	@Test
	public void givenInvalidPaymentId_whenUpdatePayment_thenNotifyCoreAndUpdateConsent() {
		//given
		String paymentId = "invalidPaymentId";

		//when
		testService.updatePayment(paymentId, "AcceptedCreditSettlementCompleted");

		//then
		Mockito.verifyNoInteractions(mockCallbackService);
	}
}
