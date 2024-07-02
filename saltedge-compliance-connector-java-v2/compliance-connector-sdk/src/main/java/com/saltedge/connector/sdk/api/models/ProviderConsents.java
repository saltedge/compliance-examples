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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.SDKConstants;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Consent data offered from provider/ASPSP.
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
@JsonIgnoreProperties
public class ProviderConsents {
    /**
     * Only the value "allAccounts" is admitted in case of Global Consent
     * or null in case of Bank Offered Consent
     */
    @JsonProperty("allPsd2")
    @NotBlank
    public String globalAccessConsent;

    /**
     * Array of account's identifier for balances.
     */
    @JsonProperty(SDKConstants.KEY_BALANCES)
    public List<ProviderOfferedConsent> balances;

    /**
     * Array of account's identifier for transactions.
     */
    @JsonProperty("transactions")
    public List<ProviderOfferedConsent> transactions;

    public ProviderConsents() {
    }

    public ProviderConsents(String globalAccessConsent) {
        this.globalAccessConsent = globalAccessConsent;
    }

    public ProviderConsents(List<ProviderOfferedConsent> balances, List<ProviderOfferedConsent> transactions) {
        this.balances = balances;
        this.transactions = transactions;
    }

    @JsonIgnore
    public boolean hasGlobalConsent() {
        return GLOBAL_CONSENT_VALUE.equals(globalAccessConsent);
    }

    public static  final String GLOBAL_CONSENT_VALUE = "allAccounts";

    /**
     * Joins consents for balances and transactions in the single entity
     *
     * @param balancesConsents list of Account's for which user give consent to provide balance amount
     * @param transactionsConsents list of Account's for which user give consent to provide transactions list
     * @param cardBalancesConsents list of Card Account's for which user give consent to provide balance amount
     * @param cardTransactionsConsents list of Card Account's for which user give consent to provide transactions list
     * @return list of ConsentData where each object contains consents for an account or card account
     */
    public static ProviderConsents buildProviderOfferedConsents(
            List<Account> balancesConsents,
            List<Account> transactionsConsents,
            List<CardAccount> cardBalancesConsents,
            List<CardAccount> cardTransactionsConsents
    ) {
        Stream<ProviderOfferedConsent> balances = balancesConsents.stream()
                .map(ProviderConsents::convertAccountToConsent)
                .filter(Objects::nonNull);
        Stream<ProviderOfferedConsent> transactions = transactionsConsents.stream()
                .map(ProviderConsents::convertAccountToConsent)
                .filter(Objects::nonNull);
        Stream<ProviderOfferedConsent> cardBalances = cardBalancesConsents.stream()
                .map(ProviderConsents::convertAccountToConsent)
                .filter(Objects::nonNull);
        Stream<ProviderOfferedConsent> cardTransactions = cardTransactionsConsents.stream()
                .map(ProviderConsents::convertAccountToConsent)
                .filter(Objects::nonNull);

        return new ProviderConsents(
                Stream.concat(balances, cardBalances).collect(Collectors.toList()),
                Stream.concat(transactions, cardTransactions).collect(Collectors.toList())
        );
    }

    public static ProviderConsents buildAllAccountsConsent() {
        return new ProviderConsents(new ArrayList<>(), new ArrayList<>());
    }

    private static ProviderOfferedConsent convertAccountToConsent(Account account) {
        if (account == null) return null;
        if (!account.hasIdentifier()) return null;

        if (StringUtils.hasText(account.getIban())) {
            return ProviderOfferedConsent.createAccountConsentFromIban(account.getIban());
        } else {
            return ProviderOfferedConsent.createAccountConsentFromBban(account.getBban());
        }
    }

    private static ProviderOfferedConsent convertAccountToConsent(CardAccount card) {
        if (card == null || !StringUtils.hasText(card.getMaskedPan())) return null;
        return ProviderOfferedConsent.createCardConsent(card.getMaskedPan());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderConsents that = (ProviderConsents) o;
        return Objects.equals(globalAccessConsent, that.globalAccessConsent) &&
                Objects.equals(balances, that.balances) &&
                Objects.equals(transactions, that.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(globalAccessConsent, balances, transactions);
    }

    @Override
    public String toString() {
        return "ProviderOfferedConsents{" +
                "globalAccessConsent='" + globalAccessConsent + '\'' +
                ", balances=" + balances +
                ", transactions=" + transactions +
                '}';
    }
}
