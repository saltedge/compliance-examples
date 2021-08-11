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
package com.saltedge.connector.ob.sdk.provider.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.SDKConstants;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccountIdentifier;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAddress;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAmount;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObRemittanceInformation;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;

/**
 * Open Banking initiation payload is sent by the initiating party to the ASPSP.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class ObPaymentInitiationData {
  /**
   * Unique identification as assigned by an instructing party for an instructed party to unambiguously identify the instruction.
   */
  @JsonProperty("instruction_identification")
  @NotEmpty
  public String instructionIdentification;

  /**
   * Unique identification assigned by the initiating party to unambiguously identify the transaction.
   */
  @JsonProperty("end_to_end_identification")
  @NotEmpty
  public String endToEndIdentification;

  /**
   * Amount of money to be moved between the debtor and creditor, before deduction of charges, expressed in the currency as ordered by the initiating party.
   */
  @JsonProperty(SDKConstants.KEY_INSTRUCTED_AMOUNT)
  @NotNull
  @Valid
  public ObAmount instructedAmount;

  /**
   * Unambiguous identification of the account of the debtor, in the case of a credit transaction.
   */
  @JsonProperty("debtor_account")
  public ObAccountIdentifier debtorAccount;

  /**
   * Provides the details to identify the beneficiary account.
   */
  @JsonProperty("creditor_account")
  @NotNull
  @Valid
  public ObAccountIdentifier creditorAccount;

  /**
   * Information that locates and identifies a specific address, as defined by postal services.
   */
  @JsonProperty("creditor_postal_address")
  public ObAddress creditorPostalAddress;

  /**
   * Information supplied to enable the matching/reconciliation of an entry with the items that the payment is intended to settle, such as commercial invoices in an accounts' receivable system, in an unstructured form.
   */
  @JsonProperty("remittance_information")
  public ObRemittanceInformation remittanceInformation;

  /**
   * Additional information that can not be captured in the structured fields and/or any other specific block.
   */
  @JsonProperty("supplementary_data")
  public Map<String, String> supplementaryData;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ObPaymentInitiationData that = (ObPaymentInitiationData) o;
    return Objects.equals(instructionIdentification, that.instructionIdentification) && Objects.equals(endToEndIdentification, that.endToEndIdentification) && Objects.equals(instructedAmount, that.instructedAmount) && Objects.equals(debtorAccount, that.debtorAccount) && Objects.equals(creditorAccount, that.creditorAccount) && Objects.equals(creditorPostalAddress, that.creditorPostalAddress) && Objects.equals(remittanceInformation, that.remittanceInformation) && Objects.equals(supplementaryData, that.supplementaryData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(instructionIdentification, endToEndIdentification, instructedAmount, debtorAccount, creditorAccount, creditorPostalAddress, remittanceInformation, supplementaryData);
  }
}
