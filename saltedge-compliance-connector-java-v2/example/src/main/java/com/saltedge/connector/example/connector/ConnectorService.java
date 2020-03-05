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

import com.saltedge.connector.example.connector.collector.AccountsCollector;
import com.saltedge.connector.example.connector.config.AuthorizationTypes;
import com.saltedge.connector.example.controllers.UserAuthorizeController;
import com.saltedge.connector.example.model.AccountEntity;
import com.saltedge.connector.example.model.CardAccountEntity;
import com.saltedge.connector.example.model.CardTransactionEntity;
import com.saltedge.connector.example.model.UserEntity;
import com.saltedge.connector.example.model.repository.AccountsRepository;
import com.saltedge.connector.example.model.repository.CardAccountsRepository;
import com.saltedge.connector.example.model.repository.UsersRepository;
import com.saltedge.connector.sdk.api.err.NotFound;
import com.saltedge.connector.sdk.provider.ProviderApi;
import com.saltedge.connector.sdk.provider.models.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.function.Supplier;

@Service
@Validated
public class ConnectorService implements ProviderApi {
    private static Logger log = LoggerFactory.getLogger(ConnectorService.class);
    @Autowired
    Environment env;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    AccountsRepository accountsRepository;
    @Autowired
    CardAccountsRepository cardAccountsRepository;

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
    public List<Account> getAccountsOfUser(@NotEmpty String userId) {
        UserEntity user = findAndValidateUser(userId);
        return AccountsCollector.collectAccounts(accountsRepository, user);
    }

    @Override
    public List<Transaction> getTransactionsOfAccount(
            @NotEmpty String userId,
            @NotEmpty String accountId,
            Date fromDate,
            Date toDate
    ) {
        UserEntity user = findAndValidateUser(userId);
        AccountEntity account = accountsRepository.findFirstByIdAndUserId(Long.valueOf(accountId), user.id)
                .orElseThrow((Supplier<RuntimeException>) NotFound.AccountNotFound::new);
        return ConnectorTypeConverters.convertTransactionsToTransactionsData(account.transactions);
    }

    @Override
    public List<CardAccount> getCardAccountsOfUser(@NotEmpty String userId) {
        UserEntity user = findAndValidateUser(userId);
        return AccountsCollector.collectCardAccounts(cardAccountsRepository, user);
    }

    @Override
    public List<CardTransaction> getTransactionsOfCardAccount(
            @NotEmpty String userId,
            @NotEmpty String accountId,
            Date fromDate,
            Date toDate
    ) {
        UserEntity user = findAndValidateUser(userId);
        CardAccountEntity account = cardAccountsRepository.findFirstByIdAndUserId(Long.valueOf(accountId), user.id)
                .orElseThrow((Supplier<RuntimeException>) NotFound.AccountNotFound::new);
        return ConnectorTypeConverters.convertCardTransactionsToTransactionsData(new LinkedList<>(account.cardTransactions));
    }

    private UserEntity findAndValidateUser(@NotNull String userId) {
        return usersRepository.findById(Long.valueOf(userId))
                .orElseThrow((Supplier<RuntimeException>) NotFound.UserNotFound::new);
    }
}
