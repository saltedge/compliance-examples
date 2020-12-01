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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltedge.connector.sdk.tools.JsonTools;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionTests {
	@Test
	public void constructorTest1() {
		Transaction model = new Transaction();

		assertThat(model.getId()).isNull();
		assertThat(model.getAmount()).isNull();
		assertThat(model.getCurrencyCode()).isNull();
		assertThat(model.getStatus()).isNull();
		assertThat(model.getValueDate()).isNull();
		assertThat(model.getBookingDate()).isNull();
		assertThat(model.getCreditorDetails()).isNull();
		assertThat(model.getDebtorDetails()).isNull();
		assertThat(model.getCurrencyExchange()).isNull();
		assertThat(model.getRemittanceInformation()).isNull();
		assertThat(model.getExtra()).isNull();
	}

	@Test
	public void constructorTest2() {
		Transaction model = new Transaction();
		model.setId("id");
		model.setAmount("1.0");
		model.setCurrencyCode("EUR");
		model.setStatus("active");
		model.setValueDate(LocalDate.parse("2019-11-18"));

		assertThat(model.getId()).isEqualTo("id");
		assertThat(model.getAmount()).isEqualTo("1.0");
		assertThat(model.getCurrencyCode()).isEqualTo("EUR");
		assertThat(model.getStatus()).isEqualTo("active");
		assertThat(model.getValueDate()).isEqualTo(LocalDate.parse("2019-11-18"));
		assertThat(model.getBookingDate()).isNull();
		assertThat(model.getCreditorDetails()).isNull();
		assertThat(model.getDebtorDetails()).isNull();
		assertThat(model.getCurrencyExchange()).isNull();
		assertThat(model.getRemittanceInformation()).isNull();
		assertThat(model.getExtra()).isNull();
	}

	@Test
	public void serializationTest() throws JsonProcessingException {
		Transaction model = new Transaction();
		model.setId("id");
		model.setAmount("1.0");
		model.setCurrencyCode("EUR");
		model.setStatus("active");
		model.setValueDate(LocalDate.parse("2019-11-18"));

		ObjectMapper mapper = JsonTools.createDefaultMapper();
		String json = mapper.writeValueAsString(model);

		assertThat(json).isEqualTo("{\"id\":\"id\",\"amount\":\"1.0\",\"currency\":\"EUR\",\"status\":\"active\",\"value_date\":\"2019-11-18\"}");
	}
}
