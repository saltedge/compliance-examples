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
package com.saltedge.connector.sdk.provider.models;

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
	public void buildProviderOfferedConsentsTest() {
		AccountData account1 = new AccountData();
		account1.iban = "MD12345";
		AccountData account2 = new AccountData();
		account2.iban = "MD67890";

		List<AccountData> balances = Lists.list(account1);
		List<AccountData> transactions = Lists.list(account2);

		ProviderOfferedConsents joinResult = ProviderOfferedConsents.buildProviderOfferedConsents(balances, transactions);

		assertThat(joinResult.balances).isEqualTo(Lists.list(new ConsentData("MD12345")));
		assertThat(joinResult.transactions).isEqualTo(Lists.list(new ConsentData("MD67890")));
	}
}
