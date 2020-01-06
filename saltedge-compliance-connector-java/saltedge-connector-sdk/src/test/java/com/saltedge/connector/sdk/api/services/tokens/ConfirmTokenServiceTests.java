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

import com.saltedge.connector.sdk.api.err.BadRequest;
import com.saltedge.connector.sdk.api.err.NotFound;
import com.saltedge.connector.sdk.callback.mapping.SessionCallbackRequest;
import com.saltedge.connector.sdk.models.persistence.Token;
import org.assertj.core.util.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfirmTokenServiceTests extends TokenServicesTests {
	@Autowired
	protected ConfirmTokenService confirmTokenService;

	@Test(expected = NotFound.TokenNotFound.class)
	public void givenNoToken_whenConfirmToken_thenRiseTokenNotFoundException() {
		// given
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(null);

		// when
		confirmTokenService.confirmToken("sessionSecret", new HashMap<>());
	}

	@Test
	public void givenTokenWithEmptyConfirmCode_whenConfirmToken_thenUpdateTokenWithAccessToken() {
		// given
		Token token = new Token("1");
		token.sessionSecret = "sessionSecret";
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(token);

		// when
		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		final ArgumentCaptor<SessionCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionCallbackRequest.class);
		confirmTokenService.confirmToken("sessionSecret", new HashMap<>());

		// then
		verify(tokensRepository).save(tokenCaptor.capture());
		Token savedToken = tokenCaptor.getValue();
		assertThat(savedToken.status).isEqualTo(Token.Status.CONFIRMED);
		assertThat(savedToken.accessToken).isNotEmpty();
		assertThat(savedToken.isExpired()).isEqualTo(false);

		verify(callbackService).sendSuccessCallback(eq("sessionSecret"), callbackCaptor.capture());
		SessionCallbackRequest callbackParams = callbackCaptor.getValue();
		assertThat(callbackParams.userId).isEqualTo("1");
		assertThat(callbackParams.providerCode).isEqualTo("spring_connector_example_md");
		assertThat(callbackParams.token).isEqualTo(savedToken.accessToken);
		assertThat(callbackParams.tokenExpiresAt).isEqualTo(savedToken.tokenExpiresAt);
	}

	@Test
	public void givenTokenWithConfirmCode_whenConfirmToken_thenSendSessionsSuccessCallback() {
		// given
		Token token = new Token("1");
		token.sessionSecret = "sessionSecret3";
		token.confirmationCode = "123456";
		token.authTypeCode = "login_password_sms";
		given(tokensRepository.findFirstBySessionSecret("sessionSecret3")).willReturn(token);

		// when
		confirmTokenService.confirmToken("sessionSecret3", Maps.newHashMap("sms", "123456"));

		// then
		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		verify(tokensRepository).save(tokenCaptor.capture());
		Token savedToken = tokenCaptor.getValue();
		assertThat(savedToken.status).isEqualTo(Token.Status.CONFIRMED);
		assertThat(savedToken.accessToken).isNotEmpty();

		final ArgumentCaptor<SessionCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionCallbackRequest.class);
		verify(callbackService).sendSuccessCallback(eq("sessionSecret3"), callbackCaptor.capture());
		SessionCallbackRequest callbackParams = callbackCaptor.getValue();
		assertThat(callbackParams.userId).isEqualTo("1");
		assertThat(callbackParams.token).isEqualTo(savedToken.accessToken);
	}

	@Test
	public void givenTokenWithConfirmCode_whenConfirmTokenWithWrongConfirmCode_thenRiseInvalidConfirmationCodeException() {
		// given
		Token token = new Token("1");
		token.sessionSecret = "sessionSecret";
		token.confirmationCode = "123456";
		token.authTypeCode = "login_password_sms";
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(token);

		// when
		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		try {
			confirmTokenService.confirmToken("sessionSecret", Maps.newHashMap("sms", "654321"));
		} catch (BadRequest.InvalidConfirmationCode e) {
			assertThat(e).isInstanceOf(BadRequest.InvalidConfirmationCode.class);
		}

		// then
		verify(tokensRepository).save(tokenCaptor.capture());
		verify(callbackService).sendFailCallback(anyString(), any(Exception.class));
		Token savedToken = tokenCaptor.getValue();
		assertThat(savedToken.status).isEqualTo(Token.Status.REVOKED);
		assertThat(savedToken.accessToken).isNull();
	}

	@Test
	public void givenInvalidParams_whenUserConfirmed_thenReturnNull() {
		// given
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(null);

		// then
		assertThat(confirmTokenService.confirmToken(null, null, null)).isNull();
		assertThat(confirmTokenService.confirmToken("sessionSecret", "1", new ArrayList<>())).isNull();
	}

	@Test
	public void givenToken_whenUserConfirmed_thenReturnConfirmedToken() {
		// given
		Token token = new Token("1");
		token.sessionSecret = "sessionSecret";
		token.confirmationCode = "123456";
		token.authTypeCode = "login_password_sms";
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(token);

		// when
		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		confirmTokenService.confirmToken("sessionSecret", "1", null);

		// then
		verify(tokensRepository).save(tokenCaptor.capture());
		Token savedToken = tokenCaptor.getValue();
		assertThat(savedToken.status).isEqualTo(Token.Status.CONFIRMED);
		assertThat(savedToken.accessToken).isNotEmpty();
		assertThat(savedToken.fetchConsents).isNull();
		verify(callbackService).sendSuccessCallback(eq("sessionSecret"), any(SessionCallbackRequest.class));
	}
}
