/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2021 Salt Edge.
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
package com.saltedge.connector.ob.sdk.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltedge.connector.ob.sdk.SDKConstants;
import com.saltedge.connector.ob.sdk.TestTools;
import com.saltedge.connector.ob.sdk.api.models.request.AccountsConsentsCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.response.ErrorResponse;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonToolsTest {
	@Test
	public void createDefaultMapperTest_case1() throws JsonProcessingException {
		ObjectMapper mapper = JsonTools.createDefaultMapper();
		ErrorResponse testObject = new ErrorResponse();
		testObject.errorClass = "TestClass";
		String jsonString = mapper.writeValueAsString(testObject);

		assertThat(jsonString).contains("TestClass");
		assertThat(jsonString).doesNotContain(SDKConstants.KEY_ERROR_MESSAGE);

		ErrorResponse result = mapper.readValue("{\"key\":\"value\"}", ErrorResponse.class);

		assertThat(result).isNotNull();
		assertThat(result.errorClass).isNull();
		assertThat(result.errorMessage).isNull();
	}

	@Test
	public void createDefaultMapperTest_case2() throws JsonProcessingException {
		ObjectMapper mapper = JsonTools.createDefaultMapper();

		String jsonString = "{\n" +
			"    \"provider_code\": \"spring_bank_ob\",\n" +
			"    \"consent_id\": 1055,\n" +
			"    \"app_name\": \"Payment company\",\n" +
			"    \"status\": \"AwaitingAuthorisation\",\n" +
			"    \"permissions\": [\n" +
			"      \"ReadAccountsBasic\",\n" +
			"      \"ReadAccountsDetail\",\n" +
			"      \"ReadBalances\",\n" +
			"      \"ReadBeneficiariesDetail\",\n" +
			"      \"ReadTransactionsBasic\",\n" +
			"      \"ReadTransactionsCredits\",\n" +
			"      \"ReadTransactionsDebits\",\n" +
			"      \"ReadTransactionsDetail\",\n" +
			"      \"ReadOffers\",\n" +
			"      \"ReadPAN\",\n" +
			"      \"ReadParty\",\n" +
			"      \"ReadPartyPSU\",\n" +
			"      \"ReadProducts\",\n" +
			"      \"ReadStandingOrdersDetail\",\n" +
			"      \"ReadScheduledPaymentsDetail\",\n" +
			"      \"ReadStatementsDetail\",\n" +
			"      \"ReadDirectDebits\"\n" +
			"    ],\n" +
			"    \"expiration_date_time\": \"2021-09-10 13:20:43 UTC\",\n" +
			"    \"transaction_from_date_time\": \"2021-05-14 13:20:40 UTC\",\n" +
			"    \"transaction_to_date_time\": \"2021-08-11 13:20:40 UTC\"\n" +
			"  }";

		AccountsConsentsCreateRequest result = mapper.readValue(jsonString, AccountsConsentsCreateRequest.class);

		assertThat(result.expirationDateTime).isNotNull();
		assertThat(result.providerCode).contains("spring_bank_ob");
	}

	@Test
	public void createAuthorizationPayloadValueTest() {
		PrivateKey privateKey = TestTools.getInstance().getRsaPrivateKey();

		assertThat(JsonTools.createAuthorizationPayloadValue(new ErrorResponse(), privateKey)).startsWith("Bearer ");
		assertThat(JsonTools.createAuthorizationPayloadValue(new ErrorResponse(), null)).isEqualTo("");
	}
}
