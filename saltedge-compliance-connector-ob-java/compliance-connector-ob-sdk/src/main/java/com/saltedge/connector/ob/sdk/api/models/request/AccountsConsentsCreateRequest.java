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
package com.saltedge.connector.ob.sdk.api.models.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

/**
 * @see <a href="https://priora.saltedge.com/docs/aspsp/ob/ais#connector-endpoints-consents">Consents Endpoints</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class AccountsConsentsCreateRequest extends CreateBaseRequest {

    /**
     * Specifies the Open Banking account access data types.
     * This is a list of the data clusters being consented by the PSU, and requested for authorisation with the ASPSP.
     *
     * Allowed values:
     * ReadAccountsBasic,
     * ReadAccountsDetail,
     * ReadBalances,
     * ReadBeneficiariesDetail,
     * ReadTransactionsBasic,
     * ReadTransactionsCredits,
     * ReadTransactionsDebits,
     * ReadTransactionsDetail,
     * ReadOffers,
     * ReadPAN,
     * ReadParty,
     * ReadPartyPSU,
     * ReadProducts,
     * ReadStandingOrdersDetail,
     * ReadScheduledPaymentsDetail,
     * ReadStatementsDetail,
     * ReadDirectDebits
     */
    @NotNull
    @JsonProperty("permissions")
    public List<String> permissions;

    /**
     * Specified date and time the permissions will expire.
     * If this is not populated, the permissions will be open-ended.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("expiration_date_time")
    public Instant expirationDateTime;

    /**
     * Specified start date and time for the transaction query period.
     * If this is not populated, the start date will be open-ended, and data will be returned from the earliest available transaction.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("transaction_from_date_time")
    public Instant transactionFrom;

    /**
     * Specified end date and time for the transaction query period.
     * If this is not populated, the end date will be open-ended, and data will be returned to the latest available transaction.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("transaction_to_date_time")
    public Instant transactionTo;
}
