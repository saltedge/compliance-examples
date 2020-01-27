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

import com.saltedge.connector.sdk.AuthorizationTypes;
import com.saltedge.connector.sdk.api.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.mapping.CreateTokenClientPayload;
import com.saltedge.connector.sdk.api.mapping.CreateTokenRequest;
import com.saltedge.connector.sdk.callback.mapping.SessionCallbackRequest;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreateTokenServiceTests extends TokenServicesTests {
	@Autowired
	protected CreateTokenService createTokenService;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		given(providerApi.createAndSendAuthorizationConfirmationCode("1", AuthorizationTypes.LOGIN_PASSWORD_SMS_AUTH_TYPE)).willReturn("confirmationCode");
	}

	@Test
	public void givenNullAuthType_whenStartAuthorization_thenSendSessionsFailCallback() {
		// given
		given(providerApi.getAuthorizationTypeByCode("oauth")).willReturn(null);
		CreateTokenRequest request = createTokenRequest("sessionSecret", "tppAppName", "redirectUrl", Arrays.array("kyc"), "oauth");

		// when
		createTokenService.startAuthorization(request);

		// then
		final ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
		verify(callbackService).sendFailCallback(eq("sessionSecret"), captor.capture());
		assertThat(((HttpErrorParams) captor.getValue()).getErrorClass()).isEqualTo("InvalidAuthorizationType");
		verifyNoInteractions(tokensRepository);
	}

	@Test
	public void givenOAuthAuthType_whenStartAuthorization_thenSaveTokenAndSendSessionsUpdateCallback() {
		// given
		given(providerApi.getAuthorizationPageUrl()).willReturn("http://example.com");
		CreateTokenRequest request = createTokenRequest("sessionSecret", "tppAppName", "redirectUrl", Arrays.array("kyc"), "oauth");

		// when
		createTokenService.startAuthorization(request);

		// then
		final ArgumentCaptor<SessionCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionCallbackRequest.class);
		verify(callbackService).sendUpdateCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().status).isEqualTo(Constants.STATUS_REDIRECT);
		assertThat(callbackCaptor.getValue().extra.get(Constants.KEY_REDIRECT_URL)).isEqualTo("http://example.com?session_secret=sessionSecret");

		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		verify(tokensRepository).save(tokenCaptor.capture());
		assertThat(tokenCaptor.getValue().status).isEqualTo(Token.Status.UNCONFIRMED);
		assertThat(tokenCaptor.getValue().sessionSecret).isEqualTo("sessionSecret");
	}

	@Test
	public void givenEmbeddedAuthTypeAndFailedAuthorization_whenStartAuthorization_thenSendSessionsFailCallback() {
		// given
		Map<String, String> credentials = createTestCredentials("login_password_sms");
		CreateTokenRequest request = createTokenRequest("sessionSecret", "tppAppName", null, Arrays.array("kyc"), credentials);
		given(providerApi.authorizeUser("login_password_sms", credentials)).willReturn(null);

		// when
		createTokenService.startAuthorization(request);

		// then
		final ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
		verify(callbackService).sendFailCallback(eq("sessionSecret"), captor.capture());
		assertThat(((HttpErrorParams) captor.getValue()).getErrorClass()).isEqualTo("InvalidCredentials");
		verifyNoInteractions(tokensRepository);
	}

	@Test
	public void givenEmbeddedInteractiveAuthType_whenStartAuthorization_thenSendSessionsUpdateCallback() {
		// given
		Map<String, String> credentials = createTestCredentials("login_password_sms");
		CreateTokenRequest request = createTokenRequest("sessionSecret", "tppAppName", null, Arrays.array("kyc"), credentials);
		given(providerApi.authorizeUser("login_password_sms", credentials)).willReturn("1");

		// when
		createTokenService.startAuthorization(request);

		// then
		final ArgumentCaptor<SessionCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionCallbackRequest.class);
		verify(callbackService).sendUpdateCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().status).isEqualTo(Constants.STATUS_WAITING_CONFIRMATION_CODE);
		assertThat(callbackCaptor.getValue().userId).isEqualTo("1");

		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		verify(tokensRepository).save(tokenCaptor.capture());
		assertThat(tokenCaptor.getValue().status).isEqualTo(Token.Status.UNCONFIRMED);
		assertThat(tokenCaptor.getValue().sessionSecret).isEqualTo("sessionSecret");
		assertThat(tokenCaptor.getValue().confirmationCode).isEqualTo("confirmationCode");
	}

	@Test
	public void givenEmbeddedNonInteractiveAuthType_whenStartAuthorization_thenSendSessionsSuccessCallback() {
		// given
		Map<String, String> credentials = createTestCredentials("login_password");
		CreateTokenRequest request = createTokenRequest("sessionSecret", "tppAppName", null, Arrays.array("kyc"), credentials);
		given(providerApi.authorizeUser("login_password", credentials)).willReturn("1");

		// when
		createTokenService.startAuthorization(request);

		// then
		final ArgumentCaptor<SessionCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionCallbackRequest.class);
		verify(callbackService).sendSuccessCallback(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().userId).isEqualTo("1");
		assertThat(callbackCaptor.getValue().token).isNotEmpty();
		assertThat(callbackCaptor.getValue().tokenExpiresAt).isAfter(new Date());
		assertThat(callbackCaptor.getValue().consent).isNull();

		final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
		verify(tokensRepository).save(tokenCaptor.capture());
		assertThat(tokenCaptor.getValue().status).isEqualTo(Token.Status.CONFIRMED);
		assertThat(tokenCaptor.getValue().sessionSecret).isEqualTo("sessionSecret");
		assertThat(tokenCaptor.getValue().accessToken).isEqualTo(callbackCaptor.getValue().token);
		assertThat(tokenCaptor.getValue().tokenExpiresAt).isEqualTo(callbackCaptor.getValue().tokenExpiresAt);
	}

	private Map<String, String> createTestCredentials(String authTypeCode) {
		Map<String, String> credentials = Maps.newHashMap("authorization_type", authTypeCode);
		credentials.put("login", "username");
		credentials.put("password", "secret");
		return credentials;
	}

	private CreateTokenRequest createTokenRequest(String sessionSecret,
												 String tppAppName,
												 String redirectUrl,
												 String[] scopes,
												 String authType) {
		return createTokenRequest(sessionSecret, tppAppName, redirectUrl, scopes, Maps.newHashMap("authorization_type", authType));
	}

	private CreateTokenRequest createTokenRequest(String sessionSecret,
												 String tppAppName,
												 String redirectUrl,
												 String[] scopes,
												 Map<String, String> credentials) {
		CreateTokenRequest result = new CreateTokenRequest();
		result.tppAppName = tppAppName;
		result.sessionSecret = sessionSecret;
		result.originalRequest = new CreateTokenRequest.OriginalRequest();
		result.originalRequest.payload = new CreateTokenClientPayload();
		result.originalRequest.payload.data = new CreateTokenClientPayload.Data();
		result.originalRequest.payload.data.redirectUrl = redirectUrl;
		result.originalRequest.payload.data.scopes = scopes;
		result.originalRequest.payload.data.credentials = credentials;
		return result;
	}
}
