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

import com.saltedge.connector.sdk.provider.models.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for communication between Compliance Connector and Service Provider application.
 * Service Provider application should implement `@Service` which `implements ProviderApi`
 */
public interface ProviderApi {
    /**
     * Provides current currencies exchange rates
     *
     * @return list of ExchangeRate objects
     * @see ExchangeRate
     */
    List<ExchangeRate> getExchangeRates();

    /**
     * Provides Authorization Types registered in `Dashboard/Settings/Authorization types` for Customer.
     * (https://priora.saltedge.com/providers/settings#authorization_types)
     *
     * @return list of AuthorizationType objects
     * @see AuthorizationType
     */
    List<AuthorizationType> getAuthorizationTypes();

    /**
     * Provides one of Authorization Types registered in `Dashboard/Settings/Authorization types` for Customer.
     * (https://priora.saltedge.com/providers/settings#authorization_types)
     * Authorization Type is selected by type code (e.g. `oauth`)
     *
     * @param authTypeCode of Authorization Type (e.g. `oauth`)
     * @return AuthorizationType registered AuthorizationType
     * @see AuthorizationType
     */
    AuthorizationType getAuthorizationTypeByCode(String authTypeCode);

    /**
     * Provides url of provider's authorization page designated for oAuth authorization
     *
     * @return URL string of Authorization page
     */
    String getAuthorizationPageUrl();

    /**
     * Generates confirmation code for authorization interactive step
     * and send it to customer via designated communication channel (e.g. sms)
     *
     * @param userId User identifier on Provider side
     * @param authType registered AuthorizationType
     * @return confirmation code string
     */
    String createAndSendAuthorizationConfirmationCode(String userId, AuthorizationType authType);

    /**
     * Authenticates user by provided credentials.
     * Credentials values should be extracted from `credentials` map by schema from AuthorizationType by `authTypeCode`
     *
     * @param authTypeCode code of registered Authorization Type (e.g. `oauth`)
     * @param credentials map of required first step credentials.
     * @return User identifier on Provider side
     */
    String authorizeUser(String authTypeCode, Map<String, String> credentials);

    /**
     * Provides reconnect policy type for an user. Connector supports next reconnect policy types: GRANT, MFA, DENY.
     * If provided GRANT type then customer will be reconnected right on request.
     * If provided MFA type then customer will be redirected to confirmation step.
     * If provided DENY type then customer request will be revoked.
     *
     * @param userId User identifier on Provider side
     * @return ReconnectPolicyType
     * @see ReconnectPolicyType
     */
    ReconnectPolicyType getReconnectPolicyType(String userId);

    /**
     * Provides personal data of user.
     *
     * @param userId User identifier on Provider side
     * @return KycData
     * @see KycData
     */
    KycData getKyc(String userId);

    /**
     * Provides account information of user
     *
     * @param userId User identifier on Provider side
     * @return list of AccountData objects
     * @see AccountData
     */
    List<AccountData> getAccounts(String userId);

    /**
     * Provides transactions which belong to account of user
     *
     * @param userId User identifier on Provider side
     * @param accountId Account identifier on Provider side
     * @param fromDate Specifies the starting date, from which transactions should be fetched.
     *                 This value can be set to 90 days ago by default.
     * @param toDate Specifies the ending date, to which transactions should be fetched.
     *               This value will always be the today’s date.
     * @return list of TransactionData objects
     * @see TransactionData
     */
    List<TransactionData> getTransactions(String userId, Long accountId, Date fromDate, Date toDate);

    /**
     * Provides Payment Templates registered in `Dashboard/Payment Templates` for Customer.
     * (https://priora.saltedge.com/providers/templates)
     *
     * @param userId User identifier on Provider side
     * @return list of PaymentTemplate objects
     * @see PaymentTemplate
     */
    List<PaymentTemplate> getPaymentTemplates(String userId);

    /**
     * Provides one of Payment Templates registered in `Dashboard/Payment Templates` for Customer.
     * (https://priora.saltedge.com/providers/templates)
     * Payment Template is selected by template code (e.g. `swift`, `sepa`)
     *
     * @param userId User identifier on Provider side
     * @param templateCode code of registered Payment Template (e.g. `swift`)
     * @return PaymentTemplate
     * @see PaymentTemplate
     */
    PaymentTemplate getPaymentTemplateByCode(String userId, String templateCode);

    /**
     * Initiates a Payment for User
     *
     * @param userId User identifier on Provider side
     * @param paymentTemplate registered Payment Template
     * @param paymentAttributes all attributes(required and optional) that are needed for a successful payment initiation
     *                          according to specified payment template.
     * @param extra any additional information deemed relevant to a payment, also service information like `priora_payment_id`.
     *              Extra should be saved and returned unmodified at `getPaymentData` request
     * @return Payment identifier on Provider side
     * @see PaymentTemplate
     */
    String createPayment(
            String userId,
            PaymentTemplate paymentTemplate,
            Map<String, String> paymentAttributes,
            Map<String, String> extra
    );

    /**
     * Checks if Payment is confirmed/done or not.
     *
     * @param paymentId Payment identifier on Provider side
     * @return true if Payment is confirmed
     */
    boolean isPaymentConfirmed(String paymentId);

    /**
     * Provides url of provider's confirmation page designated for oAuth payment confirmation
     *
     * @param paymentId Payment identifier on Provider side
     * @return URL string of Payment confirmation page
     */
    String getPaymentConfirmationPageUrl(String paymentId);

    /**
     * Checks if Payment is saved in Provider's database
     *
     * @param paymentId Payment identifier on Provider side
     * @return true if Payment exist
     */
    boolean hasPayment(String paymentId);

    /**
     * Provides Payment data including fees, status and other.
     *
     * @param paymentId Payment identifier on Provider side
     * @return PaymentData
     * @see PaymentData
     */
    PaymentData getPaymentData(String paymentId);

    /**
     * Checks provided confirmation credentials and confirms payment if they are valid.
     *
     * @param paymentId Payment identifier on Provider side
     * @param credentials map for required MFA credentials
     * @return true if Payment was confirmed
     */
    boolean confirmPayment(String paymentId, Map<String, String> credentials);

    /**
     * Cancel the Payment that is in the process of creation, meaning it has not been confirmed yet.
     *
     * @param paymentId Payment identifier on Provider side
     */
    void cancelPayment(String paymentId);
}
