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
package com.saltedge.connector.sdk.api.services.tokens;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.api.services.BaseServicesTests;
import com.saltedge.connector.sdk.callback.mapping.SessionSuccessCallbackRequest;
import com.saltedge.connector.sdk.models.Token;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfirmTokenServiceTests extends BaseServicesTests {
	@Autowired
	protected ConfirmTokenService testService;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenConfirmToken_thenSendSessionsFailCallback() {
		testService.confirmToken("", "", "", null);
	}

	@Test
	public void givenInvalidSessionSecret_whenConfirmToken_thenReturnNull() {
		// given
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(null);

		// when
		Token result = testService.confirmToken(
				"sessionSecret",
				"userId",
				"accessToken",
				ProviderConsents.buildAllAccountsConsent());

		// then
		assertThat(result).isNull();
	}

	@Test
	public void givenValidParamsWithBankConsent_whenConfirmToken_thenReturnTokenAndSendSuccess() {
		// given
		Token token = new Token();
		token.sessionSecret = "sessionSecret";
		token.tokenExpiresAt = Instant.parse("2019-08-21T16:04:49.021Z");
		ProviderConsents providerOfferedConsents = ProviderConsents.buildAllAccountsConsent();
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(token);

		// when
		Token result = testService.confirmToken(
				"sessionSecret",
				"userId",
				"accessToken",
				providerOfferedConsents
		);

		// then
		assertThat(result).isNotNull();
		assertThat(result.sessionSecret).isEqualTo("sessionSecret");
		assertThat(result.userId).isEqualTo("userId");
		assertThat(result.status).isEqualTo(Token.Status.CONFIRMED);
		assertThat(result.accessToken).isEqualTo("accessToken");
		assertThat(result.tokenExpiresAt).isEqualTo(Instant.parse("2019-08-21T16:04:49.021Z"));
		assertThat(result.providerOfferedConsents).isEqualTo(providerOfferedConsents);
		verify(tokensRepository).save(any(Token.class));

		ArgumentCaptor<SessionSuccessCallbackRequest> captor = ArgumentCaptor.forClass(SessionSuccessCallbackRequest.class);
		verify(sessionsCallbackService).sendSuccessCallback(eq("sessionSecret"), captor.capture());
		assertThat(captor.getValue().providerOfferedConsents.balances).isEmpty();
		assertThat(captor.getValue().providerOfferedConsents.transactions).isEmpty();
		assertThat(captor.getValue().providerOfferedConsents.globalAccessConsent).isNull();
		assertThat(captor.getValue().userId).isEqualTo("userId");
		assertThat(captor.getValue().token).isEqualTo("accessToken");
	}

	@Test
	public void givenValidParamsWithNullBankConsent_whenConfirmToken_thenReturnTokenAndSendSuccess() {
		// given
		Token token = new Token();
		token.tokenExpiresAt = null;
		token.sessionSecret = "sessionSecret";
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(token);

		// when
		Token result = testService.confirmToken(
				"sessionSecret",
				"userId",
				"accessToken",
				null
		);

		// then
		assertThat(result).isNotNull();
		assertThat(result.sessionSecret).isEqualTo("sessionSecret");
		assertThat(result.userId).isEqualTo("userId");
		assertThat(result.status).isEqualTo(Token.Status.CONFIRMED);
		assertThat(result.accessToken).isEqualTo("accessToken");
		assertThat(result.tokenExpiresAt)
				.isCloseTo(Instant.now().plus(SDKConstants.CONSENT_MAX_PERIOD, ChronoUnit.DAYS), byLessThan(100, ChronoUnit.MILLIS));
		assertThat(result.providerOfferedConsents).isEqualTo(ProviderConsents.buildAllAccountsConsent());
		verify(tokensRepository).save(any(Token.class));

		ArgumentCaptor<SessionSuccessCallbackRequest> captor = ArgumentCaptor.forClass(SessionSuccessCallbackRequest.class);
		verify(sessionsCallbackService).sendSuccessCallback(eq("sessionSecret"), captor.capture());
		assertThat(captor.getValue().providerOfferedConsents).isEqualTo(ProviderConsents.buildAllAccountsConsent());
		assertThat(captor.getValue().userId).isEqualTo("userId");
		assertThat(captor.getValue().token).isEqualTo("accessToken");
	}

	@Test
	public void givenValidParamsWithGlobalConsent_whenConfirmToken_thenReturnTokenAndSendSuccess() {
		// given
		ProviderConsents globalConsents = new ProviderConsents(ProviderConsents.GLOBAL_CONSENT_VALUE);
		Token token = new Token();
		token.providerOfferedConsents = globalConsents;
		token.sessionSecret = "sessionSecret";
		token.tokenExpiresAt = Instant.parse("2019-08-21T16:04:49.021Z");
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(token);

		// when
		Token result = testService.confirmToken(
				"sessionSecret",
				"userId",
				"accessToken",
				null
		);

		// then
		assertThat(result).isNotNull();
		assertThat(result.sessionSecret).isEqualTo("sessionSecret");
		assertThat(result.userId).isEqualTo("userId");
		assertThat(result.status).isEqualTo(Token.Status.CONFIRMED);
		assertThat(result.accessToken).isEqualTo("accessToken");
		assertThat(result.tokenExpiresAt).isEqualTo(Instant.parse("2019-08-21T16:04:49.021Z"));
		assertThat(result.providerOfferedConsents).isEqualTo(globalConsents);
		verify(tokensRepository).save(any(Token.class));

		ArgumentCaptor<SessionSuccessCallbackRequest> captor = ArgumentCaptor.forClass(SessionSuccessCallbackRequest.class);
		verify(sessionsCallbackService).sendSuccessCallback(eq("sessionSecret"), captor.capture());
		assertThat(captor.getValue().providerOfferedConsents).isEqualTo(globalConsents);
		assertThat(captor.getValue().userId).isEqualTo("userId");
		assertThat(captor.getValue().token).isEqualTo("accessToken");
	}
}
