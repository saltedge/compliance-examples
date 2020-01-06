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

import com.saltedge.connector.sdk.api.err.BadRequest;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.PaymentData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShowPaymentServiceTests extends PaymentServicesTests {
	@Autowired
	protected ShowPaymentService showPaymentService;

	@Test(expected = BadRequest.WrongRequestFormat.class)
	public void givenNoPayment_whenShowPayment_thenNoInteractionsWithProviderApi() {
		// given
		Token token = new Token("1");
		given(providerApi.getPaymentData("2")).willReturn(null);

		// when
		showPaymentService.showPayment("2");
	}

	@Test
	public void givenPayment_whenShowPayment_thenReturnPaymentData() {
		// given
		Token token = new Token("1");
		PaymentData testData = new PaymentData();
		given(providerApi.getPaymentData("2")).willReturn(testData);

		// when
		PaymentData paymentData = showPaymentService.showPayment("2");

		// then
		assertThat(paymentData.prioraPaymentId).isNull();
	}

	@Test
	public void givenPayment_whenShowPayment_thenReturnPaymentData2() {
		// given
		Token token = new Token("1");
		PaymentData testData = new PaymentData();
		testData.extra = new HashMap<>();
		testData.extra.put(Constants.KEY_PRIORA_PAYMENT_ID, "3");
		given(providerApi.getPaymentData("2")).willReturn(testData);

		// when
		PaymentData paymentData = showPaymentService.showPayment("2");

		// then
		assertThat(paymentData.prioraPaymentId).isEqualTo(3L);
	}
}
