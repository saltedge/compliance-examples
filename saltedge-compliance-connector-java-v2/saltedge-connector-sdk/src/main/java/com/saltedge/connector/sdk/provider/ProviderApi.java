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

import com.saltedge.connector.sdk.provider.models.AccountData;
import com.saltedge.connector.sdk.provider.models.AuthorizationType;
import com.saltedge.connector.sdk.provider.models.CurrencyExchange;
import com.saltedge.connector.sdk.provider.models.TransactionData;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for communication between Compliance Connector and Service Provider application.
 * Service Provider application should implement `@Service` which `implements ProviderApi`
 */
public interface ProviderApi {
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
     * Provides account information of user
     *
     * @param userId User identifier on Provider side
     * @return list of AccountData objects
     * @see AccountData
     */
    List<AccountData> getAccountsList(String userId);

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
    List<TransactionData> getTransactionsList(String userId, String accountId, Date fromDate, Date toDate);

}
