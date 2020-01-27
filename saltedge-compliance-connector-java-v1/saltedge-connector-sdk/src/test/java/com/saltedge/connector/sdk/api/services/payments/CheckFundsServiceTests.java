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
package com.saltedge.connector.sdk.api.services.payments;

import com.saltedge.connector.sdk.api.err.BadRequest;
import com.saltedge.connector.sdk.api.mapping.CheckFundsRequest;
import com.saltedge.connector.sdk.api.services.payments.CheckFundsService;
import com.saltedge.connector.sdk.callback.mapping.FundsCallbackRequest;
import com.saltedge.connector.sdk.callback.services.FundsCheckerCallbackService;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.ProviderApi;
import com.saltedge.connector.sdk.provider.models.AccountData;
import com.saltedge.connector.sdk.provider.models.ExchangeRate;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CheckFundsServiceTests {
	@Autowired
	protected CheckFundsService checkFundsService;
	@MockBean
	protected ProviderApi providerApi;
	private FundsCheckerCallbackService mockCallbackServer = mock(FundsCheckerCallbackService.class);
	private Token token = new Token("1");

	@Before
	public void setUp() throws Exception {
		checkFundsService.fundsCheckerCallbackService = mockCallbackServer;
		given(providerApi.getExchangeRates()).willReturn(Lists.list(
				new ExchangeRate("EUR", 2.0f),
				new ExchangeRate("USD", 1.0f)
		));
		given(providerApi.getAccounts("1")).willReturn(Lists.list(
				new AccountData("1", 100.0, 100.0, 0.0, "USD", "", "name", "card", "ABC", false, "", "", "", new HashMap<>()),
				new AccountData("2", 100.0, 100.0, 0.0, "USD", "", "name", "card", "CBA", false, "", "", "", new HashMap<>())
		));
	}

	@Test
	public void givenCurrencyNotFound_whenCheckFunds_thenSendFailCallbackInvalidAttributeValue() {
		// given
		CheckFundsRequest params = createParams(100.0);
		given(providerApi.getExchangeRates()).willReturn(Lists.emptyList());

		// when
		checkFundsService.checkFunds(token, params);

		// then
		verify(mockCallbackServer).sendFailCallback(eq("sessionSecret"), any(BadRequest.InvalidAttributeValue.class));
	}

	@Test
	public void givenAccountNotFound_whenCheckFunds_thenSendFailCallbackInvalidAttributeValue() {
		// given
		CheckFundsRequest params = createParams(100.0);
		given(providerApi.getAccounts("1")).willReturn(Lists.emptyList());

		// when
		checkFundsService.checkFunds(token, params);

		// then
		verify(mockCallbackServer).sendFailCallback(eq("sessionSecret"), any(BadRequest.InvalidAttributeValue.class));
	}

	@Test
	public void givenAccountWithNotEnoughFunds_whenCheckFunds_thenSendSuccessCallbackWithFalseValue() {
		// given
		CheckFundsRequest params = createParams(100.0);

		// when
		checkFundsService.checkFunds(token, params);

		// then
		final ArgumentCaptor<FundsCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(FundsCallbackRequest.class);
		verify(mockCallbackServer).sendSuccessCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().fundsAvailable).isFalse();
	}

	@Test
	public void givenAccountWithEnoughFunds_whenCheckFunds_thenSendSuccessCallbackWithTrueValue() {
		// given
		CheckFundsRequest params = createParams(40.0);

		// when
		checkFundsService.checkFunds(token, params);

		// then
		final ArgumentCaptor<FundsCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(FundsCallbackRequest.class);
		verify(mockCallbackServer).sendSuccessCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().fundsAvailable).isTrue();
	}

	private CheckFundsRequest createParams(double amount) {
		CheckFundsRequest params = new CheckFundsRequest();
		params.sessionSecret = "sessionSecret";
		params.account = "ABC";
		params.amount = amount;
		params.currencyCode = "EUR";
		return params;
	}
}
