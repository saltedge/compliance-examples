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
package com.saltedge.connector.sdk;

import com.saltedge.connector.sdk.provider.ProviderApi;
import com.saltedge.connector.sdk.provider.models.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TestProviderService implements ProviderApi {
    @Override
    public String getAuthorizationPageUrl() {
        return null;
    }

    @Override
    public List<AuthorizationType> getAuthorizationTypes() {
        return null;
    }

    @Override
    public AuthorizationType getAuthorizationTypeByCode(String code) {
        return null;
    }

    @Override
    public List<ExchangeRate> getExchangeRates() {
        return null;
    }

    @Override
    public ReconnectPolicyType getReconnectPolicyType(String userId) {
        return ReconnectPolicyType.GRANT;
    }

    @Override
    public String createAndSendAuthorizationConfirmationCode(@NotNull String userId, AuthorizationType authType) {
        return null;
    }

    @Override
    public String authorizeUser(String authTypeCode, Map<String, String> credentials) {
        return null;
    }

    @Override
    public KycData getKyc(@NotNull String userId) {
        return null;
    }

    @Override
    public List<AccountData> getAccounts(@NotNull String userId) {
        return null;
    }

    @Override
    public List<TransactionData> getTransactions(@NotNull String userId, Long accountId, Date fromDate, Date toDate) {
        return null;
    }

    @Override
    public List<PaymentTemplate> getPaymentTemplates(@NotNull String userId) {
        return null;
    }

    @Override
    public PaymentTemplate getPaymentTemplateByCode(String userId, String templateCode) {
        return null;
    }

    @Override
    public String createPayment(@NotNull String userId, PaymentTemplate paymentTemplate, Map<String, String> paymentAttributes, Map<String, String> extra) {
        return null;
    }

    @Override
    public boolean isPaymentConfirmed(@NotNull String paymentId) {
        return false;
    }

    @Override
    public String getPaymentConfirmationPageUrl(String paymentId) {
        return null;
    }

    @Override
    public boolean hasPayment(String paymentId) {
        return false;
    }

    @Override
    public PaymentData getPaymentData(String paymentId) {
        return null;
    }

    @Override
    public boolean confirmPayment(@NotNull String paymentId, Map<String, String> credentials) {
        return false;
    }

    @Override
    public void cancelPayment(@NotNull String paymentId) {
    }
}
