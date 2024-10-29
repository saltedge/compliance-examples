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
package com.saltedge.connector.example.model;

import com.saltedge.connector.example.model.converter.FeesConverter;
import com.saltedge.connector.example.model.converter.ObjectMapConverter;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Entity(name = "Payment")
@Table(name = "Payment")
public class PaymentEntity extends BaseEntity implements Serializable {

  @Column(name = "description")
  public String description;

  @Column(name = "status", nullable = false)
  public PaymentStatus status = PaymentStatus.PENDING;

  @Column(name = "amount")
  public String amount;

  @Column(name = "total")
  public double total;

  @Column(name = "currency")
  public String currency;

  @Column(name = "account_id")
  public Long accountId;

  @Column(name = "fees")
  @Convert(converter = FeesConverter.class)
  public List<Fee> fees;

  @Column(name = "payment_attributes")
  @Convert(converter = ObjectMapConverter.class)
  public Map<String, String> paymentAttributes;

  @Column(name = "extra")
  public String extra;

  @Column(name = "confirmation_code")
  public String confirmationCode;

  @Column(name = "from_iban")
  public String fromIban;

  @Column(name = "from_bic")
  public String fromBic;

  @Column(name = "from_account_name")
  public String fromAccountNumber;

  @Column(name = "from_sort_code")
  public String fromSortCode;

  @Column(name = "to_iban")
  public String toIban;

  @Column(name = "to_bic")
  public String toBic;

  @Column(name = "to_account_number")
  public String toAccountNumber;

  @Column(name = "to_sort_code")
  public String toSortCode;

  @Column(name = "to_account_name")
  public String toAccountName;

  @Column(name = "payment_product")
  public String paymentProduct;

  @ManyToOne
  @JoinColumn
  public UserEntity user;

  public PaymentEntity() {
  }

  public boolean isConfirmed() {
    return PaymentStatus.CONFIRMED.equals(status);
  }
}
