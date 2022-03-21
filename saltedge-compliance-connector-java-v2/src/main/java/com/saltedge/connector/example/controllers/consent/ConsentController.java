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
package com.saltedge.connector.example.controllers.consent;

import com.saltedge.connector.example.model.AccountEntity;
import com.saltedge.connector.example.model.PaymentEntity;
import com.saltedge.connector.example.model.PaymentStatus;
import com.saltedge.connector.example.model.TransactionEntity;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.Account;
import com.saltedge.connector.sdk.api.models.CardAccount;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.models.ParticipantAccount;
import com.saltedge.connector.sdk.tools.KeyTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class ConsentController extends ConsentBaseController {
  private static final Logger log = LoggerFactory.getLogger(ConsentController.class);
  public final static String BASE_PATH = "/consent/authorize";
  public final static String ACCOUNTS_BASE_PATH = BASE_PATH + "/accounts";
  public final static String PAYMENTS_BASE_PATH = BASE_PATH + "/payments";
  public final static String FUNDS_BASE_PATH = BASE_PATH + "/funds";

  @GetMapping(ACCOUNTS_BASE_PATH)
  public ModelAndView showAccountsConsentPage(
      @RequestParam(name = SDKConstants.KEY_USER_ID) String userId,
      @RequestParam(value = SDKConstants.KEY_SESSION_SECRET) String sessionSecret
  ) {
    ModelAndView result = new ModelAndView("user_consent_accounts");
    result.addObject(SDKConstants.KEY_USER_ID, userId);
    result.addObject(SDKConstants.KEY_SESSION_SECRET, sessionSecret);

    List<Account> accounts = providerService.getAccountsOfUser(userId);
    List<CardAccount> cardAccounts = providerService.getCardAccountsOfUser(userId);
    result.addObject("accounts", accounts);
    result.addObject("card_accounts", cardAccounts);

    return result;
  }

  @PostMapping(ACCOUNTS_BASE_PATH)
  public ModelAndView onAccountsConsentSubmit(
      @RequestParam(name = "submit") boolean confirmed,
      @RequestParam(name = SDKConstants.KEY_SESSION_SECRET) String sessionSecret,
      @RequestParam(name = SDKConstants.KEY_USER_ID) String userId,
      @RequestParam(name = "balances", required = false) List<String> balancesIds,
      @RequestParam(name = "transactions", required = false) List<String> transactionIds,
      @RequestParam(name = "card_balances", required = false) List<String> cardBalanceIds,
      @RequestParam(name = "card_transactions", required = false) List<String> cardTransactionIds
  ) {
    if (confirmed) return confirmAccountsConsent(sessionSecret, userId, balancesIds, transactionIds, cardBalanceIds, cardTransactionIds);
    else return onAisDenied(sessionSecret);
  }

  @GetMapping(PAYMENTS_BASE_PATH)
  public ModelAndView showPaymentConsentPage(
      @RequestParam(name = SDKConstants.KEY_USER_ID) String userId,
      @RequestParam(value = SDKConstants.KEY_PAYMENT_ID, required = false) Long paymentId
  ) {
    ModelAndView result = new ModelAndView("user_consent_payments");
    result.addObject(SDKConstants.KEY_USER_ID, userId);
    result.addObject(SDKConstants.KEY_PAYMENT_ID, paymentId);

    PaymentEntity payment = paymentsRepository.findById(paymentId).orElse(null);
    if (payment != null) {
      if (payment.isFps()) {
        result.addObject("account_from", payment.fromBban + " / " + payment.fromSortCode);
        result.addObject("account_to", payment.toBban + " / " + payment.toSortCode);
      } else {
        result.addObject("account_from", payment.fromIban);
        result.addObject("account_to", payment.toIban);
      }
      result.addObject("amount", payment.amount + " " + payment.currency);
      result.addObject("description", payment.description);
    }

    return result;
  }

  @PostMapping(PAYMENTS_BASE_PATH)
  public ModelAndView onPaymentConsentSubmit(
      @RequestParam(name = "submit") boolean confirmed,
      @RequestParam(name = SDKConstants.KEY_PAYMENT_ID) Long paymentId,
      @RequestParam(name = SDKConstants.KEY_USER_ID) Long userId
  ) {
    if (confirmed) return confirmPayment(paymentId, userId);
    else return onPisDenied(paymentId);
  }

  @GetMapping(FUNDS_BASE_PATH)
  public ModelAndView showFundsConsentPage(
      @RequestParam(name = SDKConstants.KEY_USER_ID) String userId,
      @RequestParam(value = SDKConstants.KEY_SESSION_SECRET) String sessionSecret
  ) {
    ModelAndView result = new ModelAndView("user_consent_funds");
    result.addObject(SDKConstants.KEY_USER_ID, userId);
    result.addObject(SDKConstants.KEY_SESSION_SECRET, sessionSecret);

    ParticipantAccount account = connectorCallbackService.getFundsConfirmationConsentData(sessionSecret);
    result.addObject("account", account);

    return result;
  }

  @PostMapping(FUNDS_BASE_PATH)
  public ModelAndView onFundsConsentSubmit(
      @RequestParam(name = "submit") boolean confirmed,
      @RequestParam(name = SDKConstants.KEY_SESSION_SECRET) String sessionSecret,
      @RequestParam(name = SDKConstants.KEY_USER_ID) Long userId
  ) {
    if (confirmed) return confirmFunds(sessionSecret, userId);
    else return onPiisDenied(sessionSecret);
  }

  private ModelAndView confirmAccountsConsent(String sessionSecret, String userId, List<String> balancesIds, List<String> transactionIds, List<String> cardBalanceIds, List<String> cardTransactionIds) {
    List<Account> accounts = providerService.getAccountsOfUser(userId);

    List<Account> consentForBalances;
    if (balancesIds == null) consentForBalances = new ArrayList<>();
    else consentForBalances = accounts.stream()
        .filter(item -> balancesIds.contains(item.getId())).collect(Collectors.toList());

    List<Account> consentForTransactions;
    if (transactionIds == null) consentForTransactions = new ArrayList<>();
    else consentForTransactions = accounts.stream()
        .filter(item -> transactionIds.contains(item.getId())).collect(Collectors.toList());

    // process card accounts
    List<CardAccount> cardAccounts = providerService.getCardAccountsOfUser(userId);

    List<CardAccount> consentForCardsBalances;
    if (cardBalanceIds == null) consentForCardsBalances = new ArrayList<>();
    else consentForCardsBalances = cardAccounts.stream()
        .filter(item -> cardBalanceIds.contains(item.getId())).collect(Collectors.toList());

    List<CardAccount> consentForCardsTransactions;
    if (cardTransactionIds == null) consentForCardsTransactions = new ArrayList<>();
    else consentForCardsTransactions = cardAccounts.stream()
        .filter(item -> cardTransactionIds.contains(item.getId())).collect(Collectors.toList());

    return onAisSuccess(
        sessionSecret,
        userId,
        ProviderConsents.buildProviderOfferedConsents(
            consentForBalances,
            consentForTransactions,
            consentForCardsBalances,
            consentForCardsTransactions
        )
    );
  }

  private ModelAndView confirmPayment(Long paymentId, Long userId) {
    PaymentEntity payment = paymentsRepository.findById(paymentId).orElse(null);
    String returnToUrl;
    if (payment != null) {
      AccountEntity account = accountsRepository.findById(payment.accountId).orElse(null);
      if (account != null) {
        try {
          double amount = Double.parseDouble(account.availableAmount);
          updatePaymentFundsInformation(payment, amount);
        } catch (NumberFormatException e) {
          log.error(e.getMessage(), e);
        }
      }
      processAndClosePayment(payment, userId);

      returnToUrl = connectorCallbackService.onPaymentInitiationAuthorizationSuccess(
          userId.toString(),
          payment.extra,
          payment.paymentProduct
      );
    } else {
      returnToUrl = null;
    }

    if (returnToUrl != null) return new ModelAndView("redirect:" + returnToUrl);
    return redirectToPaymentsAuth(paymentId, userId);
  }

  private void updatePaymentFundsInformation(PaymentEntity payment, Double amount) {
      boolean fundsAvailable = payment.amount < amount;
      connectorCallbackService.updatePaymentFundsInformation(fundsAvailable, payment.extra, "PDNG");
  }

  private void processAndClosePayment(PaymentEntity payment, Long userId) {
    payment.status = PaymentStatus.CLOSED;
    payment.user = usersRepository.findById(userId).orElse(null);
    paymentsRepository.save(payment);

    AccountEntity account = accountsRepository.findById(payment.accountId).orElse(null);

    double amount = -payment.amount;
    transactionsRepository.save(new TransactionEntity(
      String.format("%.2f", amount),
      payment.currency,
      "Payment " + amount + " " + payment.currency + "(Account:" + payment.accountId + ")",
      LocalDate.now(),
      "booked",
      new ArrayList<>(),
      "",
      account
    ));
  }

  private ModelAndView confirmFunds(String sessionSecret, Long userId) {
    String returnToUrl = connectorCallbackService.onFundsConfirmationConsentAuthorizationSuccess(
        sessionSecret,
        userId.toString(),
        KeyTools.generateToken(32)
    );
    if (returnToUrl == null) return redirectToFundsAuth(sessionSecret, userId.toString());
    else return new ModelAndView("redirect:" + returnToUrl);
  }
}
