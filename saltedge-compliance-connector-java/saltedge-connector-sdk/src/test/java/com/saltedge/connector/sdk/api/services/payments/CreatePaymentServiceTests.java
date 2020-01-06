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

import com.saltedge.connector.sdk.PaymentTemplates;
import com.saltedge.connector.sdk.callback.mapping.PaymentCallbackRequest;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.PaymentTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreatePaymentServiceTests extends PaymentServicesTests {
	@Autowired
	protected CreatePaymentService createPaymentService;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void givenNoPaymentType_whenCreatePayment_thenSendFailCallback() {
		// given
		Token token = new Token("1");
		token.authTypeCode = "login_password";

		// when
		createPaymentService.createPayment(token,
				"sessionSecret",
				3L,
				"paymentType",
				"redirectUrl",
				new HashMap<>(),
				new HashMap<>()
		);

		// then
		final ArgumentCaptor<PaymentCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(PaymentCallbackRequest.class);
		verify(callbackService).sendFailCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().paymentId).isNull();
		assertThat(callbackCaptor.getValue().prioraPaymentId).isEqualTo(3L);
		assertThat(callbackCaptor.getValue().errorClass).isEqualTo("InvalidPaymentType");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenConfirmedPayment_whenCreatePayment_thenSendSuccessCallback() {
		// given
		Token token = new Token("1");
		token.authTypeCode = "login_password";
		given(providerApi.createPayment(anyString(), ArgumentMatchers.any(PaymentTemplate.class), ArgumentMatchers.anyMap(), ArgumentMatchers.anyMap())).willReturn("2");
		given(providerApi.isPaymentConfirmed("2")).willReturn(true);

		// when
		createPaymentService.createPayment(token,
				"sessionSecret",
				3L,
				PaymentTemplates.TYPE_INTERNAL_TRANSFER,
				"redirectUrl",
				new HashMap<>(),
				new HashMap<>()
		);

		// then
		final ArgumentCaptor<PaymentCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(PaymentCallbackRequest.class);
		verify(callbackService).sendSuccessCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().paymentId).isEqualTo("2");
		assertThat(callbackCaptor.getValue().prioraPaymentId).isEqualTo(3L);

		final ArgumentCaptor<Map<String, String>> attributesCaptor = ArgumentCaptor.forClass(Map.class);
		final ArgumentCaptor<Map<String, String>> extraCaptor = ArgumentCaptor.forClass(Map.class);
		verify(providerApi).createPayment(eq("1"), eq(PaymentTemplates.INTERNAL_TRANSFER), attributesCaptor.capture(), extraCaptor.capture());

		assertThat(attributesCaptor.getValue()).isEmpty();
		assertThat(extraCaptor.getValue().get(Constants.KEY_PRIORA_PAYMENT_ID)).isEqualTo("3");
		assertThat(extraCaptor.getValue().get(Constants.KEY_REDIRECT_URL)).isEqualTo("redirectUrl");
		assertThat(extraCaptor.getValue().get(Constants.KEY_SESSION_SECRET)).isEqualTo("sessionSecret");
	}

	@Test
	public void givenOAuthPayment_whenCreatePayment_thenSendUpdateCallback() {
		// given
		Token token = new Token("1");
		token.authTypeCode = "oauth";
		given(providerApi.createPayment(anyString(), ArgumentMatchers.any(PaymentTemplate.class), ArgumentMatchers.anyMap(), ArgumentMatchers.anyMap())).willReturn("2");
		given(providerApi.isPaymentConfirmed("2")).willReturn(false);
		given(providerApi.getPaymentConfirmationPageUrl("2")).willReturn("http://example.org");

		// when
		createPaymentService.createPayment(token,
				"sessionSecret",
				3L,
				PaymentTemplates.TYPE_INTERNAL_TRANSFER,
				"redirectUrl",
				new HashMap<>(),
				new HashMap<>()
		);

		// then
		final ArgumentCaptor<PaymentCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(PaymentCallbackRequest.class);
		verify(callbackService).sendUpdateCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().paymentId).isEqualTo("2");
		assertThat(callbackCaptor.getValue().prioraPaymentId).isEqualTo(3L);
		assertThat(callbackCaptor.getValue().status).isEqualTo(Constants.STATUS_REDIRECT);
		assertThat(callbackCaptor.getValue().extra.redirectUrl).isEqualTo("http://example.org?payment_id=2");
	}

	@Test
	public void givenInteractivePayment_whenCreatePayment_thenSendUpdateCallback() {
		// given
		Token token = new Token("1");
		token.authTypeCode = "login_password_sms";
		given(providerApi.createPayment(anyString(), ArgumentMatchers.any(PaymentTemplate.class), ArgumentMatchers.anyMap(), ArgumentMatchers.anyMap())).willReturn("2");
		given(providerApi.isPaymentConfirmed("2")).willReturn(false);

		// when
		createPaymentService.createPayment(token,
				"sessionSecret",
				3L,
				PaymentTemplates.TYPE_SEPA,
				"redirectUrl",
				new HashMap<>(),
				new HashMap<>()
		);

		// then
		final ArgumentCaptor<PaymentCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(PaymentCallbackRequest.class);
		verify(callbackService).sendUpdateCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().paymentId).isEqualTo("2");
		assertThat(callbackCaptor.getValue().prioraPaymentId).isEqualTo(3L);
		assertThat(callbackCaptor.getValue().status).isEqualTo(Constants.STATUS_WAITING_CONFIRMATION_CODE);
		assertThat(callbackCaptor.getValue().interactiveStep.instruction).isEqualTo("input sms code");
		assertThat(callbackCaptor.getValue().interactiveStep.interactiveField).isEqualTo("sms");
		assertThat(callbackCaptor.getValue().extra).isNull();
	}

	@Test
	public void givenNonInteractivePayment_whenCreatePayment_thenSendUpdateCallback() {
		// given
		Token token = new Token("1");
		token.authTypeCode = "login_password";
		given(providerApi.createPayment(anyString(), ArgumentMatchers.any(PaymentTemplate.class), ArgumentMatchers.anyMap(), ArgumentMatchers.anyMap())).willReturn("2");
		given(providerApi.isPaymentConfirmed("2")).willReturn(false);

		// when
		createPaymentService.createPayment(token,
				"sessionSecret",
				3L,
				PaymentTemplates.TYPE_SWIFT,
				"redirectUrl",
				new HashMap<>(),
				new HashMap<>()
		);

		// then
		final ArgumentCaptor<PaymentCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(PaymentCallbackRequest.class);
		verify(callbackService).sendUpdateCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().paymentId).isEqualTo("2");
		assertThat(callbackCaptor.getValue().prioraPaymentId).isEqualTo(3L);
		assertThat(callbackCaptor.getValue().status).isEqualTo(Constants.STATUS_WAITING_CONFIRMATION_CODE);
		assertThat(callbackCaptor.getValue().interactiveStep).isNull();
		assertThat(callbackCaptor.getValue().extra).isNull();
	}
}
