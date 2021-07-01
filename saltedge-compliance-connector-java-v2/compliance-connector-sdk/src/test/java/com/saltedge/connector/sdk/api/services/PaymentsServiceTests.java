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
package com.saltedge.connector.sdk.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.Account;
import com.saltedge.connector.sdk.api.models.Amount;
import com.saltedge.connector.sdk.api.models.ParticipantAddress;
import com.saltedge.connector.sdk.api.models.PaymentOrder;
import com.saltedge.connector.sdk.api.models.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.models.requests.CreatePaymentRequest;
import com.saltedge.connector.sdk.callback.mapping.SessionUpdateCallbackRequest;
import com.saltedge.connector.sdk.tools.JsonTools;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentsServiceTests extends BaseServicesTests {
	@Autowired
	protected PaymentsService testService;

	private final ParticipantAddress address = new ParticipantAddress("street", "house number", "Toronto", "CA1234", "CA");

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void givenNullPayment_whenCreatePayment_thenSendSessionsFailCallback() throws JsonProcessingException {
		// given
		HashMap<String, String> extra = new HashMap<>();
		extra.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extra.put(SDKConstants.KEY_RETURN_TO_URL, "redirectUrl");
		extra.put(SDKConstants.KEY_END_TO_END_IDENTIFICATION, "endToEndIdentification");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extra);
		given(providerService.createPayment(
			"sepa-credit-transfers",
				"creditorAccountIban",
			"creditorAccountBic",
				"creditorName",
			address,
			"creditorAgentName",
				"debtorAccountIban",
			"debtorAccountBic",
				"1.0",
				"USD",
				"remittanceInformationUnstructured",
			extraJson
		)).willReturn(null);

		// when
		testService.createPayment(createPaymentRequest(false));

		// then
		final ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
		verify(sessionsCallbackService).sendFailCallback(eq("sessionSecret"), captor.capture());
		assertThat(((HttpErrorParams) captor.getValue()).getErrorClass()).isEqualTo("PaymentNotCreated");
		verifyNoInteractions(tokensRepository);
	}

	@Test
	public void givenSepaRequest_whenCreatePayment_thenSaveTokenAndSendSessionsUpdateCallback() throws JsonProcessingException {
		// given
		HashMap<String, String> extra = new HashMap<>();
		extra.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extra.put(SDKConstants.KEY_RETURN_TO_URL, "redirectUrl");
		extra.put(SDKConstants.KEY_END_TO_END_IDENTIFICATION, "endToEndIdentification");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extra);
		String redirectUrl = "http://example.com?payment_id=payment1";
		given(providerService.createPayment(
			"sepa-credit-transfers",
				"creditorAccountIban",
			"creditorAccountBic",
				"creditorName",
			address,
			"creditorAgentName",
				"debtorAccountIban",
			"debtorAccountBic",
				"1.0",
				"USD",
				"remittanceInformationUnstructured",
			extraJson
		)).willReturn(redirectUrl);

		// when
		testService.createPayment(createPaymentRequest(false));

		// then
		verify(providerService).createPayment(
			"sepa-credit-transfers",
				"creditorAccountIban",
			"creditorAccountBic",
				"creditorName",
			address,
			"creditorAgentName",
				"debtorAccountIban",
			"debtorAccountBic",
				"1.0",
				"USD",
				"remittanceInformationUnstructured",
			extraJson
		);
		final ArgumentCaptor<SessionUpdateCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionUpdateCallbackRequest.class);
		verify(sessionsCallbackService).sendUpdateCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().status).isEqualTo(SDKConstants.STATUS_REDIRECT);
		assertThat(callbackCaptor.getValue().redirectUrl).isEqualTo(redirectUrl);
	}

	@Test
	public void givenSepaRequestWithoutDebtor_whenCreatePayment_thenSaveTokenAndSendSessionsUpdateCallback() throws JsonProcessingException {
		// given
		HashMap<String, String> extra = new HashMap<>();
		extra.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extra.put(SDKConstants.KEY_RETURN_TO_URL, "redirectUrl");
		extra.put(SDKConstants.KEY_END_TO_END_IDENTIFICATION, "endToEndIdentification");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extra);
		String redirectUrl = "http://example.com?payment_id=payment1";
		given(providerService.createPayment(
			"sepa-credit-transfers",
			"creditorAccountIban",
			"creditorAccountBic",
			"creditorName",
			address,
			"creditorAgentName",
			null,
			null,
			"1.0",
			"USD",
			"remittanceInformationUnstructured",
			extraJson
		)).willReturn(redirectUrl);

		// when
		testService.createPayment(createPaymentRequest(true));

		// then
		verify(providerService).createPayment(
			"sepa-credit-transfers",
			"creditorAccountIban",
			"creditorAccountBic",
			"creditorName",
			address,
			"creditorAgentName",
			null,
			null,
			"1.0",
			"USD",
			"remittanceInformationUnstructured",
			extraJson
		);
		final ArgumentCaptor<SessionUpdateCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionUpdateCallbackRequest.class);
		verify(sessionsCallbackService).sendUpdateCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().status).isEqualTo(SDKConstants.STATUS_REDIRECT);
		assertThat(callbackCaptor.getValue().redirectUrl).isEqualTo(redirectUrl);
	}

	@Test
	public void givenFPSRequest_whenCreatePayment_thenSaveTokenAndSendSessionsUpdateCallback() throws JsonProcessingException {
		// given
		HashMap<String, String> extra = new HashMap<>();
		extra.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extra.put(SDKConstants.KEY_RETURN_TO_URL, "redirectUrl");
		extra.put(SDKConstants.KEY_END_TO_END_IDENTIFICATION, "endToEndIdentification");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extra);
		String redirectUrl = "http://example.com?payment_id=payment1";
		given(providerService.createFPSPayment(
			"faster-payment-service",
			"creditorAccountBban",
			"creditorAccountSortCode",
			"creditorName",
			address,
			"creditorAgentName",
			"debtorAccountBban",
			"debtorAccountSortCode",
			"1.0",
			"USD",
			"remittanceInformationUnstructured",
			extraJson
		)).willReturn(redirectUrl);

		// when
		testService.createPayment(createFPSPaymentRequest(false));

		// then
		verify(providerService).createFPSPayment(
			"faster-payment-service",
			"creditorAccountBban",
			"creditorAccountSortCode",
			"creditorName",
			address,
			"creditorAgentName",
			"debtorAccountBban",
			"debtorAccountSortCode",
			"1.0",
			"USD",
			"remittanceInformationUnstructured",
			extraJson
		);
		final ArgumentCaptor<SessionUpdateCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionUpdateCallbackRequest.class);
		verify(sessionsCallbackService).sendUpdateCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().status).isEqualTo(SDKConstants.STATUS_REDIRECT);
		assertThat(callbackCaptor.getValue().redirectUrl).isEqualTo(redirectUrl);
	}

	private CreatePaymentRequest createPaymentRequest(Boolean skipDebtorAccount) {
		Account creditorAccount = new Account();
		creditorAccount.setIban("creditorAccountIban");
		creditorAccount.setBic("creditorAccountBic");
		Account debtorAccount = new Account();
		debtorAccount.setIban("debtorAccountIban");
		debtorAccount.setBic("debtorAccountBic");
		CreatePaymentRequest result = new CreatePaymentRequest(
				"tppAppName",
				"providerCode",
				"redirectUrl",
				new PaymentOrder(
						creditorAccount,
						"creditorName",
						address,
					"creditorAgentName",
					skipDebtorAccount ? null : debtorAccount,
						new Amount("1.0", "USD"),
						"endToEndIdentification",
						"remittanceInformationUnstructured"
				),
			"sepa-credit-transfers"
		);
		result.sessionSecret = "sessionSecret";
		return result;
	}

	private CreatePaymentRequest createFPSPaymentRequest(Boolean skipDebtorAccount) {
		Account creditorAccount = new Account();
		creditorAccount.setBban("creditorAccountBban");
		creditorAccount.setSortCode("creditorAccountSortCode");
		Account debtorAccount = new Account();
		debtorAccount.setBban("debtorAccountBban");
		debtorAccount.setSortCode("debtorAccountSortCode");
		CreatePaymentRequest result = new CreatePaymentRequest(
			"tppAppName",
			"providerCode",
			"redirectUrl",
			new PaymentOrder(
				creditorAccount,
				"creditorName",
				address,
				"creditorAgentName",
				skipDebtorAccount ? null : debtorAccount,
				new Amount("1.0", "USD"),
				"endToEndIdentification",
				"remittanceInformationUnstructured"
			),
			"faster-payment-service"
		);
		result.sessionSecret = "sessionSecret";
		result.sessionSecret = "sessionSecret";
		return result;
	}
}
