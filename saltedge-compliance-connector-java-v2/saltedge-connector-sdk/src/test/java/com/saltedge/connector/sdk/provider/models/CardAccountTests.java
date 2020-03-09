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

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class CardAccountTests {
	@Test
	public void constructorTest1() {
		CardAccount model = new CardAccount();

		assertThat(model.getId()).isNull();
		assertThat(model.getName()).isNull();
		assertThat(model.getBalances()).isNull();
		assertThat(model.getCreditLimit()).isNull();
		assertThat(model.getCurrencyCode()).isNull();
		assertThat(model.getMaskedPan()).isNull();
		assertThat(model.getProduct()).isNull();
		assertThat(model.getStatus()).isNull();
		assertThat(model.getExtra()).isNull();
	}
	@Test
	public void constructorTest2() {
		CardAccountExtra extra = new CardAccountExtra();
		Amount creditLimit = new Amount();
		CardAccount model = new CardAccount("id", "name", "**** **** **** 1111", "EUR", "prod", "active", Lists.emptyList(), creditLimit, extra);

		assertThat(model.getId()).isEqualTo("id");
		assertThat(model.getName()).isEqualTo("name");
		assertThat(model.getBalances()).isEmpty();
		assertThat(model.getCreditLimit()).isEqualTo(creditLimit);
		assertThat(model.getCurrencyCode()).isEqualTo("EUR");
		assertThat(model.getMaskedPan()).isEqualTo("**** **** **** 1111");
		assertThat(model.getProduct()).isEqualTo("prod");
		assertThat(model.getStatus()).isEqualTo("active");
		assertThat(model.getExtra()).isEqualTo(extra);
	}
}
