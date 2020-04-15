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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyExchangeTests {
	@Test
	public void constructorTest() {
		CurrencyExchange model = new CurrencyExchange();

		assertThat(model.contractIdentification).isNull();
		assertThat(model.exchangeRate).isNull();
		assertThat(model.quotationDate).isNull();
		assertThat(model.sourceCurrency).isNull();
		assertThat(model.targetCurrency).isNull();
		assertThat(model.unitCurrency).isNull();

		model = new CurrencyExchange(
				"contractIdentification",
				"1.0",
				LocalDate.MIN,
				"USD",
				"EUR",
				"GBP"
		);

		assertThat(model.contractIdentification).isEqualTo("contractIdentification");
		assertThat(model.exchangeRate).isEqualTo("1.0");
		assertThat(model.quotationDate).isEqualTo(LocalDate.MIN);
		assertThat(model.sourceCurrency).isEqualTo("USD");
		assertThat(model.targetCurrency).isEqualTo("EUR");
		assertThat(model.unitCurrency).isEqualTo("GBP");
	}
}
