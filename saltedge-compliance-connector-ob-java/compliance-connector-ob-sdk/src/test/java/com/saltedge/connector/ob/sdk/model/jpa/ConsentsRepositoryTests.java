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
package com.saltedge.connector.ob.sdk.model.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ConsentsRepositoryTests {
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private ConsentsRepository consentsRepository;

	@Test
	public void givenValidCode_whenFindFirstByAuthCode_thenReturnConsent() {
		//given
		Consent consent = new Consent("tppAppName", "consentId", "AwaitingAuthorization");
		consent.authCode = "authCode";
		entityManager.persist(consent);
		entityManager.flush();

		//when
		Consent found = consentsRepository.findFirstByAuthCode("authCode");

		//then
		assertThat(found.consentId).isEqualTo("consentId");
		assertThat(found.authCode).isEqualTo("authCode");
		assertThat(found.id).isGreaterThan(0L);
	}

	@Test
	public void givenInvalidCode_whenFindFirstByAuthCode_thenReturnNull() {
		//given
		Consent consent = new Consent("tppAppName", "consentId", "AwaitingAuthorization");
		consent.authCode = "authCode";
		entityManager.persist(consent);
		entityManager.flush();

		//when
		Consent found = consentsRepository.findFirstByAuthCode("invalidAuthCode");

		//then
		assertThat(found).isNull();
	}

	@Test
	public void givenValidCode_whenFindFirstByConsentId_thenReturnConsent() {
		//given
		Consent consent = new Consent("tppAppName", "consentId", "AwaitingAuthorization");
		entityManager.persist(consent);
		entityManager.flush();

		//when
		Consent found = consentsRepository.findFirstByConsentId("consentId");

		//then
		assertThat(found.consentId).isEqualTo("consentId");
		assertThat(found.id).isGreaterThan(0L);
	}

	@Test
	public void givenInvalidCode_whenFindFirstByConsentId_thenReturnNull() {
		//given
		Consent consent = new Consent("tppAppName", "consentId", "AwaitingAuthorization");
		entityManager.persist(consent);
		entityManager.flush();

		//when
		Consent found = consentsRepository.findFirstByConsentId("invalidConsentId");

		//then
		assertThat(found).isNull();
	}

	@Test
	public void givenValidCode_whenFindFirstByAccessToken_thenReturnConsent() {
		//given
		Consent consent = new Consent("tppAppName", "consentId", "AwaitingAuthorization");
		consent.accessToken = "accessToken";
		entityManager.persist(consent);
		entityManager.flush();

		//when
		Consent found = consentsRepository.findFirstByAccessToken("accessToken");

		//then
		assertThat(found.consentId).isEqualTo("consentId");
		assertThat(found.accessToken).isEqualTo("accessToken");
		assertThat(found.id).isGreaterThan(0L);
	}

	@Test
	public void givenInvalidCode_whenFindFirstByAccessToken_thenReturnNull() {
		//given
		Consent consent = new Consent("tppAppName", "consentId", "AwaitingAuthorization");
		consent.accessToken = "accessToken";
		entityManager.persist(consent);
		entityManager.flush();

		//when
		Consent found = consentsRepository.findFirstByAccessToken("invalidAccessToken");

		//then
		assertThat(found).isNull();
	}

	@Test
	public void givenValidCode_whenFindFirstByPaymentId_thenReturnConsent() {
		//given
		Consent consent = new Consent("tppAppName", "consentId", "AwaitingAuthorization");
		consent.paymentId = "paymentId";
		entityManager.persist(consent);
		entityManager.flush();

		//when
		Consent found = consentsRepository.findFirstByPaymentId("paymentId");

		//then
		assertThat(found.consentId).isEqualTo("consentId");
		assertThat(found.paymentId).isEqualTo("paymentId");
		assertThat(found.id).isGreaterThan(0L);
	}

	@Test
	public void givenInvalidCode_whenFindFirstByPaymentId_thenReturnNull() {
		//given
		Consent consent = new Consent("tppAppName", "consentId", "AwaitingAuthorization");
		consent.paymentId = "paymentId";
		entityManager.persist(consent);
		entityManager.flush();

		//when
		Consent found = consentsRepository.findFirstByPaymentId("invalidPaymentId");

		//then
		assertThat(found).isNull();
	}
}
