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
package com.saltedge.connector.sdk.api.models;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProviderOfferedConsentTests {
	@Test
	public void constructorTest() {
		ProviderOfferedConsent model = new ProviderOfferedConsent();

		assertThat(model.iban).isNull();
		assertThat(model.bban).isNull();
		assertThat(model.bic).isNull();
		assertThat(model.msisdn).isNull();
		assertThat(model.maskedPan).isNull();

		model = new ProviderOfferedConsent("iban", "bban", "bic", "msisdn", "****");

		assertThat(model.iban).isEqualTo("iban");
		assertThat(model.bban).isEqualTo("bban");
		assertThat(model.bic).isEqualTo("bic");
		assertThat(model.msisdn).isEqualTo("msisdn");
		assertThat(model.maskedPan).isEqualTo("****");
	}

	@Test
	public void createAccountConsentTest() {
		ProviderOfferedConsent model = ProviderOfferedConsent.createAccountConsent("iban");

		assertThat(model).isEqualTo(new ProviderOfferedConsent("iban", null, null, null, null));
	}

	@Test
	public void createCardConsentTest() {
		ProviderOfferedConsent model = ProviderOfferedConsent.createCardConsent("****");

		assertThat(model).isEqualTo(new ProviderOfferedConsent(null, null, null, null, "****"));
	}

	@Test
	public void toStringTest() {
		ProviderOfferedConsent model = ProviderOfferedConsent.createCardConsent("****");

		assertThat(model.toString()).startsWith("Consent");
	}
}
