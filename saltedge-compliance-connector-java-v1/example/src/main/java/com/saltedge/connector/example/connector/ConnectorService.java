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
package com.saltedge.connector.example.connector;

import com.saltedge.connector.example.connector.config.AuthorizationTypes;
import com.saltedge.connector.example.connector.config.PaymentTemplates;
import com.saltedge.connector.example.controllers.ConfirmCodeController;
import com.saltedge.connector.example.controllers.UserAuthorizeController;
import com.saltedge.connector.example.model.*;
import com.saltedge.connector.sdk.api.err.BadRequest;
import com.saltedge.connector.sdk.api.err.NotFound;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.provider.ProviderApi;
import com.saltedge.connector.sdk.provider.models.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class ConnectorService implements ProviderApi {
    private static Logger log = LoggerFactory.getLogger(ConnectorService.class);
    @Autowired
    Environment env;
    @Autowired
    CurrenciesRepository currenciesRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    AccountsRepository accountsRepository;
    @Autowired
    TransactionsRepository transactionsRepository;
    @Autowired
    PaymentsRepository paymentsRepository;

    @Override
    public String getAuthorizationPageUrl() {
        try {
            String urlString = env.getProperty("app.url");
            if (urlString == null) return null;
            return UriComponentsBuilder.fromUriString(urlString).path(UserAuthorizeController.BASE_PATH).build().toUriString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<AuthorizationType> getAuthorizationTypes() {
        ArrayList<AuthorizationType> result = new ArrayList<>();
        result.add(AuthorizationTypes.LOGIN_PASSWORD_AUTH_TYPE);
        result.add(AuthorizationTypes.LOGIN_PASSWORD_SMS_AUTH_TYPE);
        result.add(AuthorizationTypes.OAUTH_AUTH_TYPE);
        return result;
    }

    @Override
    public AuthorizationType getAuthorizationTypeByCode(String code) {
        if (code == null) return null;
        return getAuthorizationTypes().stream().filter(type -> code.equals(type.code)).findFirst().orElse(null);
    }

    @Override
    public List<ExchangeRate> getExchangeRates() {
        ArrayList<ExchangeRate> result = new ArrayList<>();
        currenciesRepository.findAll().forEach(currency -> result.add(new ExchangeRate(currency.code, currency.rate)));
        return result;
    }

    @Override
    public ReconnectPolicyType getReconnectPolicyType(String userId) {
        return ReconnectPolicyType.GRANT;
    }

    @Override
    public String createAndSendAuthorizationConfirmationCode(@NotNull String userId, AuthorizationType authType) {
        findAndValidateUser(userId);
        if (authType.isInteractive()) {
            return "123456";//use random generator and send code via appropriate channel (e.g. SMS)
        } else {
            return null;
        }
    }

    @Override
    public String authorizeUser(String authTypeCode, Map<String, String> credentials) {
        AuthorizationType authType = getAuthorizationTypeByCode(authTypeCode);
        if (authType == null || StringUtils.isEmpty(credentials)) {
            return null;
        }
        String username = credentials.get("login");
        String password = credentials.get("password");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return null;
        }

        return usersRepository.findFirstByUsernameAndPassword(username, password).map(user -> user.id.toString()).orElse(null);
    }

    @Override
    public KycData getKyc(@NotNull String userId) {
        return ConnectorTypeConverters.convertUserToKycData(findAndValidateUser(userId));
    }

    @Override
    public List<AccountData> getAccounts(@NotNull String userId) {
        findAndValidateUser(userId);
        List<Account> accounts = accountsRepository.findByUserId(Long.valueOf(userId));
        return ConnectorTypeConverters.convertAccountsToAccountData(accounts);
    }

    @Override
    public List<TransactionData> getTransactions(@NotNull String userId, Long accountId, Date fromDate, Date toDate) {
        findAndValidateUser(userId);
        Account account = accountsRepository.findById(accountId).orElseThrow((Supplier<RuntimeException>) NotFound.AccountNotFound::new);
        if (!account.user.id.toString().equals(userId)) throw new NotFound.AccountNotFound();
        return ConnectorTypeConverters.convertTransactionsToTransactionsData(account.transactions);
    }

    @Override
    public List<PaymentTemplate> getPaymentTemplates(@NotNull String userId) {
        findAndValidateUser(userId);
        ArrayList<PaymentTemplate> result = new ArrayList<>();
        result.add(PaymentTemplates.INTERNAL_TRANSFER);
        result.add(PaymentTemplates.SWIFT);
        result.add(PaymentTemplates.SEPA);
        return result;
    }

    @Override
    public PaymentTemplate getPaymentTemplateByCode(String userId, String templateCode) {
        if (userId == null || StringUtils.isEmpty(templateCode)) return null;
        return getPaymentTemplates(userId).stream().filter(model -> templateCode.equals(model.code)).findFirst().orElse(null);
    }

    @Override
    public String createPayment(
            @NotNull String userId,
            PaymentTemplate paymentTemplate,
            Map<String, String> paymentAttributes,
            Map<String, String> extra
    ) {
        User user = findAndValidateUser(userId);
        Double amount = ConnectorServiceTools.extractAmountFromPaymentAttributes(paymentAttributes);
        if (amount == null) throw new BadRequest.InvalidAttributeValue("amount");
        List<Fee> fees = ConnectorServiceTools.getFees(paymentTemplate);
        Account debtorAccount = ConnectorServiceTools.findDebtorAccount(accountsRepository, paymentTemplate, paymentAttributes);
        if (debtorAccount == null) throw new BadRequest.InvalidAttributeValue("debtor account");
        Payment payment = paymentsRepository.save(
                new Payment(
                        paymentTemplate.code,
                        ConnectorServiceTools.extractDescriptionFromPaymentAttributes(paymentAttributes),
                        Payment.Status.PENDING,
                        amount,
                        fees,
                        ConnectorServiceTools.getTotalAmount(amount, fees),
                        debtorAccount.id,
                        paymentAttributes,
                        extra,
                        user
                )
        );
        if (ConnectorServiceTools.isExemptPayment(payment)) {
            confirmPayment(payment, "");
        } else if (paymentTemplate.isInteractive()) {
            payment.confirmationCode = createAndSendPaymentConfirmationCode(user);
            paymentsRepository.save(payment);
        }
        return payment.id.toString();
    }

    @Override
    public boolean isPaymentConfirmed(@NotNull String paymentId) {
        return findPayment(paymentId).isConfirmed();
    }

    @Override
    public String getPaymentConfirmationPageUrl(@NotNull String paymentId) {
        try {
            String urlString = env.getProperty("app.url");
            if (urlString == null) return null;
            return UriComponentsBuilder.fromUriString(urlString)
                    .path(ConfirmCodeController.BASE_PATH)
                    .queryParam(Constants.KEY_PAYMENT_ID, paymentId).build().toUriString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean hasPayment(String paymentId) {
        return findPayment(paymentId) != null;
    }

    @Override
    public PaymentData getPaymentData(String paymentId) {
        Payment payment = findPayment(paymentId);
        return ConnectorTypeConverters.convertPaymentToPaymentData(payment);
    }

    @Override
    public boolean confirmPayment(@NotNull String paymentId, Map<String, String> credentials) {
        Payment payment = findPayment(paymentId);
        PaymentTemplate template = getPaymentTemplateByCode(payment.user.id.toString(), payment.paymentTemplateCode);
        if (template == null) throw new NotFound.PaymentNotFound();

        String confirmationCode = (template.isInteractive()) ? credentials.get(template.interactiveField.code) : "";
        return confirmPayment(payment, confirmationCode);
    }

    @Override
    public void cancelPayment(@NotNull String paymentId) {
        Payment payment = findPayment(paymentId);
        payment.status = Payment.Status.CLOSED;
        paymentsRepository.save(payment);
    }

    private String createAndSendPaymentConfirmationCode(@NotNull User user) {
        return "123456";//use random generator and send code via appropriate channel (e.g. SMS)
    }

    public boolean confirmPayment(@NotNull Payment payment, @NotNull String confirmationCode) {
        boolean result = StringUtils.isEmpty(payment.confirmationCode) || payment.confirmationCode.equals(confirmationCode);
        payment.status = result ? Payment.Status.CONFIRMED : Payment.Status.FAILED;
        paymentsRepository.save(payment);
        if (result) ConnectorServiceTools.createTransaction(accountsRepository, transactionsRepository, payment);
        return result;
    }

    private User findAndValidateUser(@NotNull String userId) {
        return usersRepository.findById(Long.valueOf(userId))
                .orElseThrow((Supplier<RuntimeException>) NotFound.UserNotFound::new);
    }

    private Payment findPayment(@NotNull String paymentId) {
        return paymentsRepository.findById(Long.valueOf(paymentId))
                .orElseThrow((Supplier<RuntimeException>) NotFound.PaymentNotFound::new);
    }
}
