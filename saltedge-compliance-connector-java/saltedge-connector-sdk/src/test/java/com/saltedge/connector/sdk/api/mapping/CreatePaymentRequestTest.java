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
package com.saltedge.connector.sdk.api.mapping;

import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class CreatePaymentRequestTest extends ValidationTest {
	@Test
	public void validateTest() {
		CreatePaymentRequest model = new CreatePaymentRequest();

		assertThat(validator.validate(model)).isNotEmpty();

		model.sessionSecret = "sessionSecret";

		assertThat(validator.validate(model)).isNotEmpty();

		model.paymentType = "password";

		assertThat(validator.validate(model)).isNotEmpty();

		model.prioraPaymentId = 2L;

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest = new CreatePaymentRequest.OriginalRequest();

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.clientJwt = "clientJwt";

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload = new CreatePaymentClientPayload();

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload.exp = 3;

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload.data = new CreatePaymentClientPayload.Data();

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload.data.paymentAttributes = new HashMap<>();

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload.data.paymentAttributes.put("password", "test");

		assertThat(validator.validate(model)).isEmpty();
	}

	@Test
	public void getPaymentAttributesTest() {
		CreatePaymentRequest model = new CreatePaymentRequest();

		assertThat(model.getPaymentAttributes()).isNull();

		model.originalRequest = new CreatePaymentRequest.OriginalRequest();
		model.originalRequest.payload = new CreatePaymentClientPayload();
		model.originalRequest.payload.data = new CreatePaymentClientPayload.Data();
		model.originalRequest.payload.data.paymentAttributes = new HashMap<>();

		assertThat(model.getPaymentAttributes()).isEmpty();
	}

	@Test
	public void getExtraTest() {
		CreatePaymentRequest model = new CreatePaymentRequest();

		assertThat(model.getExtra()).isEmpty();

		model.originalRequest = new CreatePaymentRequest.OriginalRequest();
		model.originalRequest.payload = new CreatePaymentClientPayload();
		model.originalRequest.payload.data = new CreatePaymentClientPayload.Data();
		model.originalRequest.payload.data.extra = new HashMap<>();

		assertThat(model.getExtra()).isEmpty();
	}
}
