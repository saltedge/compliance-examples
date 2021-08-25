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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.SDKConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Open Banking Account information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObAccount {
  /**
   * Account identifier on Provider
   */
  @NotEmpty
  @JsonProperty(SDKConstants.KEY_ID)
  public String id;

  /**
   * Identification of the currency in which the account is held (ISO 4217).
   * Usage: Currency should only be used in case one and the same account number covers several currencies and the initiating party needs to identify which currency needs to be used for settlement on the account.
   */
  @NotEmpty
  @JsonProperty(SDKConstants.KEY_CURRENCY)
  public String currencyCode;

  /**
   * Specifies the type of account (personal or business).
   * Allowed values: Business, Personal
   */
  @NotEmpty
  @JsonProperty("account_type")
  public String accountType;

  /**
   * Specifies the sub-type of account (product family group).
   * Allowed values: ChargeCard, CreditCard, CurrentAccount, EMoney, Loan, Mortgage, PrePaidCard, Savings
   */
  @NotEmpty
  @JsonProperty("account_sub_type")
  public String accountSubType;

  /**
   * Details to identify an account.
   */
  @JsonProperty("balances")
  @NotNull
  public List<ObBalance> balances;

  /**
   * Specifies the status of account resource in code form.
   * Allowed values: Enabled, Disabled, Deleted, ProForma, Pending
   */
  @JsonProperty(SDKConstants.KEY_STATUS)
  public String status;

  /**
   * Date and time at which the resource status was updated.
   */
  @JsonProperty("status_update_date_time")
  public Instant statusUpdatedAt;

  /**
   * Specifies the description of the account type.
   */
  @JsonProperty(SDKConstants.KEY_DESCRIPTION)
  public String description;

  /**
   * The nickname of the account, assigned by the account owner in order to provide an additional means of identification of the account.
   */
  @JsonProperty("nickname")
  public String nickname;

  /**
   * Date on which the account and related basic services are effectively operational for the account owner.
   */
  @JsonProperty("opening_date")
  public Instant openingDate;

  /**
   * Maturity date for the account.
   */
  @JsonProperty("maturity_date")
  public Instant maturityDate;

  /**
   * The switch status for the account.
   */
  @JsonProperty("switch_status")
  public String switchStatus;

  /**
   * Details to identify an account.
   */
  @JsonProperty("account")
  public List<ObAccountIdentifier> accountIdentifiers;

  /**
   * Party that manages the account on behalf of the account owner, that manages the registration and booking of entries on the account, calculates balances on the account and provides information about the account.
   */
  @JsonProperty("servicer")
  public ObAccountIdentifier servicer;

  public ObAccount(
          @NotEmpty String id,
          @NotEmpty String currencyCode,
          @NotEmpty String accountType,
          @NotEmpty String accountSubType,
          @NotNull List<ObBalance> balances
  ) {
    this.id = id;
    this.currencyCode = currencyCode;
    this.accountType = accountType;
    this.accountSubType = accountSubType;
  }

  public ObAccount(
          @NotEmpty String id,
          @NotEmpty String currencyCode,
          @NotEmpty String accountType,
          @NotEmpty String accountSubType,
          @NotNull List<ObBalance> balances,
          String status,
          Instant statusUpdatedAt,
          String description,
          String nickname,
          Instant openingDate,
          Instant maturityDate,
          String switchStatus,
          List<ObAccountIdentifier> identifiers,
          ObAccountIdentifier servicer
  ) {
    this.id = id;
    this.currencyCode = currencyCode;
    this.accountType = accountType;
    this.accountSubType = accountSubType;
    this.balances = balances;
    this.status = status;
    this.statusUpdatedAt = statusUpdatedAt;
    this.description = description;
    this.nickname = nickname;
    this.openingDate = openingDate;
    this.maturityDate = maturityDate;
    this.switchStatus = switchStatus;
    this.accountIdentifiers = identifiers;
    this.servicer = servicer;
  }

  @JsonIgnore
  public boolean hasIdentifier() {
    return !accountIdentifiers.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ObAccount obAccount = (ObAccount) o;
    return Objects.equals(id, obAccount.id) && Objects.equals(currencyCode, obAccount.currencyCode) && Objects.equals(accountType, obAccount.accountType) && Objects.equals(accountSubType, obAccount.accountSubType) && Objects.equals(balances, obAccount.balances) && Objects.equals(status, obAccount.status) && Objects.equals(statusUpdatedAt, obAccount.statusUpdatedAt) && Objects.equals(description, obAccount.description) && Objects.equals(nickname, obAccount.nickname) && Objects.equals(openingDate, obAccount.openingDate) && Objects.equals(maturityDate, obAccount.maturityDate) && Objects.equals(switchStatus, obAccount.switchStatus) && Objects.equals(accountIdentifiers, obAccount.accountIdentifiers) && Objects.equals(servicer, obAccount.servicer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, currencyCode, accountType, accountSubType, balances, status, statusUpdatedAt, description, nickname, openingDate, maturityDate, switchStatus, accountIdentifiers, servicer);
  }
}
