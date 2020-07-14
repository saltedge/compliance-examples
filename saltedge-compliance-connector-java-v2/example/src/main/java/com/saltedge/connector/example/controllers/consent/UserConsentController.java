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

import com.saltedge.connector.example.controllers.UserBaseController;
import com.saltedge.connector.example.model.AccountEntity;
import com.saltedge.connector.example.model.PaymentEntity;
import com.saltedge.connector.example.model.TransactionEntity;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.Account;
import com.saltedge.connector.sdk.api.models.CardAccount;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class UserConsentController extends UserBaseController {
    private static Logger log = LoggerFactory.getLogger(UserConsentController.class);
    public final static String ACCOUNTS_BASE_PATH = "/oauth/consent/accounts";
    public final static String PAYMENTS_BASE_PATH = "/oauth/consent/payments";

    @GetMapping(ACCOUNTS_BASE_PATH)
    public ModelAndView showAccountsConsentPage(
            @RequestParam(value = SDKConstants.KEY_SESSION_SECRET) String sessionSecret,
            @RequestParam(name = SDKConstants.KEY_USER_ID) String userId
    ) {
        List<Account> accounts = providerService.getAccountsOfUser(userId);
        List<CardAccount> cardAccounts = providerService.getCardAccountsOfUser(userId);
        ModelAndView result = new ModelAndView("user_accounts_consent");
        result.addObject(SDKConstants.KEY_SESSION_SECRET, sessionSecret);
        result.addObject(SDKConstants.KEY_USER_ID, userId);
        result.addObject("accounts", accounts);
        result.addObject("card_accounts", cardAccounts);
        return result;
    }

    @PostMapping(ACCOUNTS_BASE_PATH)
    public ModelAndView onAccountsConsentSubmit(
            @RequestParam(value = SDKConstants.KEY_SESSION_SECRET) String sessionSecret,
            @RequestParam(name = SDKConstants.KEY_USER_ID) String userId,
            @RequestParam(name = "balances", required = false) List<String> balancesIds,
            @RequestParam(name = "transactions", required = false) List<String> transactionIds,
            @RequestParam(name = "card_balances", required = false) List<String> cardBalanceIds,
            @RequestParam(name = "card_transactions", required = false) List<String> cardTransactionIds
    ) {
        // process accounts
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

        return onAccountInformationAuthorizationSuccess(
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

    @GetMapping(PAYMENTS_BASE_PATH)
    public ModelAndView showPaymentsConsentPage(
            @RequestParam(value = SDKConstants.KEY_PAYMENT_ID) Long paymentId,
            @RequestParam(name = SDKConstants.KEY_USER_ID) Long userId
    ) {
        ModelAndView result = new ModelAndView("user_payments_consent");
        PaymentEntity payment = paymentsRepository.findById(paymentId).orElse(null);
        if (payment != null) {
            result.addObject("account_from", payment.fromIban);
            result.addObject("account_to", payment.toIban);
            result.addObject("amount", payment.amount + " " + payment.currency);
            result.addObject("description", payment.description);
        }
        result.addObject(SDKConstants.KEY_PAYMENT_ID, paymentId);
        result.addObject(SDKConstants.KEY_USER_ID, userId);
        return result;
    }

    @PostMapping(PAYMENTS_BASE_PATH)
    public ModelAndView onPaymentConfirmationSubmit(
            @RequestParam(value = SDKConstants.KEY_PAYMENT_ID) Long paymentId,
            @RequestParam(name = SDKConstants.KEY_USER_ID) Long userId,
            @RequestParam(name = "confirm", required = false) String confirmAction,
            @RequestParam(name = "deny", required = false) String denyAction
    ) {
        PaymentEntity payment = paymentsRepository.findById(paymentId).orElse(null);
        String returnToUrl;
        if (!StringUtils.isEmpty(confirmAction) && payment != null) {
            processAndClosePayment(payment, userId);
            returnToUrl = connectorCallbackService.onPaymentInitiationAuthorizationSuccess(
                    paymentId.toString(),
                    userId.toString(),
                    payment.extra
            );
        } else {
            Map<String, String> extra = new HashMap<>();
            if (payment != null) {
                extra = payment.extra;
                payment.status = PaymentEntity.Status.FAILED;
                paymentsRepository.save(payment);
            }
            returnToUrl = connectorCallbackService.onPaymentInitiationAuthorizationFail(paymentId.toString(), extra);
        }
        if (returnToUrl == null) {
            return new ModelAndView("redirect:/oauth/authorize/payments?payment_id=" + paymentId);
        } else {
            return new ModelAndView("redirect:" + returnToUrl);
        }
    }

    private void processAndClosePayment(PaymentEntity payment, Long userId) {
        payment.status = PaymentEntity.Status.CLOSED;
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
                new HashMap<>(),
                account
        ));
    }
}
