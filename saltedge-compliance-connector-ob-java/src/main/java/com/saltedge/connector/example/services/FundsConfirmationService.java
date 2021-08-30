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
package com.saltedge.connector.example.services;

import com.saltedge.connector.example.model.AccountEntity;
import com.saltedge.connector.example.model.repository.AccountsRepository;
import com.saltedge.connector.ob.sdk.api.models.errors.BadRequest;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccountIdentifier;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAmount;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObCurrencyExchange;
import com.saltedge.connector.ob.sdk.tools.TypeTools;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class FundsConfirmationService {
  private static final Logger log = LoggerFactory.getLogger(FundsConfirmationService.class);
  @Autowired
  private AccountsRepository accountsRepository;

  public boolean confirmFunds(@NotNull ObAccountIdentifier debtorAccount, @NotNull ObAmount amount) {
    try {
      List<ObCurrencyExchange> rates = getExchangeRates();
      ObCurrencyExchange requestCurrency = findExchangeRateByCode(rates, amount.currency);
      Float requestCurrencyExchangeRate = TypeTools.safeParseFloat(requestCurrency.exchangeRate, null);
      Float requestAmount = TypeTools.safeParseFloat(amount.amount, null);

      AccountEntity account = null;
      if (debtorAccount.isAccountNumber()) {
        account = accountsRepository.findFirstByAccountNumber(debtorAccount.identification);
      } if (debtorAccount.isIban()) {
        account = accountsRepository.findFirstByIban(debtorAccount.identification);
      }

      if (requestCurrencyExchangeRate == null || requestAmount == null) {
        throw new BadRequest.InvalidAttributeValue("FundsConfirmationRequest.currency_code");
      } else if (account == null) {
        throw new BadRequest.InvalidAttributeValue("FundsConfirmationRequest.account");
      } else {
        float availableAmount = TypeTools.safeParseFloat(account.availableAmount, 0f);
        String accountExchangeRateString = findExchangeRateByCode(rates, account.currencyCode).exchangeRate;
        float accountExchangeRate = TypeTools.safeParseFloat(accountExchangeRateString, 0f);
        return Math.abs(availableAmount) * accountExchangeRate >= Math.abs(requestAmount) * requestCurrencyExchangeRate;
      }
    } catch (Exception e) {
      log.error("FundsConfirmationService.confirmFunds:", e);
      return false;
    }
  }

  private List<ObCurrencyExchange> getExchangeRates() {
    ArrayList<ObCurrencyExchange> result = new ArrayList<>();
    result.add(new ObCurrencyExchange("EUR", "1.0"));
    result.add(new ObCurrencyExchange("USD", "0.90"));
    result.add(new ObCurrencyExchange("CAD", "0.65"));
    result.add(new ObCurrencyExchange("GBP", "1.502"));
    result.add(new ObCurrencyExchange("CHF", "0.95"));
    result.add(new ObCurrencyExchange("RUB", "0.0125"));
    result.add(new ObCurrencyExchange("CNY", "0.13"));
    result.add(new ObCurrencyExchange("JPY", "0.0085"));
    return result;
  }

  private ObCurrencyExchange findExchangeRateByCode(List<ObCurrencyExchange> rates, String code) {
    if (rates == null || !StringUtils.hasText(code)) return null;
    return rates.stream()
      .filter(model -> code.equals(model.sourceCurrency))
      .findFirst()
      .orElse(null);
  }

}
