/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2022 Salt Edge.
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

import com.saltedge.connector.sdk.api.services.BaseServicesTests;
import com.saltedge.connector.sdk.models.ConsentStatus;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import com.saltedge.connector.sdk.services.priora.RevokeTokenByPrioraService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RevokeTokenByProviderServiceTests extends BaseServicesTests {
	@Autowired
	protected RevokeTokenByPrioraService revokeTokenService;

	@Test
	public void givenAisToken_whenRevokeTokenAsync_thenSaveTokeWithStatusRevoked() {
		// given
		AisToken token = new AisToken();

		// when
		revokeTokenService.revokeTokenAsync(token);

		// then
		final ArgumentCaptor<AisToken> captor = ArgumentCaptor.forClass(AisToken.class);
		verify(aisTokensRepository).save(captor.capture());
		assertThat(captor.getValue().status).isEqualTo(ConsentStatus.REVOKED);
	}

	@Test
	public void givenPiisToken_whenRevokeTokenAsync_thenSaveTokeWithStatusRevoked() {
		// given
		PiisToken token = new PiisToken();

		// when
		revokeTokenService.revokeTokenAsync(token);

		// then
		final ArgumentCaptor<PiisToken> captor = ArgumentCaptor.forClass(PiisToken.class);
		verify(piisTokensRepository).save(captor.capture());
		assertThat(captor.getValue().status).isEqualTo(ConsentStatus.REVOKED);
	}
}
