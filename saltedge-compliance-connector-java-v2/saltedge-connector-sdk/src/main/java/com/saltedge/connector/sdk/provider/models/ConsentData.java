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
import com.saltedge.connector.sdk.config.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Consent data from user.
 * Should be requested from User after oAuth authorization.
 *
 * Example: [{"account_id": "AccountId", "scopes": ["balance", "transactions"]}]
 */
public class ConsentData {
    /**
     * Account ID
     */
    @JsonProperty(Constants.KEY_ACCOUNT_ID)
    public String accountId;

    /**
     * Set of permissions for access token.
     * Allowed values: balance, transactions
     */
    @JsonProperty(Constants.KEY_SCOPES)
    public List<Scopes> scopes;

    public ConsentData() {
    }

    public ConsentData(String accountId, List<Scopes> scopes) {
        this.accountId = accountId;
        this.scopes = scopes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsentData that = (ConsentData) o;
        if (!Objects.equals(accountId, that.accountId)) return false;
        return Objects.equals(scopes, that.scopes);
    }

    @Override
    public int hashCode() {
        int result = accountId != null ? accountId.hashCode() : 0;
        result = 31 * result + (scopes != null ? scopes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ConsentData{" +
                "accountId='" + accountId + '\'' +
                ", scopes=" + scopes +
                '}';
    }

    /**
     * Joins consents for accounts, balances and transactions in the single entity
     *
     * @param consentToAccounts list of unique ID's of account for which user give consent to provide account name
     * @param consentToBalances list of unique ID's of accounts for which user give consent to provide balance amount
     * @param consentToTransactions list of unique ID's of accounts for which user give consent to provide transactions list
     * @return list of ConsentData where each object contains consents for an account
     */
    public static List<ConsentData> joinConsents(
            List<String> consentToAccounts,
            List<String> consentToBalances,
            List<String> consentToTransactions
    ) {
        List<ConsentData> result = new ArrayList<>();
        for (String accountId : consentToAccounts) {
            List<Scopes> scopes = new ArrayList<>();
            if (consentToBalances.contains(accountId)) scopes.add(Scopes.balance);
            if (consentToTransactions.contains(accountId)) scopes.add(Scopes.transactions);
            result.add(new ConsentData(accountId, scopes));
        }
        return result;
    }

    public enum Scopes {
        balance, transactions
    }
}
