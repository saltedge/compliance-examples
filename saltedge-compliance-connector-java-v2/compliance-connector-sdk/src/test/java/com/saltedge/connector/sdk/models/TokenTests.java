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
package com.saltedge.connector.sdk.models;

import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.models.domain.AisToken;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenTests {
	@Test
	public void isExpiredTest() {
		AisToken aisToken = new AisToken();

		assertThat(aisToken.isExpired()).isTrue();

		aisToken.tokenExpiresAt = Instant.now().minusSeconds(1);

		assertThat(aisToken.isExpired()).isTrue();

		aisToken.tokenExpiresAt = Instant.now().plusSeconds(1);

		assertThat(aisToken.isExpired()).isFalse();
	}

	@Test
	public void notGlobalConsentTest() {
		AisToken aisToken = new AisToken();

		assertThat(aisToken.notGlobalConsent()).isTrue();

		aisToken.providerOfferedConsents = ProviderConsents.buildAllAccountsConsent();

		assertThat(aisToken.notGlobalConsent()).isTrue();

		aisToken.providerOfferedConsents = new ProviderConsents(ProviderConsents.GLOBAL_CONSENT_VALUE);

		assertThat(aisToken.notGlobalConsent()).isFalse();
	}
}
