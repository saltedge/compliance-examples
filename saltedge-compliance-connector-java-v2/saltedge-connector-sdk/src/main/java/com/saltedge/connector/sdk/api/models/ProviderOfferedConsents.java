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
package com.saltedge.connector.sdk.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Consent data offered from bank.
 * Contains array of account's identifier for balances and transactions.
 * Should be managed by User after successful oAuth authentication.
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
 *       "masked_pan": "**** **** **** 1111"
 *     }
 *   ]
 * }
 */
public class ProviderOfferedConsents {
    /**
     * Array of account's identifier for balances.
     */
    @JsonProperty("balances")
    public List<ProviderOfferedConsent> balances;

    /**
     * Array of account's identifier for transactions.
     */
    @JsonProperty("transactions")
    public List<ProviderOfferedConsent> transactions;

    public ProviderOfferedConsents() {
    }

    public ProviderOfferedConsents(List<ProviderOfferedConsent> balances, List<ProviderOfferedConsent> transactions) {
        this.balances = balances;
        this.transactions = transactions;
    }

    /**
     * Joins consents for balances and transactions in the single entity
     *
     * @param balancesConsents list of Account's for which user give consent to provide balance amount
     * @param transactionsConsents list of Account's for which user give consent to provide transactions list
     * @param cardBalancesConsents list of Card Account's for which user give consent to provide balance amount
     * @param cardTransactionsConsents list of Card Account's for which user give consent to provide transactions list
     * @return list of ConsentData where each object contains consents for an account or card account
     */
    public static ProviderOfferedConsents buildProviderOfferedConsents(
            List<Account> balancesConsents,
            List<Account> transactionsConsents,
            List<CardAccount> cardBalancesConsents,
            List<CardAccount> cardTransactionsConsents
    ) {
        Stream<ProviderOfferedConsent> balances = balancesConsents.stream()
                .map(ProviderOfferedConsents::convertAccountToConsent)
                .filter(Objects::nonNull);
        Stream<ProviderOfferedConsent> transactions = transactionsConsents.stream()
                .map(ProviderOfferedConsents::convertAccountToConsent)
                .filter(Objects::nonNull);
        Stream<ProviderOfferedConsent> cardBalances = cardBalancesConsents.stream()
                .map(ProviderOfferedConsents::convertAccountToConsent)
                .filter(Objects::nonNull);
        Stream<ProviderOfferedConsent> cardTransactions = cardTransactionsConsents.stream()
                .map(ProviderOfferedConsents::convertAccountToConsent)
                .filter(Objects::nonNull);

        return new ProviderOfferedConsents(
                Stream.concat(balances, cardBalances).collect(Collectors.toList()),
                Stream.concat(transactions, cardTransactions).collect(Collectors.toList())
        );
    }

    private static ProviderOfferedConsent convertAccountToConsent(Account account) {
        if (account == null || StringUtils.isEmpty(account.getIban())) return null;
        return ProviderOfferedConsent.createAccountConsent(account.getIban());
    }

    private static ProviderOfferedConsent convertAccountToConsent(CardAccount card) {
        if (card == null || StringUtils.isEmpty(card.getMaskedPan())) return null;
        return ProviderOfferedConsent.createCardConsent(card.getMaskedPan());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderOfferedConsents that = (ProviderOfferedConsents) o;
        return Objects.equals(balances, that.balances) &&
                Objects.equals(transactions, that.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balances, transactions);
    }

    @Override
    public String toString() {
        return "ProviderOfferedConsents{" +
                "balances=" + balances +
                ", transactions=" + transactions +
                '}';
    }
}
