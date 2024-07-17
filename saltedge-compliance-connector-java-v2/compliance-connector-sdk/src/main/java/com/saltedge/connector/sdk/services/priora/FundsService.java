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
package com.saltedge.connector.sdk.services.priora;

import com.saltedge.connector.sdk.api.models.Account;
import com.saltedge.connector.sdk.api.models.AccountBalance;
import com.saltedge.connector.sdk.api.models.ExchangeRate;
import com.saltedge.connector.sdk.api.models.err.BadRequest;
import com.saltedge.connector.sdk.api.models.err.HttpErrorParams;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.api.models.requests.FundsConfirmationRequest;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import com.saltedge.connector.sdk.tools.TypeTools;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Service designated for fund confirmation functionality.
 */
@Service
public class FundsService extends BaseService {
  private static final Logger log = LoggerFactory.getLogger(FundsService.class);

  public boolean confirmFundsAvailability(@NotNull PiisToken token, @NotNull FundsConfirmationRequest request) {
    if (!request.account.equals(token.account)) throw new BadRequest.InvalidAttributeValue("FundsConfirmationRequest.account");
    try {
      Account account = providerService.getAccountsOfUser(token.userId).stream()
          .filter(model -> model.containsAccountIdentifier(request.getAccountIdentifier()))
          .findFirst().orElse(null);
      if (account == null) throw new NotFound.AccountNotFound();

      List<ExchangeRate> rates = providerService.getExchangeRates();
      ExchangeRate requestCurrency = findExchangeRateByCode(rates, request.instructedAmount.currency);
      if (requestCurrency == null) throw new BadRequest.InvalidAttributeValue("instructedAmount.currency");

      Float requestAmount = TypeTools.safeParseFloat(request.instructedAmount.amount, null);
      if (requestAmount == null) throw new BadRequest.InvalidAttributeValue("instructedAmount.amount");

      AccountBalance balance = account.getBalance("openingAvailable");
      if (balance == null) balance = account.getBalance("openingBooked");
      if (balance == null) throw new NotFound.BalanceNotFound();

      float balanceAmount = TypeTools.safeParseFloat(balance.amount, 0f);
      float accountExchangeRate = findExchangeRateByCode(rates, account.getCurrencyCode()).exchangeRate;
      return Math.abs(balanceAmount) * accountExchangeRate >= Math.abs(requestAmount) * requestCurrency.exchangeRate;
    } catch (Exception e) {
      log.error("CheckFundsService.checkFunds:", e);
      if (e instanceof HttpErrorParams) throw e;
      return false;
    }
  }

  private ExchangeRate findExchangeRateByCode(List<ExchangeRate> rates, String code) {
    if (rates == null || !StringUtils.hasLength(code)) return null;
    return rates.stream()
        .filter(model -> code.equals(model.currencyCode))
        .findFirst()
        .orElse(null);
  }
}
