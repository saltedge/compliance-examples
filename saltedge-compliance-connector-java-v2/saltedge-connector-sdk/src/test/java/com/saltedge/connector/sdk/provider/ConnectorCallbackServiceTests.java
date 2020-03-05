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

import com.saltedge.connector.sdk.api.services.tokens.ConfirmTokenService;
import com.saltedge.connector.sdk.api.services.tokens.RevokeTokenService;
import com.saltedge.connector.sdk.models.persistence.Token;
import com.saltedge.connector.sdk.provider.models.ProviderOfferedConsents;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConnectorCallbackServiceTests {
	@Autowired
	private ConnectorCallbackService testService;
	@MockBean
	private ConfirmTokenService confirmTokenService;
	@MockBean
	private RevokeTokenService revokeTokenService;

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenOnOAuthAuthorizationSuccess_thenThrowConstraintViolationException() {
		testService.onOAuthAuthorizationSuccess("", "", null);
	}

	@Test
	public void givenNullToken_whenOnOAuthAuthorizationSuccess_thenReturnNull() {
		// given
		ProviderOfferedConsents consent = new ProviderOfferedConsents();
		given(confirmTokenService.confirmToken("sessionSecret", "user1", consent)).willReturn(null);

		// when
		String result = testService.onOAuthAuthorizationSuccess("sessionSecret", "user1", consent);

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
		String result = testService.onOAuthAuthorizationSuccess("sessionSecret", "user1", consent);

		// then
		assertThat(result).isEqualTo("http://redirect.to");
	}

	@Test(expected = ConstraintViolationException.class)
	public void givenInvalidParams_whenOnOAuthAuthorizationError_thenThrowConstraintViolationException() {
		testService.onOAuthAuthorizationError("");
	}

	@Test
	public void givenNullToken_whenOnOAuthAuthorizationError_thenReturnNull() {
		// given
		ProviderOfferedConsents consent = new ProviderOfferedConsents();
		given(revokeTokenService.revokeTokenBySessionSecret("sessionSecret")).willReturn(null);

		// when
		String result = testService.onOAuthAuthorizationError("sessionSecret");

		// then
		assertThat(result).isNull();
	}

	@Test
	public void givenToken_whenOnOAuthAuthorizationError_thenRevokeTokenAndReturnRedirectUrl() {
		// given
		Token token = new Token("sessionSecret", "tppAppName", "authTypeCode", "http://redirect.to");
		given(revokeTokenService.revokeTokenBySessionSecret("sessionSecret")).willReturn(token);

		// when
		String result = testService.onOAuthAuthorizationError("sessionSecret");

		// then
		assertThat(result).isEqualTo("http://redirect.to");
	}
}
