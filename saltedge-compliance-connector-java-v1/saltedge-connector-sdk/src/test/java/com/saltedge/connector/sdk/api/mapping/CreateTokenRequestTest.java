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

public class CreateTokenRequestTest extends ValidationTest {
	@Test
	public void validateTest() {
		CreateTokenRequest model = new CreateTokenRequest();

		assertThat(validator.validate(model)).isNotEmpty();

		model.sessionSecret = "sessionSecret";

		assertThat(validator.validate(model)).isNotEmpty();

		model.tppAppName = "appName";

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest = new CreateTokenRequest.OriginalRequest();

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.clientJwt = "clientJwt";

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload = new CreateTokenClientPayload();

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload.exp = 3;

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload.data = new CreateTokenClientPayload.Data();

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload.data.scopes = new String[]{"transaction"};

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload.data.consentPeriodDays = 1;

		assertThat(validator.validate(model)).isNotEmpty();

		model.originalRequest.payload.data.credentials = new HashMap<>();

		assertThat(validator.validate(model)).isEmpty();
	}

	@Test
	public void getCredentialsTest() {
		CreateTokenRequest model = new CreateTokenRequest();

		assertThat(model.getCredentials()).isNull();

		model.originalRequest = new CreateTokenRequest.OriginalRequest();
		model.originalRequest.payload = new CreateTokenClientPayload();
		model.originalRequest.payload.data = new CreateTokenClientPayload.Data();
		model.originalRequest.payload.data.credentials = new HashMap<>();

		assertThat(model.getCredentials()).isEmpty();
	}

	@Test
	public void getAuthorizationTypeTest() {
		CreateTokenRequest model = new CreateTokenRequest();

		assertThat(model.getAuthorizationType()).isNull();

		model.originalRequest = new CreateTokenRequest.OriginalRequest();
		model.originalRequest.payload = new CreateTokenClientPayload();
		model.originalRequest.payload.data = new CreateTokenClientPayload.Data();
		model.originalRequest.payload.data.credentials = new HashMap<>();
		model.originalRequest.payload.data.credentials.put("sms", "123456");

		assertThat(model.getAuthorizationType()).isNull();

		model.originalRequest.payload.data.credentials.put("authorization_type", "password");

		assertThat(model.getAuthorizationType()).isEqualTo("password");
	}
}
