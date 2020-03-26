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

import com.saltedge.connector.sdk.api.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.mapping.CreateTokenRequest;
import com.saltedge.connector.sdk.api.services.BaseServicesTests;
import com.saltedge.connector.sdk.callback.mapping.SessionUpdateCallbackRequest;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.models.persistence.Token;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreateTokenServiceTests extends BaseServicesTests {
	@Autowired
	protected CreateTokenService testService;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void givenNullAuthType_whenStartAuthorization_thenSendSessionsFailCallback() {
		// given
		given(providerService.getAuthorizationTypeByCode("oauth")).willReturn(null);
		CreateTokenRequest request = createTokenRequest("sessionSecret", "tppAppName", "redirectUrl");

		// when
		testService.startAuthorization(request);

		// then
		final ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
		verify(callbackService).sendFailCallback(eq("sessionSecret"), captor.capture());
		assertThat(((HttpErrorParams) captor.getValue()).getErrorClass()).isEqualTo("InvalidAuthorizationType");
		verifyNoInteractions(tokensRepository);
	}

	@Test
	public void givenOAuthAuthType_whenStartAuthorization_thenSaveTokenAndSendSessionsUpdateCallback() {
		// given
		given(providerService.getAccountInformationAuthorizationPageUrl("sessionSecret")).willReturn("http://example.com?session_secret=sessionSecret");
		CreateTokenRequest request = createTokenRequest("sessionSecret", "tppAppName", "redirectUrl");
		request.authorizationType = "oauth";

		// when
		testService.startAuthorization(request);

		// then
		final ArgumentCaptor<SessionUpdateCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionUpdateCallbackRequest.class);
		verify(callbackService).sendUpdateCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().status).isEqualTo(SDKConstants.STATUS_REDIRECT);
		assertThat(callbackCaptor.getValue().redirectUrl).isEqualTo("http://example.com?session_secret=sessionSecret");

		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		verify(tokensRepository).save(tokenCaptor.capture());
		assertThat(tokenCaptor.getValue().status).isEqualTo(Token.Status.UNCONFIRMED);
		assertThat(tokenCaptor.getValue().sessionSecret).isEqualTo("sessionSecret");
	}

	private CreateTokenRequest createTokenRequest(
			String sessionSecret,
			String tppAppName,
			String redirectUrl
	) {
		CreateTokenRequest result = new CreateTokenRequest();
		result.tppAppName = tppAppName;
		result.sessionSecret = sessionSecret;
		result.redirectUrl = redirectUrl;
		return result;
	}
}
