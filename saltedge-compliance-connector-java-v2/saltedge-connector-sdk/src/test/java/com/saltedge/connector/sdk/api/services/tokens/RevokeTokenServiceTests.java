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

import com.saltedge.connector.sdk.api.err.NotFound;
import com.saltedge.connector.sdk.api.services.BaseServicesTests;
import com.saltedge.connector.sdk.models.persistence.Token;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RevokeTokenServiceTests extends BaseServicesTests {
	@Autowired
	protected RevokeTokenService revokeTokenService;

	@Test
	public void givenToken_whenRevokeTokenAsync_thenSaveTokeWithStatusRevoked() {
		// given
		Token token = new Token();

		// when
		revokeTokenService.revokeTokenAsync(token);

		// then
		final ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);
		verify(tokensRepository).save(captor.capture());
		assertThat(captor.getValue().status).isEqualTo(Token.Status.REVOKED);
	}

	@Test
	public void givenToken_whenRevokeTokenBySessionSecret_thenSaveTokeWithStatusRevoked() {
		// given
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(new Token());

		// when
		revokeTokenService.revokeTokenBySessionSecret("sessionSecret");

		// then
		final ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);
		verify(tokensRepository).save(captor.capture());
		assertThat(captor.getValue().status).isEqualTo(Token.Status.REVOKED);
	}

	@Test(expected = NotFound.TokenNotFound.class)
	public void givenNoToken_whenRevokeTokenBySessionSecret_thenRiseTokenNotFoundException() {
		// given
		given(tokensRepository.findFirstBySessionSecret("sessionSecret")).willReturn(null);

		// when
		revokeTokenService.revokeTokenBySessionSecret("sessionSecret");
	}
}
