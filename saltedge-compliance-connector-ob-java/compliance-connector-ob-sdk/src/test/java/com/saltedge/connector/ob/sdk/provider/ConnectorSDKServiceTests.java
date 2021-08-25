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
package com.saltedge.connector.ob.sdk.provider;

import com.saltedge.connector.ob.sdk.api.services.ObAuthorizationService;
import com.saltedge.connector.ob.sdk.api.services.ObPaymentService;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.model.jpa.ConsentsRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.InvalidParameterException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class ConnectorSDKServiceTests {
	@Autowired
	private ConnectorSDKService testService;
	@MockBean
	private ConsentsRepository mockConsentsRepository;
	@MockBean
	private ObAuthorizationService mockAuthorizationService;
	@MockBean
	private ObPaymentService mockPaymentService;

	@Test
	public void whenOnUserInitiateConsentAuthorization_thenReturnRedirectUrl() {
		//given
		given(mockAuthorizationService.createAuthorization("authorizeUrl", "authCode", null)).willReturn("redirectUrl");

		//when
		String result = testService.onUserInitiateConsentAuthorization("authorizeUrl", "authCode", null);

		//then
		assertThat(result).isEqualTo("redirectUrl");
	}

	@Test
	public void whenGetConsent_thenReturnConsent() {
		//given
		Consent consent = new Consent();
		given(mockConsentsRepository.findFirstByAuthCode("authCode")).willReturn(consent);

		//when
		Consent result = testService.getConsent("authCode");

		//then
		assertThat(result).isEqualTo(consent);
	}

	@Test
	public void givenAuthCodeAndUserId_whenOnConsentApprove_thenReturnRedirectUrl() {
		//given
		given(mockAuthorizationService.updateAuthorization("authCode", "userId", "approved", null)).willReturn("redirectUrl");

		//when
		String result = testService.onConsentApprove("authCode", "userId");

		//then
		assertThat(result).isEqualTo("redirectUrl");
	}

	@Test
	public void givenAuthCodeAndUserIdAndIdentifiers_whenOnConsentApprove_thenReturnRedirectUrl() {
		//given
		List<String> identifiers = Lists.list("identifier");
		given(mockAuthorizationService.updateAuthorization("authCode", "userId", "approved", identifiers)).willReturn("redirectUrl");

		//when
		String result = testService.onConsentApprove("authCode", "userId", identifiers);

		//then
		assertThat(result).isEqualTo("redirectUrl");
	}

	@Test
	public void givenAuthCodeAndUserId_whenOnConsentDeny_thenReturnRedirectUrl() {
		//given
		given(mockAuthorizationService.updateAuthorization("authCode", "userId", "denied", null)).willReturn("redirectUrl");

		//when
		String result = testService.onConsentDeny("authCode", "userId");

		//then
		assertThat(result).isEqualTo("redirectUrl");
	}

	@Test
	public void givenValidStatus_whenUpdatePayment_thenCallPaymentService() {
		for (String status : ConnectorSDKService.VALID_PAYMENT_STATUSES) {
			//when
			testService.onPaymentStatusUpdate("paymentId", status);

			//then
			Mockito.verify(mockPaymentService).updatePayment("paymentId", status);
		}
	}

	@Test
	public void givenInvalidStatus_whenUpdatePayment_thenThrowException() {
		assertThrows(InvalidParameterException.class, () -> testService.onPaymentStatusUpdate("paymentId", "approved"));
	}
}
