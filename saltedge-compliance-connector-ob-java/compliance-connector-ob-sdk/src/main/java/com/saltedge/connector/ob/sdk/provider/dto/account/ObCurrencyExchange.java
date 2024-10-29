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
package com.saltedge.connector.ob.sdk.provider.dto.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.SDKConstants;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;

/**
 * Info of exchange operation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class ObCurrencyExchange {

  /**
   * Currency from which an amount is to be converted in a currency conversion.
   */
  @NotEmpty
  @JsonProperty("source_currency")
  public String sourceCurrency;

  /**
   * Currency into which an amount is to be converted in a currency conversion.
   */
  @JsonProperty("target_currency")
  public String targetCurrency;

  /**
   * Currency in which the rate of exchange is expressed in a currency exchange.
   * In the example 1GBP = xxxCUR, the unit currency is GBP.
   */
  @JsonProperty("unit_currency")
  public String unitCurrency;

  /**
   * Factor used to convert an amount from one currency into another. This reflects the price at which one currency was bought with another currency.
   * Usage: ExchangeRate expresses the ratio between UnitCurrency and QuotedCurrency (ExchangeRate = UnitCurrency/QuotedCurrency).
   */
  @NotEmpty
  @JsonProperty("exchange_rate")
  public String exchangeRate;

  /**
   * Unique identification to unambiguously identify the foreign exchange contract.
   */
  @JsonProperty("contract_identification")
  public String contractIdentification;

  /**
   * Date and time at which an exchange rate is quoted.
   */
  @JsonProperty("quotation_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  public LocalDate quotationDate;

  /**
   * Amount of money to be moved between the debtor and creditor, before deduction of charges, expressed in the currency as ordered by the initiating party.
   */
  @JsonProperty(SDKConstants.KEY_INSTRUCTED_AMOUNT)
  public ObAmount instructedAmount;

  public ObCurrencyExchange(String sourceCurrency, String exchangeRate) {
    this.sourceCurrency = sourceCurrency;
    this.exchangeRate = exchangeRate;
  }

  public ObCurrencyExchange(String sourceCurrency, String targetCurrency, String unitCurrency, String exchangeRate, String contractIdentification, LocalDate quotationDate, ObAmount instructedAmount) {
    this.sourceCurrency = sourceCurrency;
    this.targetCurrency = targetCurrency;
    this.unitCurrency = unitCurrency;
    this.exchangeRate = exchangeRate;
    this.contractIdentification = contractIdentification;
    this.quotationDate = quotationDate;
    this.instructedAmount = instructedAmount;
  }
}
