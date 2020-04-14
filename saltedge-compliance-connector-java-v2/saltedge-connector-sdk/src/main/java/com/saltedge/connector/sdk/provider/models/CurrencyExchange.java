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
package com.saltedge.connector.sdk.provider.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Info of exchange operation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyExchange {
    /**
     * Identification code of exchange operation.
     */
    @JsonProperty("contract_identification")
    public String contractIdentification;

    /**
     * For card accounts, only one exchange rate is used.
     */
    @JsonProperty("exchange_rate")
    public String exchangeRate;

    /**
     * Placed date on a quotation for products or services after which the quoted price is no longer enforceable.
     */
    @JsonProperty("quotation_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public LocalDate quotationDate;

    /**
     * Currency code from.
     */
    @JsonProperty("source_currency")
    public String sourceCurrency;

    /**
     * Currency code to.
     */
    @JsonProperty("target_currency")
    public String targetCurrency;

    /**
     * The value (code) of two currencies relative to each other.
     */
    @JsonProperty("unit_currency")
    public String unitCurrency;

    public CurrencyExchange() {
    }

    public CurrencyExchange(
            String contractIdentification,
            String exchangeRate,
            LocalDate quotationDate,
            String sourceCurrency,
            String targetCurrency,
            String unitCurrency
    ) {
        this.contractIdentification = contractIdentification;
        this.exchangeRate = exchangeRate;
        this.quotationDate = quotationDate;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.unitCurrency = unitCurrency;
    }
}
