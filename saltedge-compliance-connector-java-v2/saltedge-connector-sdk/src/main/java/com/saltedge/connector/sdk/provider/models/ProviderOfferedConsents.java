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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Consent data from bank.
 * Should be requested from User after successful oAuth authorization.
 *
 * Example:
 * {
 *   "balances": [
 *     {
 *       "iban": "MD123456"
 *     }
 *   ],
 *   "transactions": [
 *     {
 *       "iban": "MD123456"
 *     }
 *   ]
 * }
 */
public class ProviderOfferedConsents {
    @JsonProperty("balances")
    public List<ConsentData> balances;

    @JsonProperty("transactions")
    public List<ConsentData> transactions;

    public ProviderOfferedConsents() {
    }

    public ProviderOfferedConsents(List<ConsentData> balances, List<ConsentData> transactions) {
        this.balances = balances;
        this.transactions = transactions;
    }

    /**
     * Joins consents for balances and transactions in the single entity
     *
     * @param balancesConsents list of Account's for which user give consent to provide balance amount
     * @param transactionsConsents list of Account's for which user give consent to provide transactions list
     * @return list of ConsentData where each object contains consents for an account
     */
    public static ProviderOfferedConsents buildProviderOfferedConsents(
            List<AccountData> balancesConsents,
            List<AccountData> transactionsConsents
    ) {
        List<ConsentData> balances = balancesConsents.stream()
                .map(ProviderOfferedConsents::convertAccountDataToConsentData)
                .filter(Objects::nonNull).collect(Collectors.toList());
        List<ConsentData> transactions = transactionsConsents.stream()
                .map(ProviderOfferedConsents::convertAccountDataToConsentData)
                .filter(Objects::nonNull).collect(Collectors.toList());
        return new ProviderOfferedConsents(balances, transactions);
    }

    private static ConsentData convertAccountDataToConsentData(AccountData account) {
        if (account == null || StringUtils.isEmpty(account.iban)) return null;
        return new ConsentData(account.iban);
    }
}
