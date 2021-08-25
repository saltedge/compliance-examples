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
import com.saltedge.connector.ob.sdk.model.jpa.ConsentsRepository;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccountIdentifier;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAmount;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.Mockito.verify;

@SpringBootTest
public class ObConsentsCreateServiceTests {
	@MockBean
	protected ConsentsRepository mockConsentsRepository;
	@Autowired
	protected ObConsentsCreateService testService;

	@Test
	public void whenCreateAisConsent_thenSaveConsent() {
		// given
		AccountsConsentsCreateRequest request = new AccountsConsentsCreateRequest();
		request.tppAppName = "tppAppName";
		request.consentId = "1";
		request.permissions = Collections.emptyList();
		request.expirationDateTime = null;
		request.transactionFrom = null;
		request.transactionTo = null;

		// when
		testService.createAisConsent(request);

		// then
		Consent testConsent = Consent.createAisConsent("tppAppName", "1", "AwaitingAuthorisation", Collections.emptyList(), null, null, null);
		verify(mockConsentsRepository).save(testConsent);
	}

	@Test
	public void whenCreatePisConsent_thenSaveConsent() {
		// given
		ObPaymentInitiationData paymentData = new ObPaymentInitiationData();
		paymentData.instructionIdentification = "instructionIdentification";
		paymentData.endToEndIdentification = "endToEndIdentification";
		paymentData.instructedAmount = new ObAmount("1.0", "GBP");
		paymentData.creditorAccount = new ObAccountIdentifier("scheme", "identifier");
		PaymentConsentsCreateRequest request = new PaymentConsentsCreateRequest();
		request.tppAppName = "tppAppName";
		request.consentId = "1";
		request.paymentInitiation = paymentData;

		// when
		testService.createPisConsent(request);

		// then
		Consent testConsent = Consent.createPisConsent("tppAppName", "1", "AwaitingAuthorisation", paymentData);
		verify(mockConsentsRepository).save(testConsent);
	}

	@Test
	public void whenCreatePiisConsent_thenSaveConsent() {
		// given
		FundsConsentsCreateRequest request = new FundsConsentsCreateRequest();
		request.tppAppName = "tppAppName";
		request.consentId = "1";
		request.expirationDateTime = Instant.now().plusSeconds(1);
		request.debtorAccount = new ObAccountIdentifier("scheme", "identifier");

		// when
		testService.createPiisConsent(request);

		// then
		Consent testConsent = Consent.createPiisConsent("tppAppName", "1", "AwaitingAuthorisation", request.expirationDateTime, request.debtorAccount);
		verify(mockConsentsRepository).save(testConsent);
	}
}
