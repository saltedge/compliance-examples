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

import com.saltedge.connector.sdk.callback.mapping.SessionCallbackRequest;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.ReconnectPolicyType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReconnectTokenServiceTests extends TokenServicesTests {
	@Autowired
	protected ReconnectTokenService reconnectTokenService;

	@Test
	public void givenConfirmedTokenAndReconnectPolicyTypeGRANT_whenReconnect_thenSendSessionsSuccessCallback() {
		// given
		Token token = createTestToken();
		given(providerApi.getReconnectPolicyType(token.userId)).willReturn(ReconnectPolicyType.GRANT);

		// when
		reconnectTokenService.reconnect(token, "sessionSecret2");

		// then
		final ArgumentCaptor<SessionCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionCallbackRequest.class);
		verify(callbackService).sendSuccessCallback(eq("sessionSecret2"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().userId).isEqualTo(token.userId);
		assertThat(callbackCaptor.getValue().token).isNotEqualTo("accessToken");
		assertThat(callbackCaptor.getValue().tokenExpiresAt).isAfter(new Date());
		assertThat(callbackCaptor.getValue().consent).isNull();

		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		verify(tokensRepository).save(tokenCaptor.capture());
		assertThat(tokenCaptor.getValue().status).isEqualTo(Token.Status.CONFIRMED);
		assertThat(tokenCaptor.getValue().sessionSecret).isEqualTo("sessionSecret2");
		assertThat(tokenCaptor.getValue().accessToken).isEqualTo(callbackCaptor.getValue().token);
		assertThat(tokenCaptor.getValue().tokenExpiresAt).isEqualTo(callbackCaptor.getValue().tokenExpiresAt);
	}

	@Test
	public void givenConfirmedTokenAndReconnectPolicyTypeMFA_whenReconnect_thenSendSessionsUpdateCallback() {
		// given
		Token token = createTestToken();
		given(providerApi.getAuthorizationPageUrl()).willReturn("http://example.com");
		given(providerApi.getReconnectPolicyType(token.userId)).willReturn(ReconnectPolicyType.MFA);

		// when
		reconnectTokenService.reconnect(token, "sessionSecret2");

		// then
		final ArgumentCaptor<SessionCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionCallbackRequest.class);
		verify(callbackService).sendUpdateCallback(eq("sessionSecret2"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().status).isEqualTo(Constants.STATUS_REDIRECT);
		assertThat(callbackCaptor.getValue().userId).isEqualTo("1");
		assertThat(callbackCaptor.getValue().extra.get(Constants.KEY_REDIRECT_URL)).isEqualTo("http://example.com?session_secret=sessionSecret2");

		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		verify(tokensRepository).save(tokenCaptor.capture());
		assertThat(tokenCaptor.getValue().status).isEqualTo(Token.Status.UNCONFIRMED);
		assertThat(tokenCaptor.getValue().sessionSecret).isEqualTo("sessionSecret2");
	}

	@Test
	public void givenConfirmedTokenAndReconnectPolicyTypeDENY_whenReconnect_thenSendSessionsFailCallback() {
		// given
		Token token = createTestToken();
		given(providerApi.getReconnectPolicyType(token.userId)).willReturn(ReconnectPolicyType.DENY);

		// when
		reconnectTokenService.reconnect(token, "sessionSecret2");

		// then
		final ArgumentCaptor<SessionCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionCallbackRequest.class);
		verify(callbackService).sendFailCallback(eq("sessionSecret1"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().userId).isEqualTo("1");
		assertThat(callbackCaptor.getValue().errorClass).isEqualTo("CannotReconnectToken");

		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		verify(tokensRepository).save(tokenCaptor.capture());
		assertThat(tokenCaptor.getValue().status).isEqualTo(Token.Status.CONFIRMED);
		assertThat(tokenCaptor.getValue().sessionSecret).isEqualTo("sessionSecret1");
	}

	private Token createTestToken() {
		Token token = new Token("1");
		token.status = Token.Status.CONFIRMED;
		token.sessionSecret = "sessionSecret1";
		token.accessToken = "accessToken";
		return token;
	}
}
