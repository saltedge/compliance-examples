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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.api.models.validation.RequestAccountConstraint;
import com.saltedge.connector.sdk.models.ParticipantAccount;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import static com.saltedge.connector.sdk.SDKConstants.*;

/**
 * Payment data.
 */
@JsonIgnoreProperties
public class PaymentOrder {
  /**
   * Creditor account data.
   */
  @JsonProperty("creditor_account")
  @NotNull
  @RequestAccountConstraint
  public ParticipantAccount creditorAccount;

  /**
   * Creditor full name.
   */
  @JsonProperty("creditor_name")
  @NotEmpty
  public String creditorName;

  /**
   * Creditor address.
   */
  @JsonProperty("creditor_address")
  public ParticipantAddress creditorAddress;

  /**
   * Debtor account data.
   */
  @JsonProperty(DEBTOR_ACCOUNT)
  public ParticipantAccount debtorAccount;

  /**
   * Amount and currency.
   */
  @JsonProperty(KEY_INSTRUCTED_AMOUNT)
  @NotNull
  @Valid
  public Amount instructedAmount;

  /**
   * Payment identifier on TPP side.
   */
  @JsonProperty(KEY_END_TO_END_IDENTIFICATION)
  @NotEmpty
  public String endToEndIdentification;

  /**
   * Payment description.
   */
  @JsonProperty("remittance_information_unstructured")
  public String remittanceInformationUnstructured;

  /**
   * Creditor bank name.
   */
  @JsonProperty("creditor_agent_name")
  public String creditorAgentName;

  public PaymentOrder() {
  }

  public PaymentOrder(ParticipantAccount creditorAccount, @NotEmpty String creditorName, ParticipantAddress creditorAddress, String creditorAgentName, ParticipantAccount debtorAccount, @NotNull @Valid Amount instructedAmount, String endToEndIdentification, String remittanceInformationUnstructured) {
    this.creditorAccount = creditorAccount;
    this.creditorName = creditorName;
    this.creditorAddress = creditorAddress;
    this.creditorAgentName = creditorAgentName;
    this.debtorAccount = debtorAccount;
    this.instructedAmount = instructedAmount;
    this.endToEndIdentification = endToEndIdentification;
    this.remittanceInformationUnstructured = remittanceInformationUnstructured;
  }

  public ParticipantAccount getCreditorAccount() {
    return creditorAccount;
  }

  public void setCreditorAccount(ParticipantAccount creditorAccount) {
    this.creditorAccount = creditorAccount;
  }

  public String getCreditorName() {
    return creditorName;
  }

  public void setCreditorName(String creditorName) {
    this.creditorName = creditorName;
  }

  public ParticipantAddress getCreditorAddress() {
    return creditorAddress;
  }

  public void setCreditorAddress(ParticipantAddress creditorAddress) {
    this.creditorAddress = creditorAddress;
  }

  public ParticipantAccount getDebtorAccount() {
    return debtorAccount;
  }

  public void setDebtorAccount(ParticipantAccount debtorAccount) {
    this.debtorAccount = debtorAccount;
  }

  public Amount getInstructedAmount() {
    return instructedAmount;
  }

  public void setInstructedAmount(Amount instructedAmount) {
    this.instructedAmount = instructedAmount;
  }

  public String getEndToEndIdentification() {
    return endToEndIdentification;
  }

  public void setEndToEndIdentification(String endToEndIdentification) {
    this.endToEndIdentification = endToEndIdentification;
  }

  public String getRemittanceInformationUnstructured() {
    return remittanceInformationUnstructured;
  }

  public void setRemittanceInformationUnstructured(String remittanceInformationUnstructured) {
    this.remittanceInformationUnstructured = remittanceInformationUnstructured;
  }

  public String getCreditorAgentName() {
    return creditorAgentName;
  }

  public void setCreditorAgentName(String creditorAgentName) {
    this.creditorAgentName = creditorAgentName;
  }
}
