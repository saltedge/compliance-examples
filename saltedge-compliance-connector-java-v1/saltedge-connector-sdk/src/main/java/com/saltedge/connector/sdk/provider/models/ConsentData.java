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

/**
 * Consent data from user.
 * Should be requested from User after oAuth authorization.
 *
 * Example: [{"account_id": "all", "scopes": ["balance", "transactions"]}]
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

    public static List<ConsentData> createConsents(
            List<Long> accounts,
            List<Long> balancesOfAccounts,
            List<Long> transactionsOfAccounts
    ) {
        List<ConsentData> result = new ArrayList<>();
        for (Long accountId : accounts) {
            List<Scopes> scopes = new ArrayList<>();
            if (balancesOfAccounts.contains(accountId)) scopes.add(Scopes.balance);
            if (transactionsOfAccounts.contains(accountId)) scopes.add(Scopes.transactions);
            result.add(new ConsentData(accountId.toString(), scopes));
        }
        return result;
    }

    public enum Scopes {
        balance, transactions
    }
}
