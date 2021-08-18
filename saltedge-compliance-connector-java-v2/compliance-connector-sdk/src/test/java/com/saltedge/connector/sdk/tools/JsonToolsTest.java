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
package com.saltedge.connector.sdk.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.TestTools;
import com.saltedge.connector.sdk.api.models.Account;
import com.saltedge.connector.sdk.api.models.Meta;
import com.saltedge.connector.sdk.api.models.Transaction;
import com.saltedge.connector.sdk.api.models.responses.AccountsResponse;
import com.saltedge.connector.sdk.api.models.responses.TransactionsResponse;
import com.saltedge.connector.sdk.callback.mapping.BaseFailRequest;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonToolsTest {
	@Test
	public void createDefaultMapperTest() throws JsonProcessingException {
		assertThat(new JsonTools()).isNotNull();

		ObjectMapper mapper = JsonTools.createDefaultMapper();
		BaseFailRequest testObject = new BaseFailRequest();
		testObject.errorClass = "TestClass";
		String jsonString = mapper.writeValueAsString(testObject);

		assertThat(jsonString).contains("TestClass");
		assertThat(jsonString).doesNotContain(SDKConstants.KEY_ERROR_MESSAGE);

		BaseFailRequest result = mapper.readValue("{\"key\":\"value\"}", BaseFailRequest.class);

		assertThat(result).isNotNull();
		assertThat(result.errorClass).isNull();
		assertThat(result.errorMessage).isNull();
	}

	@Test
	public void createAuthorizationHeaderValueTest() {
		PrivateKey privateKey = TestTools.getInstance().getRsaPrivateKey();

		assertThat(JsonTools.createAuthorizationHeaderValue(new BaseFailRequest(), privateKey)).startsWith("Bearer ");
		assertThat(JsonTools.createAuthorizationHeaderValue(new BaseFailRequest(), null)).isEqualTo("");
	}

	@Test
	public void serializationTest_AccountsResponse() throws JsonProcessingException {
		ObjectMapper mapper = JsonTools.createDefaultMapper();
		List<Account> accounts = Lists.list(new Account("id", "name", Collections.emptyList(), "CACC", "EUR"));
		AccountsResponse model = new AccountsResponse(accounts);
		String jsonString = mapper.writeValueAsString(model);

		assertThat(jsonString).startsWith("{");
	}

	@Test
	public void serializationTest_TransactionsResponse() throws JsonProcessingException {
		ObjectMapper mapper = JsonTools.createDefaultMapper();
		List<Transaction> transactions = Lists.list(new Transaction());
		TransactionsResponse model = new TransactionsResponse(transactions, new Meta());
		String jsonString = mapper.writeValueAsString(model);

		assertThat(jsonString).startsWith("{");
	}
}
