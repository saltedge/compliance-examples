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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.api.models.err.Unauthorized;
import com.saltedge.connector.sdk.callback.SessionsCallbackService;
import com.saltedge.connector.sdk.callback.TokensCallbackService;
import com.saltedge.connector.sdk.callback.mapping.SessionSuccessCallbackRequest;
import com.saltedge.connector.sdk.callback.mapping.SessionUpdateCallbackRequest;
import com.saltedge.connector.sdk.models.ConsentStatus;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.services.provider.ConfirmTokenService;
import com.saltedge.connector.sdk.services.provider.RevokeTokenByProviderService;
import com.saltedge.connector.sdk.tools.JsonTools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.validation.ConstraintViolationException;
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
	private RevokeTokenByProviderService revokeTokenService;
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
		given(confirmTokenService.findAisTokenBySessionSecret("sessionSecret")).willReturn(null);

		// when
		boolean result = testService.isUserConsentRequired("sessionSecret");

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void givenTokenWithGlobalConsent_whenIsUserConsentRequired_thenReturnFalse() {
		// given
		AisToken aisToken = new AisToken();
		aisToken.providerOfferedConsents = new ProviderConsents(ProviderConsents.GLOBAL_CONSENT_VALUE);
		given(confirmTokenService.findAisTokenBySessionSecret("sessionSecret")).willReturn(aisToken);

		// when
		boolean result = testService.isUserConsentRequired("sessionSecret");

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void givenTokenWithNoConsent_whenIsUserConsentRequired_thenReturnTrue() {
		// given
		AisToken aisToken = new AisToken();
		given(confirmTokenService.findAisTokenBySessionSecret("sessionSecret")).willReturn(aisToken);

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
				null);
	}

	@Test
	public void givenNullToken_whenOnAccountInformationAuthorizationSuccess_thenReturnNull() {
		// given
		ProviderConsents consent = new ProviderConsents();
		given(confirmTokenService.confirmAisToken(
				"sessionSecret",
				"user1",
				"accessToken",
				consent
		)).willReturn(null);

		// when
		String result = testService.onAccountInformationAuthorizationSuccess(
				"sessionSecret",
				"user1",
				"accessToken",
				consent
		);

		// then
		assertThat(result).isNull();
	}

	@Test
	public void givenToken_whenOnAccountInformationAuthorizationSuccess_thenConfirmTokenAndReturnRedirectUrl() {
		// given
		AisToken aisToken = new AisToken("sessionSecret", "tppAppName", "authTypeCode", "http://redirect.to", Instant.parse("2019-11-18T16:04:49.585Z"));
		ProviderConsents consent = new ProviderConsents();
		given(confirmTokenService.confirmAisToken(
				"sessionSecret",
				"user1",
				"accessToken",
				consent
		)).willReturn(aisToken);

		// when
		String result = testService.onAccountInformationAuthorizationSuccess(
				"sessionSecret",
				"user1",
				"accessToken",
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
	public void givenNullToken_whenOnAccountInformationAuthorizationFail_thenReturnNullAndSendFailCallback() {
		// given
		given(revokeTokenService.revokeAisTokenBySessionSecret("sessionSecret")).willReturn(null);

		// when
		String result = testService.onAccountInformationAuthorizationFail("sessionSecret");

		// then
		assertThat(result).isNull();
		verify(sessionsCallbackService).sendFailCallback(eq("sessionSecret"), eq(new Unauthorized.AccessDenied()));
	}

	@Test
	public void givenToken_whenOnAccountInformationAuthorizationFail_thenRevokeTokenAndReturnRedirectUrlAndSendFailCallback() {
		// given
		AisToken aisToken = new AisToken("userId");
		aisToken.tppRedirectUrl = "http://redirect.to";
		given(revokeTokenService.revokeAisTokenBySessionSecret("sessionSecret")).willReturn(aisToken);

		// when
		String result = testService.onAccountInformationAuthorizationFail("sessionSecret");

		// then
		assertThat(result).isEqualTo("http://redirect.to");
		verify(sessionsCallbackService).sendFailCallback(eq("sessionSecret"), eq(new Unauthorized.AccessDenied()));
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenRevokeAccountInformationConsent_thenThrowConstraintViolationException() {
		testService.revokeAccountInformationConsent("", "");
	}

	@Test
	public void givenNullToken_whenRevokeAccountInformationConsent_thenReturnFalse() {
		// given
		given(revokeTokenService.revokeAisTokenByUserIdAndAccessToken("userId", "accessToken")).willReturn(null);

		// when
		boolean result = testService.revokeAccountInformationConsent("userId", "accessToken");

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void givenRevokedToken_whenRevokeAccountInformationConsent_thenSendRevokeCallbackAndReturnTrue() {
		// given
		AisToken aisToken = new AisToken("userId");
		aisToken.status = ConsentStatus.REVOKED;
		given(revokeTokenService.revokeAisTokenByUserIdAndAccessToken("userId", "accessToken")).willReturn(aisToken);

		// when
		boolean result = testService.revokeAccountInformationConsent("userId", "accessToken");

		// then
		assertThat(result).isTrue();
		verify(tokensCallbackService).sendRevokeAisTokenCallback(eq("accessToken"));
	}

	@Test
	public void givenNotRevokedToken_whenRevokeAccountInformationConsent_thenReturnFalse() {
		// given
		AisToken aisToken = new AisToken("userId");
		aisToken.status = ConsentStatus.UNCONFIRMED;
		given(revokeTokenService.revokeAisTokenByUserIdAndAccessToken("userId", "accessToken")).willReturn(aisToken);

		// when
		boolean result = testService.revokeAccountInformationConsent("userId", "accessToken");

		// then
		assertThat(result).isFalse();
		verifyNoMoreInteractions(tokensCallbackService);
	}

	@Test
	public void updatePaymentFundsInformationTestCase1() throws JsonProcessingException {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extraData);

		// when
		testService.updatePaymentFundsInformation(true, extraJson, "PDNG");

		// then
		verify(sessionsCallbackService).sendUpdateCallback(eq("sessionSecret"), eq(new SessionUpdateCallbackRequest(true, "PDNG")));
	}

	@Test
	public void updatePaymentFundsInformationTestCase2() throws JsonProcessingException {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extraData);

		// when
		testService.updatePaymentFundsInformation(false, extraJson, "PDNG");

		// then
		verify(sessionsCallbackService).sendUpdateCallback(eq("sessionSecret"), eq(new SessionUpdateCallbackRequest(false, "PDNG")));
	}

	@Test
	public void updatePaymentFundsInformationTestCase3() throws JsonProcessingException {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extraData);

		// when
		testService.updatePaymentFundsInformation(false, extraJson, "PDNG");

		// then
		verifyNoMoreInteractions(sessionsCallbackService);
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyUserId_whenOnPaymentInitiationAuthorizationSuccess_thenThrowConstraintViolationException() {
		// when
		String result = testService.onPaymentInitiationAuthorizationSuccess("", "", "sepa-credit-transfers");

		// then
		assertThat(result).isEqualTo("");
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyExtra_whenOnPaymentInitiationAuthorizationSuccess_thenThrowConstraintViolationException() {
		// when
		String result = testService.onPaymentInitiationAuthorizationSuccess("userId", "", "sepa-credit-transfers");

		// then
		assertThat(result).isEqualTo("");
	}

	@Test
	public void givenExtraWithoutSessionSecret_whenOnPaymentInitiationAuthorizationSuccess_thenReturnEmptyRedirect() throws JsonProcessingException {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(KEY_DESCRIPTION, "test");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extraData);

		// when
		String result = testService.onPaymentInitiationAuthorizationSuccess("user1", extraJson, "sepa-credit-transfers");

		// then
		assertThat(result).isEqualTo("");
	}

	@Test
	public void givenExtra_whenOnSepaPaymentInitiationAuthorizationSuccess_thenReturnRedirect() throws JsonProcessingException {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extraData.put(SDKConstants.KEY_RETURN_TO_URL, "http://redirect.to");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extraData);

		// when
		String result = testService.onPaymentInitiationAuthorizationSuccess("user1", extraJson, "sepa-credit-transfers");

		// then
		assertThat(result).isEqualTo("http://redirect.to");
		verify(sessionsCallbackService).sendSuccessCallback(eq("sessionSecret"), eq(new SessionSuccessCallbackRequest("user1", "ACTC")));
	}

	@Test
	public void givenExtra_whenOnInstantSepaPaymentInitiationAuthorizationSuccess_thenReturnRedirect() throws JsonProcessingException {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extraData.put(SDKConstants.KEY_RETURN_TO_URL, "http://redirect.to");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extraData);

		// when
		String result = testService.onPaymentInitiationAuthorizationSuccess("user1", extraJson, "instant-sepa-credit-transfers");

		// then
		assertThat(result).isEqualTo("http://redirect.to");
		verify(sessionsCallbackService).sendSuccessCallback(eq("sessionSecret"), eq(new SessionSuccessCallbackRequest("user1", "ACCC")));
	}

	@Test
	public void givenExtra_whenOnFpsPaymentInitiationAuthorizationSuccess_thenReturnRedirect() throws JsonProcessingException {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extraData.put(SDKConstants.KEY_RETURN_TO_URL, "http://redirect.to");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extraData);

		// when
		String result = testService.onPaymentInitiationAuthorizationSuccess("user1", extraJson, "instant-sepa-credit-transfers");

		// then
		assertThat(result).isEqualTo("http://redirect.to");
		verify(sessionsCallbackService).sendSuccessCallback(eq("sessionSecret"), eq(new SessionSuccessCallbackRequest("user1", "ACSC")));
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyExtra_whenOnPaymentInitiationAuthorizationFail_thenThrowConstraintViolationException() {
		// when
		testService.onPaymentInitiationAuthorizationFail("");
	}

	@Test
	public void givenExtra_whenOnPaymentInitiationAuthorizationFail_thenReturnRedirect() throws JsonProcessingException {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extraData.put(SDKConstants.KEY_RETURN_TO_URL, "http://redirect.to");
		String extraJson = JsonTools.createDefaultMapper().writeValueAsString(extraData);

		// when
		String result = testService.onPaymentInitiationAuthorizationFail(extraJson);

		// then
		assertThat(result).isEqualTo("http://redirect.to");
		verify(sessionsCallbackService).sendFailCallback(eq("sessionSecret"), ArgumentMatchers.any(NotFound.PaymentNotCreated.class));
	}
}
