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
package com.saltedge.connector.sdk.provider;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.api.services.tokens.ConfirmTokenService;
import com.saltedge.connector.sdk.api.services.tokens.RevokeTokenService;
import com.saltedge.connector.sdk.callback.mapping.SessionSuccessCallbackRequest;
import com.saltedge.connector.sdk.callback.services.SessionsCallbackService;
import com.saltedge.connector.sdk.callback.services.TokensCallbackService;
import com.saltedge.connector.sdk.models.Token;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.HashMap;

import static com.saltedge.connector.sdk.SDKConstants.KEY_DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConnectorCallbackServiceTests {
	@Autowired
	private ConnectorSDKCallbackService testService;
	@MockBean
	private ConfirmTokenService confirmTokenService;
	@MockBean
	private RevokeTokenService revokeTokenService;
	@MockBean
	private SessionsCallbackService sessionsCallbackService;
	@MockBean
	private TokensCallbackService tokensCallbackService;

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenIsUserConsentRequired_thenThrowConstraintViolationException() {
		testService.isUserConsentRequired("");
	}

	@Test
	public void givenNullToken_whenIsUserConsentRequired_thenReturnFalse() {
		// given
		given(confirmTokenService.findTokenBySessionSecret("sessionSecret")).willReturn(null);

		// when
		boolean result = testService.isUserConsentRequired("sessionSecret");

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void givenTokenWithGlobalConsent_whenIsUserConsentRequired_thenReturnFalse() {
		// given
		Token token = new Token();
		token.providerOfferedConsents = new ProviderConsents(ProviderConsents.GLOBAL_CONSENT_VALUE);
		given(confirmTokenService.findTokenBySessionSecret("sessionSecret")).willReturn(token);

		// when
		boolean result = testService.isUserConsentRequired("sessionSecret");

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void givenTokenWithNoConsent_whenIsUserConsentRequired_thenReturnTrue() {
		// given
		Token token = new Token();
		given(confirmTokenService.findTokenBySessionSecret("sessionSecret")).willReturn(token);

		// when
		boolean result = testService.isUserConsentRequired("sessionSecret");

		// then
		assertThat(result).isTrue();
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenOnAccountInformationAuthorizationSuccess_thenThrowConstraintViolationException() {
		testService.onAccountInformationAuthorizationSuccess(
				"",
				"",
				"",
				null,
				null);
	}

	@Test
	public void givenNullToken_whenOnAccountInformationAuthorizationSuccess_thenReturnNull() {
		// given
		ProviderConsents consent = new ProviderConsents();
		given(confirmTokenService.confirmToken(
				"sessionSecret",
				"user1",
				"accessToken",
				Instant.parse("2019-11-18T16:04:49.585Z"),
				consent
		)).willReturn(null);

		// when
		String result = testService.onAccountInformationAuthorizationSuccess(
				"sessionSecret",
				"user1",
				"accessToken",
				Instant.parse("2019-11-18T16:04:49.585Z"),
				consent
		);

		// then
		assertThat(result).isNull();
	}

	@Test
	public void givenToken_whenOnAccountInformationAuthorizationSuccess_thenConfirmTokenAndReturnRedirectUrl() {
		// given
		Token token = new Token("sessionSecret", "tppAppName", "authTypeCode", "http://redirect.to");
		ProviderConsents consent = new ProviderConsents();
		given(confirmTokenService.confirmToken(
				"sessionSecret",
				"user1",
				"accessToken",
				Instant.parse("2019-11-18T16:04:49.585Z"),
				consent
		)).willReturn(token);

		// when
		String result = testService.onAccountInformationAuthorizationSuccess(
				"sessionSecret",
				"user1",
				"accessToken",
				Instant.parse("2019-11-18T16:04:49.585Z"),
				consent
		);

		// then
		assertThat(result).isEqualTo("http://redirect.to");
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenOnAccountInformationAuthorizationFail_thenThrowConstraintViolationException() {
		testService.onAccountInformationAuthorizationFail("");
	}

	@Test
	public void givenNullToken_whenOnAccountInformationAuthorizationFail_thenReturnNull() {
		// given
		given(revokeTokenService.revokeTokenBySessionSecret("sessionSecret")).willReturn(null);

		// when
		String result = testService.onAccountInformationAuthorizationFail("sessionSecret");

		// then
		assertThat(result).isNull();
	}

	@Test
	public void givenToken_whenOnAccountInformationAuthorizationFail_thenRevokeTokenAndReturnRedirectUrl() {
		// given
		Token token = new Token("sessionSecret", "tppAppName", "authTypeCode", "http://redirect.to");
		given(revokeTokenService.revokeTokenBySessionSecret("sessionSecret")).willReturn(token);

		// when
		String result = testService.onAccountInformationAuthorizationFail("sessionSecret");

		// then
		assertThat(result).isEqualTo("http://redirect.to");
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenRevokeAccountInformationConsent_thenThrowConstraintViolationException() {
		testService.revokeAccountInformationConsent("", "");
	}

	@Test
	public void givenNullToken_whenRevokeAccountInformationConsent_thenReturnFalse() {
		// given
		given(revokeTokenService.revokeTokenByUserIdAndAccessToken("userId", "accessToken")).willReturn(null);

		// when
		boolean result = testService.revokeAccountInformationConsent("userId", "accessToken");

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void givenRevokedToken_whenRevokeAccountInformationConsent_thenSendRevokeCallbackAndReturnTrue() {
		// given
		Token token = new Token("sessionSecret", "tppAppName", "authTypeCode", "http://redirect.to");
		token.status = Token.Status.REVOKED;
		given(revokeTokenService.revokeTokenByUserIdAndAccessToken("userId", "accessToken")).willReturn(token);

		// when
		boolean result = testService.revokeAccountInformationConsent("userId", "accessToken");

		// then
		assertThat(result).isTrue();
		verify(tokensCallbackService).sendRevokeTokenCallback(eq("accessToken"));
	}

	@Test
	public void givenNotRevokedToken_whenRevokeAccountInformationConsent_thenReturnFalse() {
		// given
		Token token = new Token("sessionSecret", "tppAppName", "authTypeCode", "http://redirect.to");
		token.status = Token.Status.UNCONFIRMED;
		given(revokeTokenService.revokeTokenByUserIdAndAccessToken("userId", "accessToken")).willReturn(token);

		// when
		boolean result = testService.revokeAccountInformationConsent("userId", "accessToken");

		// then
		assertThat(result).isFalse();
		verifyNoMoreInteractions(tokensCallbackService);
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyPaymentId_whenOnPaymentInitiationAuthorizationSuccess_thenThrowConstraintViolationException() {
		// given
		HashMap<String, String> extraData = new HashMap<>();

		// when
		testService.onPaymentInitiationAuthorizationSuccess("", "user1", extraData);
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyUserId_whenOnPaymentInitiationAuthorizationSuccess_thenThrowConstraintViolationException() {
		// given
		HashMap<String, String> extraData = new HashMap<>();

		// when
		testService.onPaymentInitiationAuthorizationSuccess("paymentId", "", extraData);
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyExtra_whenOnPaymentInitiationAuthorizationSuccess_thenThrowConstraintViolationException() {
		// given
		HashMap<String, String> extraData = new HashMap<>();

		// when
		testService.onPaymentInitiationAuthorizationSuccess("paymentId", "userId", extraData);
	}

	@Test
	public void givenExtraWithoutSessionSecret_whenOnPaymentInitiationAuthorizationSuccess_thenReturnEmptyRedirect() {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(KEY_DESCRIPTION, "test");

		// when
		String result = testService.onPaymentInitiationAuthorizationSuccess("payment1", "user1", extraData);

		// then
		assertThat(result).isEqualTo("");
	}

	@Test
	public void givenExtra_whenOnPaymentInitiationAuthorizationSuccess_thenReturnRedirect() {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extraData.put(SDKConstants.KEY_RETURN_TO_URL, "http://redirect.to");

		// when
		String result = testService.onPaymentInitiationAuthorizationSuccess("payment1", "user1", extraData);

		// then
		assertThat(result).isEqualTo("http://redirect.to");
		verify(sessionsCallbackService).sendSuccessCallback(eq("sessionSecret"), eq(new SessionSuccessCallbackRequest("user1", "ACTC")));
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyPaymentId_whenOnPaymentInitiationAuthorizationFail_thenThrowConstraintViolationException() {
		// given
		HashMap<String, String> extraData = new HashMap<>();

		// when
		testService.onPaymentInitiationAuthorizationFail("", extraData);
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyExtra_whenOnPaymentInitiationAuthorizationFail_thenThrowConstraintViolationException() {
		// given
		HashMap<String, String> extraData = new HashMap<>();

		// when
		testService.onPaymentInitiationAuthorizationFail("payment1", extraData);
	}

	@Test
	public void givenExtra_whenOnPaymentInitiationAuthorizationFail_thenReturnRedirect() {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extraData.put(SDKConstants.KEY_RETURN_TO_URL, "http://redirect.to");

		// when
		String result = testService.onPaymentInitiationAuthorizationFail("payment1", extraData);

		// then
		assertThat(result).isEqualTo("http://redirect.to");
		verify(sessionsCallbackService).sendFailCallback(eq("sessionSecret"), ArgumentMatchers.any(NotFound.PaymentNotCreated.class));
	}
}
