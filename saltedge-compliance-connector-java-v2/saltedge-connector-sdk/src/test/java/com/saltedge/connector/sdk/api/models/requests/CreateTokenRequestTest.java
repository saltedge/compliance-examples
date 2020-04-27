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
package com.saltedge.connector.sdk.api.models.requests;

import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.api.models.ValidationTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateTokenRequestTest extends ValidationTest {
	@Test
	public void validateTest() {
		CreateTokenRequest model = new CreateTokenRequest();

		assertThat(validator.validate(model)).isNotEmpty();

		model.sessionSecret = "sessionSecret";

		assertThat(validator.validate(model)).isNotEmpty();

		model.tppAppName = "tppAppName";

		assertThat(validator.validate(model)).isNotEmpty();

		model.redirectUrl = "redirectUrl";

		assertThat(validator.validate(model)).isNotEmpty();

		model.authorizationType = "authorizationType";

		assertThat(validator.validate(model)).isNotEmpty();

		model.providerCode = "providerCode";

		assertThat(validator.validate(model)).isNotEmpty();

		model.requestedConsent = new ProviderConsents();

		assertThat(validator.validate(model)).isEmpty();
	}

	@Test
	public void requestedConsentTest() {
		CreateTokenRequest model = new CreateTokenRequest();
		model.requestedConsent = new ProviderConsents();

		assertThat(model.requestedConsent.hasGlobalConsent()).isFalse();

		model.requestedConsent.globalAccessConsent = "test";

		assertThat(model.requestedConsent.hasGlobalConsent()).isFalse();

		model.requestedConsent.globalAccessConsent = "allAccounts";

		assertThat(model.requestedConsent.hasGlobalConsent()).isTrue();
	}
}
