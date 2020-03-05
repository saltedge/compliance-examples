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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Additional information deemed relevant to a payment.
 * All fields are optional
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionExtra {
    /**
     * Additional details given for the related transaction.
     */
    @JsonProperty("additional_information")
    public String additionalInformation;

    /**
     * Bank transaction code as used by the ASPSP and using the sub-elements of this structured code defined by ISO20022.
     */
    @JsonProperty("bank_transaction_code")
    public String bankTransactionCode;

    /**
     * Identification of a Cheque.
     */
    @JsonProperty("check_id")
    public String checkId;

    /**
     * Is the identification of the transaction as used e.g. for reference for deltafunction on application level. The same identification as for example used within camt.05x messages.
     */
    @JsonProperty("entry_reference")
    public String entryReference;

    /**
     * Identification of Mandates, e.g. a SEPA Mandate ID.
     */
    @JsonProperty("mandate_id")
    public String mandateId;

    /**
     * Proprietary bank transaction code as used within a community or within an ASPSP e.g. for MT94x based transaction reports.
     */
    @JsonProperty("proprietary_bank_transaction_code")
    public String proprietaryBankTransactionCode;

    /**
     * Purpose code.
     */
    @JsonProperty("purpose_code")
    public String purposeCode;

    /**
     * The ultimate party to which an amount of money is due. The ultimate creditor could be the same as creditor or it could be different, such as the seller.
     */
    @JsonProperty("ultimate_creditor")
    public String ultimateCreditor;

    /**
     * The ultimate party that owes an amount of money to the (ultimate) creditor, such as the buyer of services or goods.
     */
    @JsonProperty("ultimate_debtor")
    public String ultimateDebtor;
}
