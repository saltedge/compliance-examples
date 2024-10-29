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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.SDKConstants;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Compliance Open Banking Transaction data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class ObTransaction {

  /**
   * Transaction identifier on Provider side
   */
  @NotEmpty
  @JsonProperty(SDKConstants.KEY_ID)
  public String id;

  /**
   * Unique reference for the transaction.
   * This reference is optionally populated, and may as an example be the FPID in the Faster Payments context.
   */
  @JsonProperty("transaction_reference")
  public String transactionReference;

  /**
   * Unique reference for the statement. This reference may be optionally populated if available.
   */
  @JsonProperty("statement_reference")
  public List<String> statementReference;

  /**
   * Indicates whether the transaction is a credit or a debit entry.
   */
  @NotEmpty
  @JsonProperty("credit_debit_indicator")
  public String creditDebitIndicator;

  /**
   * Status of a transaction entry on the books of the account servicer.
   * Allowed values: Booked, Pending
   */
  @NotEmpty
  @JsonProperty(SDKConstants.KEY_STATUS)
  public String status;

  /**
   * Specifies the Mutability of the Transaction record.
   */
  @JsonProperty("transaction_mutability")
  public String transactionMutability;

  /**
   * Date and time when a transaction entry is posted to an account on the account servicer's books.
   *
   * Usage: Booking date is the expected booking date, unless the status is booked, in which case it is the actual booking date.
   */
  @NotNull
  @JsonProperty("booking_date_time")
  public Instant bookingDateTime;

  /**
   * Date and time at which assets become available to the account owner in case of a credit entry, or cease to be available to the account owner in case of a debit transaction entry.
   *
   * Usage: If transaction entry status is pending and value date is present, then the value date refers to an expected/requested value date.
   * For transaction entries subject to availability/float and for which availability information is provided, the value date must not be used.
   * In this case the availability component identifies the number of availability days.
   */
  @JsonProperty("value_date_time")
  public Instant valueDateTime;

  /**
   * Further details of the transaction. This is the transaction narrative, which is unstructured text.
   */
  @JsonProperty("transaction_information")
  public String transactionInformation;

  /**
   * Amount of money in the cash transaction entry.
   */
  @NotNull
  @JsonProperty(SDKConstants.KEY_AMOUNT)
  public ObAmount amount;

  /**
   * Transaction charges to be paid by the charge bearer
   */
  @JsonProperty("charge_amount")
  public ObAmount chargeAmount;

  /**
   * Transaction charges to be paid by the charge bearer
   */
  @JsonProperty("currency_exchange")
  public ObCurrencyExchange currencyExchange;

  /**
   * Set of elements used to fully identify the type of underlying transaction resulting in an entry.
   */
  @JsonProperty("bank_transaction_code")
  public ObBankTransactionCode bankTransactionCode;

  /**
   * Set of elements to fully identify a proprietary bank transaction code.
   */
  @JsonProperty("proprietary_bank_transaction_code")
  public ObProprietaryBankTransactionCode proprietaryBankTransactionCode;

  /**
   * Define the balance as a numerical representation of the net increases and decreases in an account after a transaction entry is applied to the account.
   */
  @JsonProperty("balance")
  public ObBalance balance;

  /**
   * Details of the merchant involved in the transaction.
   */
  @JsonProperty("merchant_details")
  public ObMerchant merchantDetails;

  /**
   * Financial institution servicing an account for the creditor.
   */
  @JsonProperty("creditor_agent")
  public ObAgent creditorAgent;

  /**
   * Unambiguous identification of the account of the creditor, in the case of a debit transaction.
   */
  @JsonProperty("creditor_account")
  public ObAccountIdentifier creditorAccount;

  /**
   * Financial institution servicing an account for the debtor.
   */
  @JsonProperty("debtor_agent")
  public ObAgent debtorAgent;

  /**
   * Unambiguous identification of the account of the debtor, in the case of a credit transaction.
   */
  @JsonProperty("debtor_account")
  public ObAccountIdentifier debtorAccount;

  /**
   * Set of elements to describe the card instrument used in the transaction.
   */
  @JsonProperty("card_instrument")
  public ObCardIdentifier cardInstrument;

  /**
   * Additional information that can not be captured in the structured fields and/or any other specific block.
   */
  @JsonProperty("supplementary_data")
  public Map<String, String> supplementaryData;
}
