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
package com.saltedge.connector.example.compliance_connector;

import com.saltedge.connector.example.model.AccountEntity;
import com.saltedge.connector.example.model.TransactionEntity;
import com.saltedge.connector.example.model.UserEntity;
import com.saltedge.connector.example.model.repository.AccountsRepository;
import com.saltedge.connector.example.model.repository.TransactionsRepository;
import com.saltedge.connector.example.model.repository.UsersRepository;
import com.saltedge.connector.example.services.FundsConfirmationService;
import com.saltedge.connector.example.services.PaymentsService;
import com.saltedge.connector.ob.sdk.api.models.errors.NotFound;
import com.saltedge.connector.ob.sdk.provider.ProviderServiceAbs;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccount;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccountIdentifier;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAmount;
import com.saltedge.connector.ob.sdk.provider.dto.account.TransactionsPage;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObPaymentInitiationData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Example (Proof of concept) of Provider Service,
 * designated for communication between Compliance Connector SDK and Provider/ASPSP application.
 *
 * @see ProviderServiceAbs
 */
@Service
@Validated
public class ComplianceConnectorService implements ProviderServiceAbs {
  private static final Logger log = LoggerFactory.getLogger(ComplianceConnectorService.class);
  public static int PAGE_SIZE = 90;
  @Autowired
  private Environment env;
  @Autowired
  private UsersRepository usersRepository;
  @Autowired
  private AccountsRepository accountsRepository;
  @Autowired
  private TransactionsRepository transactionsRepository;
  @Autowired
  private PaymentsService paymentsService;
  @Autowired
  private FundsConfirmationService fundsService;

  @Override
  public List<ObAccount> getAccountsOfUser(@NotEmpty String userId) {
    log.info("ComplianceConnectorService.getAccountsOfUser for user:" + userId);
    UserEntity user = findAndValidateUser(userId);
    try {
      List<AccountEntity> accounts = accountsRepository.findByUserId(user.id);
      return ConnectorDataConverters.convertAccountsToAccountData(accounts, user);
    } catch (Exception e) {
      log.error("getAccountsOfUser", e);
      return Collections.emptyList();
    }
  }

  @Override
  public TransactionsPage getTransactionsOfAccount(
    @NotEmpty String userId,
    @NotEmpty String accountId,
    @NotNull LocalDate fromDate,
    @NotNull LocalDate toDate,
    String fromId
  ) {
    log.info("ComplianceConnectorService.getTransactionsOfAccount");
    UserEntity user = findAndValidateUser(userId);
    AccountEntity account = accountsRepository.findFirstByIdAndUserId(Long.parseLong(accountId), user.id)
      .orElseThrow((Supplier<RuntimeException>) NotFound.AccountNotFound::new);

    int fromIdValue = (StringUtils.hasText(fromId)) ? Integer.parseInt(fromId) : 0;
    Page<TransactionEntity> pagedResult = transactionsRepository.findByAccountIdAndMadeOnBetween(
      account.id,
      fromDate, toDate,
      PageRequest.of(fromIdValue, PAGE_SIZE)
    );

    if (pagedResult.hasContent()) {
      return new TransactionsPage(
        ConnectorDataConverters.convertTransactionsToTransactionsData(pagedResult.getContent()),
        (pagedResult.hasNext()) ? String.valueOf(fromIdValue + 1) : null
      );
    } else {
      return new TransactionsPage(new ArrayList<>(), null);
    }
  }

  @Override
  public boolean confirmFunds(String userId, @NotNull ObAccountIdentifier debtorAccount, @NotNull ObAmount amount) {
    try {
      UserEntity user = findAndValidateUser(userId);
      return fundsService.confirmFunds(debtorAccount, amount);
    } catch (Exception e) {
      log.error("CheckFundsService.checkFunds:", e);
      return false;
    }
  }

  @Override
  public String initiatePayment(String userId, @NotNull ObPaymentInitiationData params) {
    UserEntity user = findAndValidateUser(userId);

    Long paymentId = paymentsService.initiatePayment(params);
    paymentsService.processPayment(paymentId, params);//Async
    return String.valueOf(paymentId);
  }

  private UserEntity findAndValidateUser(@NotNull String userId) {
    return usersRepository.findById(Long.valueOf(userId))
      .orElseThrow((Supplier<RuntimeException>) NotFound.UserNotFound::new);
  }
}
