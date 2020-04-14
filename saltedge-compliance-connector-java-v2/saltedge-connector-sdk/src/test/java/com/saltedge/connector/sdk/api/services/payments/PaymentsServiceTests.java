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

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.mapping.CreatePaymentRequest;
import com.saltedge.connector.sdk.api.services.BaseServicesTests;
import com.saltedge.connector.sdk.callback.mapping.SessionUpdateCallbackRequest;
import com.saltedge.connector.sdk.models.PaymentOrder;
import com.saltedge.connector.sdk.provider.models.Amount;
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

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void givenNullPayment_whenCreatePayment_thenSendSessionsFailCallback() {
		// given
		HashMap<String, String> extra = new HashMap<>();
		extra.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extra.put(SDKConstants.KEY_RETURN_TO_URL, "redirectUrl");
		extra.put(SDKConstants.KEY_END_TO_END_IDENTIFICATION, "endToEndIdentification");
		given(providerService.createPayment(
				"creditorAccountIban",
				"creditorName",
				"debtorAccountIban",
				"1.0",
				"USD",
				"remittanceInformationUnstructured",
				extra
		)).willReturn(null);

		// when
		testService.createPayment(createPaymentRequest());

		// then
		final ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
		verify(sessionsCallbackService).sendFailCallback(eq("sessionSecret"), captor.capture());
		assertThat(((HttpErrorParams) captor.getValue()).getErrorClass()).isEqualTo("PaymentNotCreated");
		verifyNoInteractions(tokensRepository);
	}

	@Test
	public void givenOAuthAuthType_whenCreatePayment_thenSaveTokenAndSendSessionsUpdateCallback() {
		// given
		HashMap<String, String> extra = new HashMap<>();
		extra.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extra.put(SDKConstants.KEY_RETURN_TO_URL, "redirectUrl");
		extra.put(SDKConstants.KEY_END_TO_END_IDENTIFICATION, "endToEndIdentification");
		given(providerService.createPayment(
				"creditorAccountIban",
				"creditorName",
				"debtorAccountIban",
				"1.0",
				"USD",
				"remittanceInformationUnstructured",
				extra
		)).willReturn("payment1");
		given(providerService.getPaymentAuthorizationPageUrl("payment1")).willReturn("http://example.com?payment_id=payment1");

		// when
		testService.createPayment(createPaymentRequest());

		// then
		verify(providerService).createPayment(
				"creditorAccountIban",
				"creditorName",
				"debtorAccountIban",
				"1.0",
				"USD",
				"remittanceInformationUnstructured",
				extra
		);
		final ArgumentCaptor<SessionUpdateCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionUpdateCallbackRequest.class);
		verify(sessionsCallbackService).sendUpdateCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().status).isEqualTo(SDKConstants.STATUS_REDIRECT);
		assertThat(callbackCaptor.getValue().redirectUrl).isEqualTo("http://example.com?payment_id=payment1");
	}

	private CreatePaymentRequest createPaymentRequest(

	) {
		CreatePaymentRequest result = new CreatePaymentRequest(
				"tppAppName",
				"providerCode",
				"redirectUrl",
				new PaymentOrder(
						new PaymentOrder.Account("creditorAccountIban"),
						"creditorName",
						new PaymentOrder.Account("debtorAccountIban"),
						new Amount("1.0", "USD"),
						"endToEndIdentification",
						"remittanceInformationUnstructured"
				)
		);
		result.sessionSecret = "sessionSecret";
		return result;
	}
}
