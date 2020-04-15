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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProviderOfferedConsentsTests {
	@Test
	public void constructorTest() {
		ProviderOfferedConsents model = new ProviderOfferedConsents();

		assertThat(model.balances).isNull();
		assertThat(model.transactions).isNull();

		model = new ProviderOfferedConsents(Lists.emptyList(), Lists.emptyList());

		assertThat(model.balances).isEmpty();
		assertThat(model.transactions).isEmpty();
	}

	@Test
	public void buildProviderOfferedConsentsTest() throws JsonProcessingException {
		Account account1 = new Account();
		account1.setIban("MD12345");
		Account account2 = new Account();
		account2.setIban("MD67890");
		CardAccount cardAccount1 = new CardAccount();
		cardAccount1.setMaskedPan("**** **** **** 1111");
		CardAccount cardAccount2 = new CardAccount();
		cardAccount2.setMaskedPan("**** **** **** 2222");

		List<Account> accountsOfBalancesConsent = Lists.list(account1);
		List<Account> accountsOfTransactionsConsent = Lists.list(account2);
		List<CardAccount> cardsOfBalancesConsent = Lists.list(cardAccount1);
		List<CardAccount> cardsOfTransactionsConsent = Lists.list(cardAccount2);

		ProviderOfferedConsents joinResult = ProviderOfferedConsents.buildProviderOfferedConsents(
				accountsOfBalancesConsent,
				accountsOfTransactionsConsent,
				cardsOfBalancesConsent,
				cardsOfTransactionsConsent
		);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String json = mapper.writeValueAsString(joinResult);
		assertThat(json).isEqualTo("{\"balances\":[{\"iban\":\"MD12345\"},{\"masked_pan\":\"**** **** **** 1111\"}],\"transactions\":[{\"iban\":\"MD67890\"},{\"masked_pan\":\"**** **** **** 2222\"}]}");
	}
}
