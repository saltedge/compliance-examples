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

import com.saltedge.connector.ob.sdk.SDKConstants;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccountIdentifier;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Database entity for saving data about connection between Connector and Salt Edge Compliance Solution
 */
@Entity
public class Consent extends BaseJpaEntity implements Serializable {

    @Column(name = "tpp_name")
    @Size(min = 1, max = 4096)
    public String tppName;

    @Column(name = SDKConstants.KEY_CONSENT_ID, nullable = false)
    public String consentId;

    /**
     * Values: AwaitingAuthorisation, Authorised, Rejected, Revoked
     */
    @Column(name = SDKConstants.KEY_STATUS, nullable = false)
    public String status;

    @Column(name = SDKConstants.KEY_AUTHORIZATION_ID)
    public String authorizationId;

    @Column(name = SDKConstants.KEY_AUTH_CODE)
    public String authCode;

    @Column(name = SDKConstants.KEY_ACCESS_TOKEN)
    public String accessToken;

    @Column(name = SDKConstants.KEY_USER_ID)
    public String userId;

    //Identifiers with granted access for PIS
    @Column(name = "account_identifiers")
    @Convert(converter = StringListConverter.class)
    public List<String> accountIdentifiers;

    //AIS initial data
    @Column(name = "permissions", length = 4096)
    @Convert(converter = StringListConverter.class)
    public List<String> permissions;

    @Column(name = "permissions_expires_at")
    public Instant permissionsExpiresAt;

    @Column(name = "transaction_from_date_time")
    public Instant transactionFrom;

    @Column(name = "transaction_to_date_time")
    public Instant transactionTo;

    //PIS initial data
    @Column(name = "payment", length = 4096)
    @Convert(converter = PaymentInitiationDataConverter.class)
    public ObPaymentInitiationData payment;

    @Column(name = "compliance_payment_id")
    public String compliancePaymentId;

    @Column(name = "payment_id")
    public String paymentId;

    //PIIS initial data
    @Column(name = "debtor_account", length = 4096)
    @Convert(converter = AccountIdentifierConverter.class)
    public ObAccountIdentifier debtorAccount;

    public Consent() {
    }

    public Consent(String tppName, String consentId, String status) {
        this.tppName = tppName;
        this.consentId = consentId;
        this.status = status;
    }

    public static Consent createAisConsent(
      String tppName,
      String consentId,
      String status,
      List<String> permissions,
      Instant permissionsExpiresAt,
      Instant transactionFrom,
      Instant transactionTo
    ) {
        Consent result = new Consent(tppName, consentId, status);
        result.permissions = permissions;
        result.permissionsExpiresAt = permissionsExpiresAt;
        result.transactionFrom = transactionFrom;
        result.transactionTo = transactionTo;
        return result;
    }

    public static Consent createPisConsent(
      String tppName,
      String consentId,
      String status,
      ObPaymentInitiationData payment
    ) {
        Consent result = new Consent(tppName, consentId, status);
        result.payment = payment;
        return result;
    }

    public static Consent createPiisConsent(
      String tppName,
      String consentId,
      String status,
      Instant permissionsExpiresAt,
      ObAccountIdentifier debtorAccount
    ) {
        Consent result = new Consent(tppName, consentId, status);
        result.permissionsExpiresAt = permissionsExpiresAt;
        result.debtorAccount = debtorAccount;
        return result;
    }

    public boolean isAwaitingAuthorisation() {
        return "AwaitingAuthorisation".equalsIgnoreCase(status);
    }

    public boolean isAuthorised() {
        return ("Authorised".equalsIgnoreCase(status) || "approved".equalsIgnoreCase(status))
          && StringUtils.hasText(userId);
    }

    public boolean isConsumed() {
        return "Consumed".equalsIgnoreCase(status);
    }

    public boolean isRejected() {
        return "Rejected".equalsIgnoreCase(status) || "denied".equalsIgnoreCase(status);
    }

    public boolean isRevoked() {
        return "Revoked".equalsIgnoreCase(status);
    }

    public boolean isAisConsent() {
        return permissions != null;
    }

    public boolean isPisConsent() {
        return payment != null;
    }

    public boolean isPiisConsent() {
        return debtorAccount != null;
    }

    public boolean aisPermissionsExpired() {
        return isAisConsent() && permissionsExpiresAt != null && permissionsExpiresAt.isBefore(Instant.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consent consent = (Consent) o;
        return Objects.equals(tppName, consent.tppName) && Objects.equals(consentId, consent.consentId) && Objects.equals(status, consent.status) && Objects.equals(authorizationId, consent.authorizationId) && Objects.equals(authCode, consent.authCode) && Objects.equals(accessToken, consent.accessToken) && Objects.equals(userId, consent.userId) && Objects.equals(accountIdentifiers, consent.accountIdentifiers) && Objects.equals(permissions, consent.permissions) && Objects.equals(permissionsExpiresAt, consent.permissionsExpiresAt) && Objects.equals(transactionFrom, consent.transactionFrom) && Objects.equals(transactionTo, consent.transactionTo) && Objects.equals(payment, consent.payment) && Objects.equals(compliancePaymentId, consent.compliancePaymentId) && Objects.equals(paymentId, consent.paymentId) && Objects.equals(debtorAccount, consent.debtorAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tppName, consentId, status, authorizationId, authCode, accessToken, userId, accountIdentifiers, permissions, permissionsExpiresAt, transactionFrom, transactionTo, payment, compliancePaymentId, paymentId, debtorAccount);
    }
}
