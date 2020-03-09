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

import com.saltedge.connector.sdk.callback.mapping.SessionSuccessCallbackRequest;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.ProviderOfferedConsents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfirmTokenServiceTests extends TokenServicesTests {
	@Autowired
	protected ConfirmTokenService testService;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenConfirmToken_thenSendSessionsFailCallback() {
		testService.confirmToken("", "", null);
	}

	@Test
	public void givenInvalidSessionSecret_whenConfirmToken_thenReturnNull() {
		// given
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(null);

		// when
		Token result = testService.confirmToken("sessionSecret", "userId", new ProviderOfferedConsents());

		// then
		assertThat(result).isNull();
	}

	@Test
	public void givenValidParams_whenConfirmToken_thenReturnTokenAndSendSuccess() {
		// given
		Token token = new Token();
		token.sessionSecret = "sessionSecret";
		ProviderOfferedConsents providerOfferedConsents = new ProviderOfferedConsents();
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(token);

		// when
		Token result = testService.confirmToken("sessionSecret", "userId", providerOfferedConsents);

		// then
		assertThat(result).isNotNull();
		assertThat(result.sessionSecret).isEqualTo("sessionSecret");
		assertThat(result.userId).isEqualTo("userId");
		assertThat(result.status).isEqualTo(Token.Status.CONFIRMED);
		assertThat(result.accessToken).isNotEmpty();
		assertThat(result.tokenExpiresAt).isAfter(new Date());
		assertThat(result.providerOfferedConsents).isEqualTo(providerOfferedConsents);
		verify(tokensRepository).save(any(Token.class));
		ArgumentCaptor<SessionSuccessCallbackRequest> captor = ArgumentCaptor.forClass(SessionSuccessCallbackRequest.class);
		verify(callbackService).sendSuccessCallback(eq("sessionSecret"), captor.capture());
		assertThat(captor.getValue().providerOfferedConsents).isEqualTo(providerOfferedConsents);
		assertThat(captor.getValue().token).isNotEmpty();
		assertThat(captor.getValue().userId).isEqualTo("userId");
	}
}
