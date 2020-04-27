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
package com.saltedge.connector.sdk.callback.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.tools.JsonTools;
import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionSuccessCallbackRequestTests {
	@Test
	public void constructorTest() {
		SessionSuccessCallbackRequest model = new SessionSuccessCallbackRequest();

		assertThat(model.providerOfferedConsents).isNull();
		assertThat(model.token).isNull();
		assertThat(model.tokenExpiresAt).isNull();
		assertThat(model.userId).isNull();
	}

	@Test
	public void givenBankConsent_whenSerialize_thenReturnJsonString() throws JsonProcessingException {
		SessionSuccessCallbackRequest model = new SessionSuccessCallbackRequest(
				ProviderConsents.buildAllAccountsConsent(),
				"accessToken",
				Instant.parse("2019-11-18T16:04:50.915Z"),
				"userId"
		);

		ObjectMapper mapper = JsonTools.createDefaultMapper();
		String json = mapper.writeValueAsString(model);

		assertThat(json).isEqualTo("{\"extra\":{},\"consent\":{\"balances\":[],\"transactions\":[]},\"token\":\"accessToken\",\"token_expires_at\":\"2019-11-18T16:04:50.915Z\",\"user_id\":\"userId\"}");
	}

	@Test
	public void givenGlobalConsent_whenSerialize_thenReturnJsonString() throws JsonProcessingException {
		SessionSuccessCallbackRequest model = new SessionSuccessCallbackRequest(
				new ProviderConsents(ProviderConsents.GLOBAL_CONSENT_VALUE),
				"accessToken",
				Instant.parse("2019-11-18T16:04:50.915Z"),
				"userId"
		);

		ObjectMapper mapper = JsonTools.createDefaultMapper();
		String json = mapper.writeValueAsString(model);

		assertThat(json).isEqualTo("{\"extra\":{},\"consent\":{\"allPsd2\":\"allAccounts\"},\"token\":\"accessToken\",\"token_expires_at\":\"2019-11-18T16:04:50.915Z\",\"user_id\":\"userId\"}");
	}
}
