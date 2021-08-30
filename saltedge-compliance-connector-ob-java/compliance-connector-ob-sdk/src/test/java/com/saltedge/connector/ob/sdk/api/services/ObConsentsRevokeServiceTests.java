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

import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.model.jpa.ConsentsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class ObConsentsRevokeServiceTests {
	@MockBean
	protected ConsentsRepository mockConsentsRepository;
	@Autowired
	protected ObConsentsRevokeService testService;

	@Test
	public void givenValidConsent_whenRevokeConsent_thenSaveConsentWithStatusRevoked() {
		//given
		String consentId = "consentId";
		Consent consent = new Consent();
		consent.consentId = consentId;
		given(mockConsentsRepository.findFirstByConsentId(consentId)).willReturn(consent);

		//when
		testService.revokeConsent(consentId);

		//then
		final ArgumentCaptor<Consent> captor = ArgumentCaptor.forClass(Consent.class);
		Mockito.verify(mockConsentsRepository).save(captor.capture());
		assertThat(captor.getValue().status).isEqualTo("Revoked");
	}

	@Test
	public void givenInvalidConsent_whenRevokeTokenBySessionSecret_thenRiseTokenNotFoundException() {
		// given
		String consentId = "consentId";
		given(mockConsentsRepository.findFirstByConsentId(consentId)).willReturn(null);

		//when
		testService.revokeConsent(consentId);
		Mockito.verify(mockConsentsRepository).findFirstByConsentId(consentId);
		Mockito.verifyNoMoreInteractions(mockConsentsRepository);
	}
}
