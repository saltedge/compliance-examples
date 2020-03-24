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
import com.saltedge.connector.sdk.api.err.NotFound;
import com.saltedge.connector.sdk.api.services.tokens.ConfirmTokenService;
import com.saltedge.connector.sdk.api.services.tokens.RevokeTokenService;
import com.saltedge.connector.sdk.callback.mapping.SessionSuccessCallbackRequest;
import com.saltedge.connector.sdk.callback.services.SessionsCallbackService;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.ProviderOfferedConsents;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;

import static com.saltedge.connector.sdk.SDKConstants.KEY_DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConnectorCallbackServiceTests {
	@Autowired
	private ConnectorCallbackService testService;
	@MockBean
	private ConfirmTokenService confirmTokenService;
	@MockBean
	private RevokeTokenService revokeTokenService;
	@MockBean
	private SessionsCallbackService callbackService;

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenOnOAuthAuthorizationSuccess_thenThrowConstraintViolationException() {
		testService.onOAuthAccountsAuthorizationSuccess("", "", null);
	}

	@Test
	public void givenNullToken_whenOnOAuthAuthorizationSuccess_thenReturnNull() {
		// given
		ProviderOfferedConsents consent = new ProviderOfferedConsents();
		given(confirmTokenService.confirmToken("sessionSecret", "user1", consent)).willReturn(null);

		// when
		String result = testService.onOAuthAccountsAuthorizationSuccess("sessionSecret", "user1", consent);

		// then
		assertThat(result).isNull();
	}

	@Test
	public void givenToken_whenOnOAuthAuthorizationSuccess_thenConfirmTokenAndReturnRedirectUrl() {
		// given
		Token token = new Token("sessionSecret", "tppAppName", "authTypeCode", "http://redirect.to");
		ProviderOfferedConsents consent = new ProviderOfferedConsents();
		given(confirmTokenService.confirmToken("sessionSecret", "user1", consent)).willReturn(token);

		// when
		String result = testService.onOAuthAccountsAuthorizationSuccess("sessionSecret", "user1", consent);

		// then
		assertThat(result).isEqualTo("http://redirect.to");
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenOnOAuthAuthorizationError_thenThrowConstraintViolationException() {
		testService.onOAuthAccountsAuthorizationError("");
	}

	@Test
	public void givenNullToken_whenOnOAuthAuthorizationError_thenReturnNull() {
		// given
		ProviderOfferedConsents consent = new ProviderOfferedConsents();
		given(revokeTokenService.revokeTokenBySessionSecret("sessionSecret")).willReturn(null);

		// when
		String result = testService.onOAuthAccountsAuthorizationError("sessionSecret");

		// then
		assertThat(result).isNull();
	}

	@Test
	public void givenToken_whenOnOAuthAuthorizationError_thenRevokeTokenAndReturnRedirectUrl() {
		// given
		Token token = new Token("sessionSecret", "tppAppName", "authTypeCode", "http://redirect.to");
		given(revokeTokenService.revokeTokenBySessionSecret("sessionSecret")).willReturn(token);

		// when
		String result = testService.onOAuthAccountsAuthorizationError("sessionSecret");

		// then
		assertThat(result).isEqualTo("http://redirect.to");
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyPaymentId_whenOnOAuthPaymentAuthorizationSuccess_thenThrowConstraintViolationException() {
		// given
		HashMap<String, String> extraData = new HashMap<>();

		// when
		testService.onOAuthPaymentAuthorizationSuccess("", "user1", extraData);
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyUserId_whenOnOAuthPaymentAuthorizationSuccess_thenThrowConstraintViolationException() {
		// given
		HashMap<String, String> extraData = new HashMap<>();

		// when
		testService.onOAuthPaymentAuthorizationSuccess("paymentId", "", extraData);
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyExtra_whenOnOAuthPaymentAuthorizationSuccess_thenThrowConstraintViolationException() {
		// given
		HashMap<String, String> extraData = new HashMap<>();

		// when
		testService.onOAuthPaymentAuthorizationSuccess("paymentId", "userId", extraData);
	}

	@Test
	public void givenExtraWithoutSessionSecret_whenOnOAuthPaymentAuthorizationSuccess_thenReturnEmptyRedirect() {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(KEY_DESCRIPTION, "test");

		// when
		String result = testService.onOAuthPaymentAuthorizationSuccess("payment1", "user1", extraData);

		// then
		assertThat(result).isEqualTo("");
	}

	@Test
	public void givenExtra_whenOnOAuthPaymentAuthorizationSuccess_thenReturnRedirect() {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extraData.put(SDKConstants.KEY_RETURN_TO_URL, "http://redirect.to");

		// when
		String result = testService.onOAuthPaymentAuthorizationSuccess("payment1", "user1", extraData);

		// then
		assertThat(result).isEqualTo("http://redirect.to");
		verify(callbackService).sendSuccessCallback(eq("sessionSecret"), eq(new SessionSuccessCallbackRequest("user1", "ACTC")));
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyPaymentId_whenOAuthPaymentAuthorizationFail_thenThrowConstraintViolationException() {
		// given
		HashMap<String, String> extraData = new HashMap<>();

		// when
		testService.onOAuthPaymentAuthorizationFail("", extraData);
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenEmptyExtra_whenOAuthPaymentAuthorizationFail_thenThrowConstraintViolationException() {
		// given
		HashMap<String, String> extraData = new HashMap<>();

		// when
		testService.onOAuthPaymentAuthorizationFail("payment1", extraData);
	}

	@Test
	public void givenExtra_whenOAuthPaymentAuthorizationFail_thenReturnRedirect() {
		// given
		HashMap<String, String> extraData = new HashMap<>();
		extraData.put(SDKConstants.KEY_SESSION_SECRET, "sessionSecret");
		extraData.put(SDKConstants.KEY_RETURN_TO_URL, "http://redirect.to");

		// when
		String result = testService.onOAuthPaymentAuthorizationFail("payment1", extraData);

		// then
		assertThat(result).isEqualTo("http://redirect.to");
		verify(callbackService).sendFailCallback(eq("sessionSecret"), ArgumentMatchers.any(NotFound.PaymentNotCreated.class));
	}
}
