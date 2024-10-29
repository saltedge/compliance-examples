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
import com.saltedge.connector.sdk.api.models.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.models.requests.CreateAisTokenRequest;
import com.saltedge.connector.sdk.api.services.BaseServicesTests;
import com.saltedge.connector.sdk.callback.mapping.SessionUpdateCallbackRequest;
import com.saltedge.connector.sdk.models.ConsentStatus;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.services.priora.CreateAisTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
public class CreateAisTokenServiceTests extends BaseServicesTests {
	@Autowired
	protected CreateAisTokenService testService;

	private final String psuIpAddress = "192.168.0.1";
	private final LocalDate validUntil = LocalDate.parse("2030-12-31");

	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void givenNullAuthType_whenStartAuthorization_thenSendSessionsFailCallback() {
		// given
		given(providerService.getAuthorizationTypes()).willReturn(Collections.emptyList());
		CreateAisTokenRequest request = createTokenRequest(ProviderConsents.buildAllAccountsConsent());

		// when
		testService.startAuthorization(request);

		// then
		final ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
		verify(sessionsCallbackService).sendFailCallbackAsync(eq("sessionSecret"), captor.capture());
		assertThat(((HttpErrorParams) captor.getValue()).getErrorClass()).isEqualTo("InvalidAuthorizationType");
		verifyNoInteractions(aisTokensRepository);
	}

	@Test
	public void givenOAuthAuthTypeAndBankConsent_whenStartAuthorization_thenSaveTokenWithoutConsentAndSendSessionsUpdateCallback() {
		// given
		given(providerService.getAccountInformationAuthorizationPageUrl("sessionSecret", true, psuIpAddress))
				.willReturn("http://example.com?session_secret=sessionSecret");
		ProviderConsents providerConsents = ProviderConsents.buildAllAccountsConsent();
		CreateAisTokenRequest request = createTokenRequest(providerConsents);
		request.authorizationType = "oauth";

		// when
		testService.startAuthorization(request);

		// then
		final ArgumentCaptor<SessionUpdateCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionUpdateCallbackRequest.class);
		verify(sessionsCallbackService).sendUpdateCallbackAsync(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().status).isEqualTo(SDKConstants.STATUS_RECEIVED);
		assertThat(callbackCaptor.getValue().redirectUrl).isEqualTo("http://example.com?session_secret=sessionSecret");

		final ArgumentCaptor<AisToken> tokenCaptor = ArgumentCaptor.forClass(AisToken.class);
		verify(aisTokensRepository).save(tokenCaptor.capture());
		assertThat(tokenCaptor.getValue().status).isEqualTo(ConsentStatus.UNCONFIRMED);
		assertThat(tokenCaptor.getValue().sessionSecret).isEqualTo("sessionSecret");
		assertThat(tokenCaptor.getValue().providerOfferedConsents).isEqualTo(providerConsents);
		assertThat(tokenCaptor.getValue().tokenExpiresAt.toString()).isEqualTo("2030-12-31T23:59:59.999Z");
	}

	@Test
	public void givenOAuthAuthTypeAndGlobalConsent_whenStartAuthorization_thenSaveTokenWithConsentAndSendSessionsUpdateCallback() {
		// given
		given(providerService.getAccountInformationAuthorizationPageUrl("sessionSecret", false, psuIpAddress))
				.willReturn("http://example.com?session_secret=sessionSecret");
		CreateAisTokenRequest request = createTokenRequest(new ProviderConsents("allAccounts"));
		request.authorizationType = "oauth";

		// when
		testService.startAuthorization(request);

		// then
		final ArgumentCaptor<SessionUpdateCallbackRequest> callbackCaptor = ArgumentCaptor.forClass(SessionUpdateCallbackRequest.class);
		verify(sessionsCallbackService).sendUpdateCallbackAsync(eq("sessionSecret"), callbackCaptor.capture());
		assertThat(callbackCaptor.getValue().status).isEqualTo(SDKConstants.STATUS_RECEIVED);
		assertThat(callbackCaptor.getValue().redirectUrl).isEqualTo("http://example.com?session_secret=sessionSecret");

		final ArgumentCaptor<AisToken> tokenCaptor = ArgumentCaptor.forClass(AisToken.class);
		verify(aisTokensRepository).save(tokenCaptor.capture());
		assertThat(tokenCaptor.getValue().status).isEqualTo(ConsentStatus.UNCONFIRMED);
		assertThat(tokenCaptor.getValue().sessionSecret).isEqualTo("sessionSecret");
		assertThat(tokenCaptor.getValue().providerOfferedConsents).isNotNull();
		assertThat(tokenCaptor.getValue().tokenExpiresAt.toString()).isEqualTo("2030-12-31T23:59:59.999Z");
	}

	private CreateAisTokenRequest createTokenRequest(ProviderConsents requestedConsent) {
		CreateAisTokenRequest result = new CreateAisTokenRequest();
		result.tppAppName = "tppAppName";
		result.sessionSecret = "sessionSecret";
		result.redirectUrl = "redirectUrl";
		result.requestedConsent = requestedConsent;
		result.validUntil = validUntil;
		result.recurringIndicator = true;
		result.psuIpAddress = psuIpAddress;
		return result;
	}
}
