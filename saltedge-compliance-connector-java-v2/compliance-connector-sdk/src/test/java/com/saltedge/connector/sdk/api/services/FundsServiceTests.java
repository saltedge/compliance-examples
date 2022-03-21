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
package com.saltedge.connector.sdk.api.services;

import com.saltedge.connector.sdk.api.models.*;
import com.saltedge.connector.sdk.api.models.err.BadRequest;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.api.models.requests.FundsConfirmationRequest;
import com.saltedge.connector.sdk.models.ParticipantAccount;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import com.saltedge.connector.sdk.services.priora.FundsService;
import com.saltedge.connector.sdk.models.domain.AisToken;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FundsServiceTests extends BaseServicesTests {
	@Autowired
	protected FundsService testService;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
    given(providerService.getExchangeRates()).willReturn(Lists.list(
        new ExchangeRate("EUR", 1.0f),
        new ExchangeRate("USD", 0.90f),
        new ExchangeRate("GBP", 1.502f)
    ));
    Account account = new Account();
    account.setIban("iban");
    account.setBalances(Lists.list(new AccountBalance("100.0", "GBP", "openingBooked")));
    account.setCurrencyCode("EUR");
    given(providerService.getAccountsOfUser("1")).willReturn(Lists.list(account));
	}

	@Test
	public void givenValidRequest_whenConfirmFunds_thenReturnTrue() {
		// given
    PiisToken token = new PiisToken("1");
    token.account = ParticipantAccount.createWithIbanAndCurrency("iban", "EUR");
    FundsConfirmationRequest request = new FundsConfirmationRequest(
        "code",
        ParticipantAccount.createWithIbanAndCurrency("iban", "EUR"),
        new Amount("1.0", "EUR")
    );

		// when
    boolean result = testService.confirmFundsAvailability(token, request);

		// then
    assertThat(result).isTrue();
	}

  @Test
  public void givenValidRequestWithBigAmount_whenConfirmFunds_thenReturnFalse() {
    // given
    PiisToken token = new PiisToken("1");
    token.account = ParticipantAccount.createWithIbanAndCurrency("iban", "EUR");
    FundsConfirmationRequest request = new FundsConfirmationRequest(
        "code",
        ParticipantAccount.createWithIbanAndCurrency("iban", "EUR"),
        new Amount("123456789.0", "USD")
    );

    // when
    boolean result = testService.confirmFundsAvailability(token, request);

    // then
    assertThat(result).isFalse();
  }

  @Test(expected = BadRequest.InvalidAttributeValue.class)
  public void givenInvalidIban_whenConfirmFunds_thenException() {
    // given
    PiisToken token = new PiisToken("1");
    token.account = ParticipantAccount.createWithIbanAndCurrency("iban", "EUR");
    FundsConfirmationRequest request = new FundsConfirmationRequest(
        "code",
        ParticipantAccount.createWithIbanAndCurrency("iban1", "EUR"),
        new Amount("1.0", "USD")
    );

    // when
    testService.confirmFundsAvailability(token, request);
  }

  @Test(expected = BadRequest.InvalidAttributeValue.class)
  public void givenInvalidCurrency_whenConfirmFunds_thenException() {
    // given
    PiisToken token = new PiisToken("1");
    token.account = ParticipantAccount.createWithIbanAndCurrency("iban", "EUR");
    FundsConfirmationRequest request = new FundsConfirmationRequest(
        "code",
        ParticipantAccount.createWithIbanAndCurrency("iban", "RUB"),
        new Amount("1.0", "RUB")
    );

    // when
    testService.confirmFundsAvailability(token, request);
  }

  @Test(expected = NotFound.AccountNotFound.class)
  public void givenInvalidAccount_whenConfirmFunds_thenException() {
    // given
    PiisToken token = new PiisToken("1");
    token.account = ParticipantAccount.createWithIbanAndCurrency("iban1", "EUR");
    FundsConfirmationRequest request = new FundsConfirmationRequest(
        "code",
        ParticipantAccount.createWithIbanAndCurrency("iban1", "EUR"),
        new Amount("1.0", "RUB")
    );

    // when
    testService.confirmFundsAvailability(token, request);
  }
}
