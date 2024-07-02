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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.sdk.SDKConstants;
import jakarta.annotation.Nullable;
import org.springframework.util.StringUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Account information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {
  /**
   * Account identifier on Provider
   */
  @JsonProperty(SDKConstants.KEY_ID)
  @NotBlank
  private String id;

  /**
   * Human readable account name
   */
  @JsonProperty(SDKConstants.KEY_NAME)
  @NotBlank
  private String name;

  @JsonProperty(SDKConstants.KEY_BALANCES)
  @NotNull
  private List<AccountBalance> balances;

  /**
   * ExternalCashAccountType1Code from ISO 20022 (https://www.iso20022.org/).
   * Allowed values: CACC, CASH, CISH, COMM, CPAC, LLSV, LOAN, MGLD, MOMA, NREX, ODFT, ONDP, OTHR, SACC, SLRY, SVGS, TAXE, TRAN, TRAS
   */
  @JsonProperty("cash_account_type")
  @NotBlank
  private String cashAccountType;

  /**
   * Currency code of account (ISO 4217).
   */
  @JsonProperty(SDKConstants.KEY_CURRENCY)
  @NotBlank
  private String currencyCode;

  /**
   * A number uniquely identifying a subscription in a Global System for Mobile communications or a Universal Mobile Telecommunications System mobile network.
   */
  @JsonProperty(SDKConstants.KEY_MSISDN)
  private String msisdn;

  /**
   * Product Name of the Bank for this account, proprietary definition.
   */
  @JsonProperty(SDKConstants.KEY_PRODUCT)
  @NotBlank
  private String product;

  /**
   * Current status of the account. Allowed values: enabled, deleted, blocked
   */
  @JsonProperty(SDKConstants.KEY_STATUS)
  @NotBlank
  private String status;

  /**
   * Any additional information deemed relevant to a account
   */
  @JsonProperty(SDKConstants.KEY_EXTRA)
  private Map<String, String> extra;

  /**
   * Basic Bank Account Number
   */
  @JsonProperty(SDKConstants.KEY_BBAN)
  private String bban;

  /**
   * Bank Identifier Code
   */
  @JsonProperty(SDKConstants.KEY_BIC)
  private String bic;

  /**
   * International Bank Account Number
   */
  @JsonProperty(SDKConstants.KEY_IBAN)
  private String iban;

  /**
   * Number code, which is used by British and Irish banks.
   */
  @JsonProperty(SDKConstants.KEY_SORT_CODE)
  private String sortCode;

  public Account() {
  }

  public Account(
    @NotBlank String id,
    @NotBlank String name,
    @NotNull List<AccountBalance> balances,
    @NotBlank String cashAccountType,
    @NotBlank String currencyCode
  ) {
    this.id = id;
    this.name = name;
    this.balances = balances;
    this.cashAccountType = cashAccountType;
    this.currencyCode = currencyCode;
  }

  /**
   * Check if `bban` or `bic` or `iban` or `sort_code` field is not empty
   *
   * @return true if model contains at least one identifier
   */
  public boolean hasIdentifier() {
    return StringUtils.hasText(bban)
      || StringUtils.hasText(bic)
      || StringUtils.hasText(iban)
      || StringUtils.hasText(sortCode);
  }

  /**
   * Check if `bban` or `bic` or `iban` or `sort_code` field is equal to account code
   *
   * @param accountCode an unique account code (can be iban or bban or bic or sort_code)
   * @return true if one of identifiers is equal to accountCode param
   */
  public boolean containsAccountIdentifier(@NotEmpty String accountCode) {
    if (StringUtils.hasText(accountCode)) return accountCode.equals(iban) || accountCode.equals(bban) || accountCode.equals(bic) || accountCode.equals(sortCode);
    else return false;
  }

  /**
   * Check if `bban` or `bic` or `iban` or `sort_code` field intersects with identifiers
   *
   * @param identifiers a list of unique account codes (can be iban or bban or bic or sort_code)
   * @return true if one of identifiers intersects with identifiers. If identifiers is NULL or Empty returns true.
   */
  public boolean containsAccountIdentifiers(@Nullable List<ProviderOfferedConsent> identifiers) {
    if (identifiers == null || identifiers.isEmpty()) return true;

    return identifiers.stream().anyMatch(identifier ->
            (StringUtils.hasText(identifier.iban) && Objects.equals(this.iban, identifier.iban))
                    || (StringUtils.hasText(identifier.bban) && Objects.equals(this.bban, identifier.bban))
                    || (StringUtils.hasText(identifier.bic) && Objects.equals(this.bic, identifier.bic))
                    || (StringUtils.hasText(identifier.bankAccountIdentifier) && Objects.equals(this.sortCode, identifier.bankAccountIdentifier))
    );
  }

  /**
   * Find first Account balance object by it type.
   *
   * @param balanceType unique type code of account balance
   * @return AccountBalance object
   * @see AccountBalance
   */
  public AccountBalance getBalance(@NotEmpty String balanceType) {
    if (balances == null || balances.isEmpty() || StringUtils.isEmpty(balanceType)) return null;
    return balances.stream()
      .filter(model -> balanceType.equals(model.type))
      .findFirst()
      .orElse(null);
  }

  public String getId() {
    return id;
  }

  public void setId(@NotBlank String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(@NotBlank String name) {
    this.name = name;
  }

  public List<AccountBalance> getBalances() {
    return balances;
  }

  public void setBalances(List<AccountBalance> balances) {
    this.balances = balances;
  }

  public String getCashAccountType() {
    return cashAccountType;
  }

  public void setCashAccountType(@NotBlank String cashAccountType) {
    this.cashAccountType = cashAccountType;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(@NotBlank String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public String getBban() {
    return bban;
  }

  public void setBban(String bban) {
    this.bban = bban;
  }

  public String getBic() {
    return bic;
  }

  public void setBic(String bic) {
    this.bic = bic;
  }

  public String getIban() {
    return iban;
  }

  public void setIban(String iban) {
    this.iban = iban;
  }

  public String getSortCode() {
    return sortCode;
  }

  public void setSortCode(String sortCode) {
    this.sortCode = sortCode;
  }

  public String getMsisdn() {
    return msisdn;
  }

  public void setMsisdn(String msisdn) {
    this.msisdn = msisdn;
  }

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Map<String, String> getExtra() {
    return extra;
  }

  public void setExtra(Map<String, String> extra) {
    this.extra = extra;
  }

  @Override
  public String toString() {
    return "Account{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", balances=" + balances +
        ", cashAccountType='" + cashAccountType + '\'' +
        ", currencyCode='" + currencyCode + '\'' +
        ", msisdn='" + msisdn + '\'' +
        ", product='" + product + '\'' +
        ", status='" + status + '\'' +
        ", extra=" + extra +
        ", bban='" + bban + '\'' +
        ", bic='" + bic + '\'' +
        ", iban='" + iban + '\'' +
        ", sortCode='" + sortCode + '\'' +
        '}';
  }
}
