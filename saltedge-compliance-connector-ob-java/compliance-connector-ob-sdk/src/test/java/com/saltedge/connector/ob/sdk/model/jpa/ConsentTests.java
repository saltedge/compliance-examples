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

import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccountIdentifier;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAmount;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObRiskData;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ConsentTests {
	@Test
	public void createAisConsentTest() {
		//when
		Consent result = Consent.createAisConsent("tppAppName", "1", "AwaitingAuthorisation", Collections.emptyList(), null, null, null);

		//then
		assertThat(result.isAisConsent()).isTrue();
		assertThat(result.isPisConsent()).isFalse();
		assertThat(result.isPiisConsent()).isFalse();
	}

	@Test
	public void createPisConsentTest() {
		//given
		ObPaymentInitiationData paymentData = new ObPaymentInitiationData();
		paymentData.instructionIdentification = "instructionIdentification";
		paymentData.endToEndIdentification = "endToEndIdentification";
		paymentData.instructedAmount = new ObAmount("1.0", "GBP");
		paymentData.creditorAccount = new ObAccountIdentifier("scheme", "identifier");

		ObRiskData risk = new ObRiskData();
		risk.paymentContextCode = "BillPayment";
		risk.merchantCategoryCode = "5541";

		//when
		Consent result = Consent.createPisConsent(
				"tppAppName",
				"1",
				"AwaitingAuthorisation",
				"domestic_payment",
				paymentData,
				risk
		);

		//then
		assertThat(result.isAisConsent()).isFalse();
		assertThat(result.isPisConsent()).isTrue();
		assertThat(result.isPiisConsent()).isFalse();
	}

	@Test
	public void createPiisConsentTest() {
		//given
		Instant expirationDateTime = Instant.now().plusSeconds(1);
		ObAccountIdentifier debtorAccount = new ObAccountIdentifier("scheme", "identifier");

		//when
		Consent result = Consent.createPiisConsent("tppAppName", "1", "AwaitingAuthorisation", expirationDateTime, debtorAccount);

		//then
		assertThat(result.isAisConsent()).isFalse();
		assertThat(result.isPisConsent()).isFalse();
		assertThat(result.isPiisConsent()).isTrue();
	}

	@Test
	public void isAwaitingAuthorisationTest() {
		Consent testConsent = new Consent();
		testConsent.status = "Pending";

		assertThat(testConsent.isAwaitingAuthorisation()).isFalse();

		testConsent.status = "AwaitingAuthorisation";

		assertThat(testConsent.isAwaitingAuthorisation()).isTrue();
	}

	@Test
	public void isAuthorisedTest() {
		Consent testConsent = new Consent();
		testConsent.status = "Pending";
		testConsent.userId = "userId";

		assertThat(testConsent.isAuthorised()).isFalse();

		testConsent.status = "Authorised";
		testConsent.userId = "";

		assertThat(testConsent.isAuthorised()).isFalse();

		testConsent.status = "Authorised";
		testConsent.userId = "userId";

		assertThat(testConsent.isAuthorised()).isTrue();

		testConsent.status = "approved";
		testConsent.userId = "userId";

		assertThat(testConsent.isAuthorised()).isTrue();
	}

	@Test
	public void isConsumedTest() {
		Consent testConsent = new Consent();
		testConsent.status = "Authorised";

		assertThat(testConsent.isConsumed()).isFalse();

		testConsent.status = "Consumed";

		assertThat(testConsent.isConsumed()).isTrue();
	}

	@Test
	public void isRejectedTest() {
		Consent testConsent = new Consent();
		testConsent.status = "Pending";

		assertThat(testConsent.isRejected()).isFalse();

		testConsent.status = "Rejected";

		assertThat(testConsent.isRejected()).isTrue();

		testConsent.status = "denied";

		assertThat(testConsent.isRejected()).isTrue();
	}

	@Test
	public void isRevokedTest() {
		Consent testConsent = new Consent();
		testConsent.status = "Authorised";

		assertThat(testConsent.isRevoked()).isFalse();

		testConsent.status = "Revoked";

		assertThat(testConsent.isRevoked()).isTrue();
	}

	@Test
	public void isAisConsentTest() {
		Consent testConsent = new Consent();
		testConsent.permissions = null;

		assertThat(testConsent.isAisConsent()).isFalse();

		testConsent.permissions = Lists.list("ReadAccountsDetail");

		assertThat(testConsent.isAisConsent()).isTrue();
	}

	@Test
	public void isPisConsentTest() {
		Consent testConsent = new Consent();
		testConsent.paymentInitiation = null;

		assertThat(testConsent.isPisConsent()).isFalse();

		ObPaymentInitiationData paymentData = new ObPaymentInitiationData();
		paymentData.instructionIdentification = "instructionIdentification";
		paymentData.endToEndIdentification = "endToEndIdentification";
		paymentData.instructedAmount = new ObAmount("1.0", "GBP");
		paymentData.creditorAccount = new ObAccountIdentifier("scheme", "identifier");
		testConsent.paymentInitiation = paymentData;

		assertThat(testConsent.isPisConsent()).isTrue();
	}

	@Test
	public void isPiisConsentTest() {
		Consent testConsent = new Consent();
		testConsent.debtorAccount = null;

		assertThat(testConsent.isPiisConsent()).isFalse();

		testConsent.debtorAccount = new ObAccountIdentifier("scheme", "identifier");

		assertThat(testConsent.isPiisConsent()).isTrue();
	}

	@Test
	public void aisPermissionsExpiredTest() {
		Consent testConsent = new Consent();
		testConsent.permissions = null;
		testConsent.permissionsExpiresAt = null;

		assertThat(testConsent.aisPermissionsExpired()).isFalse();

		testConsent.permissions = Lists.list("ReadAccountsDetail");
		testConsent.permissionsExpiresAt = null;

		assertThat(testConsent.aisPermissionsExpired()).isFalse();

		testConsent.permissions = null;
		testConsent.permissionsExpiresAt = Instant.now().minusSeconds(1);

		assertThat(testConsent.aisPermissionsExpired()).isFalse();

		testConsent.permissions = Lists.list("ReadAccountsDetail");
		testConsent.permissionsExpiresAt = Instant.now().plusSeconds(1);

		assertThat(testConsent.aisPermissionsExpired()).isFalse();

		testConsent.permissions = Lists.list("ReadAccountsDetail");
		testConsent.permissionsExpiresAt = Instant.now().minusSeconds(1);

		assertThat(testConsent.aisPermissionsExpired()).isTrue();
	}
}
