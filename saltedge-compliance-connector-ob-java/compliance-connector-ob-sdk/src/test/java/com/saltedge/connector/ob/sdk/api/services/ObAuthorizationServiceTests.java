/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2021 Salt Edge.
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
package com.saltedge.connector.ob.sdk.api.services;

import com.saltedge.connector.ob.sdk.api.models.errors.NotFound;
import com.saltedge.connector.ob.sdk.api.models.request.AuthorizationCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.AuthorizationUpdateRequest;
import com.saltedge.connector.ob.sdk.api.models.response.AuthorizationsCreateResponse;
import com.saltedge.connector.ob.sdk.api.models.response.AuthorizationsUpdateResponse;
import com.saltedge.connector.ob.sdk.callback.ConnectorCallbackService;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.model.jpa.ConsentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class ObAuthorizationServiceTests {
	@MockBean
	protected ConsentsRepository mockConsentsRepository;
	@MockBean
	protected ConnectorCallbackService mockCallbackService;
	@Autowired
	protected ObAuthorizationService testService;

	@BeforeEach
	public void setUp() throws Exception {
		Consent newConsent = new Consent("tppName", "consentId", "AwaitingAuthorization");
		given(mockConsentsRepository.findFirstByConsentId("consentId")).willReturn(newConsent);

		Consent awaitingAuthorizationConsent = new Consent("tppName", "consentId", "AwaitingAuthorization");
		awaitingAuthorizationConsent.authorizationId = "authorizationId";
		awaitingAuthorizationConsent.authCode = "authCode";
		awaitingAuthorizationConsent.accessToken = "accessToken";
		given(mockConsentsRepository.findFirstByAuthCode("authCode")).willReturn(awaitingAuthorizationConsent);
	}

	//createAuthorization(...)

	@Test
	public void givenCallbackResponseWithAccessToken_whenCreateAuthorization_thenSendCallbackAndUpdateConsent() {
		//given
		Instant authCodeExp = Instant.now();
		AuthorizationCreateRequest callbackRequest = new AuthorizationCreateRequest("authorizeUrl", "authCode", authCodeExp);
		AuthorizationsCreateResponse callbackResponse = new AuthorizationsCreateResponse();
		callbackResponse.data = new AuthorizationsCreateResponse.Data("authorizationId", "consentId", "accessToken");

		given(mockCallbackService.createAuthorization(callbackRequest)).willReturn(callbackResponse);

		//when
		String result = testService.createAuthorization("authorizeUrl", "authCode", authCodeExp);

		//then
		final ArgumentCaptor<Consent> captor = ArgumentCaptor.forClass(Consent.class);
		Mockito.verify(mockConsentsRepository).save(captor.capture());
		assertThat(captor.getValue().authCode).isEqualTo("authCode");
		assertThat(captor.getValue().accessToken).isEqualTo("accessToken");
		assertThat(captor.getValue().authorizationId).isEqualTo("authorizationId");
		assertThat(result).isNull();
	}

	@Test
	public void givenCallbackResponseWithReturnUrl_whenCreateAuthorization_thenSendCallbackAndUpdateConsent() {
		//given
		Instant authCodeExp = Instant.now();
		AuthorizationCreateRequest callbackRequest = new AuthorizationCreateRequest("authorizeUrl", "authCode", authCodeExp);
		AuthorizationsCreateResponse callbackResponse = new AuthorizationsCreateResponse();
		callbackResponse.data = new AuthorizationsCreateResponse.Data("redirectUri");

		given(mockCallbackService.createAuthorization(callbackRequest)).willReturn(callbackResponse);

		//when
		String result = testService.createAuthorization("authorizeUrl", "authCode", authCodeExp);

		//then
		Mockito.verifyNoMoreInteractions(mockConsentsRepository);
		assertThat(result).isEqualTo("redirectUri");
	}

	@Test
	public void givenInvalidCallbackResponse_whenCreateAuthorization_thenConsentNotUpdated() {
		//given
		Instant authCodeExp = Instant.now();
		AuthorizationCreateRequest callbackRequest = new AuthorizationCreateRequest("authorizeUrl", "authCode", Instant.now());

		given(mockCallbackService.createAuthorization(callbackRequest)).willReturn(null);

		//when
		assertThrows(NotFound.ConsentNotFound.class, () -> testService.createAuthorization("authorizeUrl", "authCode", authCodeExp));

		//then
		Mockito.verifyNoMoreInteractions(mockConsentsRepository);
	}

	@Test
	public void givenInvalidCallbackResponseData_whenCreateAuthorization_thenConsentNotUpdated() {
		//given
		Instant authCodeExp = Instant.now();
		AuthorizationCreateRequest callbackRequest = new AuthorizationCreateRequest("authorizeUrl", "authCode", authCodeExp);
		AuthorizationsCreateResponse callbackResponse = new AuthorizationsCreateResponse();

		given(mockCallbackService.createAuthorization(callbackRequest)).willReturn(callbackResponse);

		//when
		assertThrows(NotFound.ConsentNotFound.class, () -> testService.createAuthorization("authorizeUrl", "authCode", authCodeExp));

		//then
		Mockito.verifyNoMoreInteractions(mockConsentsRepository);
	}

	//updateAuthorization(...)

	@Test
	public void givenCallbackResponseWithUserId_whenUpdateAuthorization_thenSendCallbackAndUpdateConsent() {
		//given
		AuthorizationUpdateRequest callbackRequest = new AuthorizationUpdateRequest("userId", "approved", Collections.emptyList());
		AuthorizationsUpdateResponse callbackResponse = new AuthorizationsUpdateResponse();
		AuthorizationsUpdateResponse.Data data = new AuthorizationsUpdateResponse.Data("authorizationId", "Authorised", "accessToken");
		data.redirectUri = "redirectUri";
		callbackResponse.data = data;

		given(mockCallbackService.updateAuthorization("authorizationId", callbackRequest)).willReturn(callbackResponse);

		//when
		String result = testService.updateAuthorization("authCode", "userId", "approved", Collections.emptyList());

		//then
		final ArgumentCaptor<Consent> captor = ArgumentCaptor.forClass(Consent.class);
		Mockito.verify(mockConsentsRepository).save(captor.capture());
		assertThat(captor.getValue().userId).isEqualTo("userId");
		assertThat(captor.getValue().accountIdentifiers).isEqualTo(Collections.emptyList());
		assertThat(captor.getValue().status).isEqualTo("Authorised");
		assertThat(result).isEqualTo("redirectUri");
	}
}
