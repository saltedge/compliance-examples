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

public class ConsentDataTests {
	@Test
	public void constructorTest() {
		ConsentData model = new ConsentData();

		assertThat(model.accountId).isNull();
		assertThat(model.scopes).isNull();

		model = new ConsentData("accountId", Lists.list(ConsentData.Scopes.balance));

		assertThat(model.accountId).isEqualTo("accountId");
		assertThat(model.scopes).isEqualTo(Lists.list(ConsentData.Scopes.balance));
	}

	@Test
	public void joinConsentsTest() {
		List<String> accounts = Lists.list("id1", "id2", "id3", "id4");
		List<String> balances = Lists.list("id1", "id4");
		List<String> transactions = Lists.list("id2", "id4");
		List<ConsentData> joinResult = ConsentData.joinConsents(accounts, balances, transactions);

		assertThat(joinResult).isEqualTo(Lists.list(
				new ConsentData("id1", Lists.list(ConsentData.Scopes.balance)),
				new ConsentData("id2", Lists.list(ConsentData.Scopes.transactions)),
				new ConsentData("id3", Lists.emptyList()),
				new ConsentData("id4", Lists.list(ConsentData.Scopes.balance, ConsentData.Scopes.transactions))
		));
	}
}
