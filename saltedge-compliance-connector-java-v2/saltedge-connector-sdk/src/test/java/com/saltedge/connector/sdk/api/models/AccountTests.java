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

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountTests {
	@Test
	public void constructorTest1() {
		Account model = new Account();

		assertThat(model.getId()).isNull();
		assertThat(model.getName()).isNull();
		assertThat(model.getBalances()).isNull();
		assertThat(model.getCashAccountType()).isNull();
		assertThat(model.getCurrencyCode()).isNull();
		assertThat(model.getBban()).isNull();
		assertThat(model.getBic()).isNull();
		assertThat(model.getIban()).isNull();
		assertThat(model.getMsisdn()).isNull();
		assertThat(model.getProduct()).isNull();
		assertThat(model.getStatus()).isNull();
		assertThat(model.getExtra()).isNull();
	}
	@Test
	public void constructorTest2() {
		Account model = new Account("id", "name", Lists.emptyList(), "cashAccountType", "EUR");
		model.setBban("bban");
		model.setBic("bic");
		model.setIban("iban");
		model.setMsisdn("+1");
		model.setProduct("prod");
		model.setStatus("active");
		model.setExtra(new HashMap<>());

		assertThat(model.getId()).isEqualTo("id");
		assertThat(model.getName()).isEqualTo("name");
		assertThat(model.getBalances()).isEmpty();
		assertThat(model.getCashAccountType()).isEqualTo("cashAccountType");
		assertThat(model.getCurrencyCode()).isEqualTo("EUR");
		assertThat(model.getBban()).isEqualTo("bban");
		assertThat(model.getBic()).isEqualTo("bic");
		assertThat(model.getIban()).isEqualTo("iban");
		assertThat(model.getMsisdn()).isEqualTo("+1");
		assertThat(model.getProduct()).isEqualTo("prod");
		assertThat(model.getStatus()).isEqualTo("active");
		assertThat(model.getExtra()).isEmpty();
	}

	@Test
	public void hasIdentifierTest() {
		Account model = new Account();

		assertThat(model.hasIdentifier()).isFalse();

		model.setIban("iban");
		model.setBban(null);
		model.setBic(null);

		assertThat(model.hasIdentifier()).isTrue();

		model.setIban(null);
		model.setBban("bban");
		model.setBic(null);

		assertThat(model.hasIdentifier()).isTrue();

		model.setIban(null);
		model.setBban(null);
		model.setBic("bic");

		assertThat(model.hasIdentifier()).isTrue();
	}

	@Test
	public void containsAccountIdentifierTest() {
		Account model = new Account();

		assertThat(model.containsAccountIdentifier("")).isFalse();

		model.setIban("iban");
		model.setBban(null);
		model.setBic(null);

		assertThat(model.containsAccountIdentifier("")).isFalse();
		assertThat(model.containsAccountIdentifier("iban")).isTrue();

		model.setIban(null);
		model.setBban("bban");
		model.setBic(null);

		assertThat(model.containsAccountIdentifier("bban")).isTrue();

		model.setIban(null);
		model.setBban(null);
		model.setBic("bic");

		assertThat(model.containsAccountIdentifier("bic")).isTrue();
	}
}
