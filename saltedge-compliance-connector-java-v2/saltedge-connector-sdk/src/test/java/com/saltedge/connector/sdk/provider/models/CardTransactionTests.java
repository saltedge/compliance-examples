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

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class CardTransactionTests {
	@Test
	public void constructorTest1() {
		CardTransaction model = new CardTransaction();

		assertThat(model.getId()).isNull();
		assertThat(model.getAmount()).isNull();
		assertThat(model.getCurrencyCode()).isNull();
		assertThat(model.getStatus()).isNull();
		assertThat(model.getTransactionDate()).isNull();
		assertThat(model.getTransactionDetails()).isNull();
		assertThat(model.getBookingDate()).isNull();
		assertThat(model.getCardAcceptorAddress()).isNull();
		assertThat(model.getCardAcceptorId()).isNull();
		assertThat(model.getCurrencyExchange()).isNull();
		assertThat(model.getInvoiced()).isNull();
		assertThat(model.getMarkupFee()).isNull();
		assertThat(model.getMarkupFeePercentage()).isNull();
		assertThat(model.getMaskedPan()).isNull();
		assertThat(model.getMerchantCategoryCode()).isNull();
		assertThat(model.getOriginalAmount()).isNull();
		assertThat(model.getProprietaryBankTransactionCode()).isNull();
		assertThat(model.getTerminalId()).isNull();
	}
	@Test
	public void constructorTest2() {
		CardTransaction.AcceptorAddress address = new CardTransaction.AcceptorAddress();
		Amount amount = new Amount();
		CardTransaction model = new CardTransaction(
				"id",
				"1.0",
				"EUR",
				"active",
				new Date(0),
				"transactionDetails",
				new Date(0),
				address,
				"cardAcceptorId",
				Lists.emptyList(),
				true,
				amount,
				"markupFeePercentage",
				"****",
				"merchantCategoryCode",
				amount,
				"proprietaryBankTransactionCode",
				"terminalId"
		);

		assertThat(model.getId()).isEqualTo("id");
		assertThat(model.getAmount()).isEqualTo("1.0");
		assertThat(model.getCurrencyCode()).isEqualTo("EUR");
		assertThat(model.getStatus()).isEqualTo("active");
		assertThat(model.getTransactionDate()).isEqualTo(new Date(0));
		assertThat(model.getTransactionDetails()).isEqualTo("transactionDetails");
		assertThat(model.getBookingDate()).isEqualTo(new Date(0));
		assertThat(model.getCardAcceptorAddress()).isEqualTo(address);
		assertThat(model.getCardAcceptorId()).isEqualTo("cardAcceptorId");
		assertThat(model.getCurrencyExchange()).isEqualTo(Lists.emptyList());
		assertThat(model.getInvoiced()).isEqualTo(true);
		assertThat(model.getMarkupFee()).isEqualTo(amount);
		assertThat(model.getMarkupFeePercentage()).isEqualTo("markupFeePercentage");
		assertThat(model.getMaskedPan()).isEqualTo("****");
		assertThat(model.getMerchantCategoryCode()).isEqualTo("merchantCategoryCode");
		assertThat(model.getOriginalAmount()).isEqualTo(amount);
		assertThat(model.getProprietaryBankTransactionCode()).isEqualTo("proprietaryBankTransactionCode");
		assertThat(model.getTerminalId()).isEqualTo("terminalId");
	}
}
