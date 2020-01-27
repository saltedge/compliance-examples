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

import com.saltedge.connector.sdk.callback.mapping.PaymentCallbackRequest;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfirmPaymentServiceTests extends PaymentServicesTests {
	@Autowired
	protected ConfirmPaymentService confirmPaymentService;

	@Test
	public void givenProviderReturnFalseOnConfirmPayment_whenConfirmPayment_thenSendInvalidConfirmationCodeCallback() {
		// given
		given(providerApi.confirmPayment("2", new HashMap<>())).willReturn(false);

		// when
		confirmPaymentService.confirmPayment("sessionSecret", "2", 3L, new HashMap<>());

		// then
		final ArgumentCaptor<PaymentCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(PaymentCallbackRequest.class);
		verify(callbackService).sendFailCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().paymentId).isEqualTo("2");
		assertThat(callbackCaptor.getValue().prioraPaymentId).isEqualTo(3L);
		assertThat(callbackCaptor.getValue().errorClass).isEqualTo("InvalidConfirmationCode");
		assertThat(callbackCaptor.getValue().errorMessage).isEqualTo("Invalid confirmation code.");
	}

	@Test
	public void givenProviderReturnSuccessOnConfirmPayment_whenConfirmPayment_thenSendSuccessCallback() {
		// given
		given(providerApi.confirmPayment("2", new HashMap<>())).willReturn(true);

		// when
		confirmPaymentService.confirmPayment("sessionSecret", "2", 3L, new HashMap<>());

		// then
		final ArgumentCaptor<PaymentCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(PaymentCallbackRequest.class);
		verify(callbackService).sendSuccessCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().paymentId).isEqualTo("2");
		assertThat(callbackCaptor.getValue().prioraPaymentId).isEqualTo(3L);
	}
}
