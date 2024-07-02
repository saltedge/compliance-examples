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
package com.saltedge.connector.example.model;

import com.saltedge.connector.example.model.converter.StringMapConverter;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.models.domain.BaseEntity;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Entity(name = "Account")
@Table(name = "Account")
public class AccountEntity extends BaseEntity implements Serializable {

  @Column(name = "name", nullable = false)
  public String name;

  @Column(name = "nature", nullable = false)
  public String nature;

  @Column(name = "currency_code", nullable = false)
  public String currencyCode;

  @Column(name = SDKConstants.KEY_IBAN, nullable = false)
  public String iban;

  @Column(name = "bic", nullable = false)
  public String bic;

  @Column(name = "bban", nullable = false)
  public String bban;

  @Column(name = "sort_code", nullable = false)
  public String sortCode;

  @Column(name = "swift_code", nullable = false)
  public String swiftCode;

  @Column(name = "available_amount", nullable = false)
  public String availableAmount;

  @Column(name = "balance", nullable = false)
  public String balance;

  @Column(name = "credit_limit", nullable = false)
  public String creditLimit;

  @Column(name = "is_payment_account", nullable = false)
  public Boolean isPaymentAccount;

  @Column(name = "status", nullable = false)
  public String status;

  @Column(name = "pan")
  public String pan;

  @Column(name = "extra", nullable = false)
  @Convert(converter = StringMapConverter.class)
  public Map<String, String> extra;

  @ManyToOne
  @JoinColumn
  public UserEntity user;

  @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
  public List<TransactionEntity> transactions;

  public AccountEntity() {
  }

  public AccountEntity(
    @NotNull String name,
    @NotNull String nature,
    @NotNull String currencyCode,
    @NotNull String iban,
    @NotNull String bic,
    @NotNull String bban,
    @NotNull String sortCode,
    @NotNull String swiftCode,
    @NotNull String availableAmount,
    @NotNull String balance,
    @NotNull String creditLimit,
    @NotNull Boolean isPaymentAccount,
    @NotNull String status,
    @NotNull String pan,
    @NotNull Map<String, String> extra,
    @NotNull UserEntity user
  ) {
    this.name = name;
    this.nature = nature;
    this.currencyCode = currencyCode;
    this.iban = iban;
    this.bic = bic;
    this.sortCode = sortCode;
    this.swiftCode = swiftCode;
    this.bban = bban;
    this.availableAmount = availableAmount;
    this.balance = balance;
    this.creditLimit = creditLimit;
    this.isPaymentAccount = isPaymentAccount;
    this.status = status;
    this.pan = pan;
    this.extra = extra;
    this.user = user;
  }
}
