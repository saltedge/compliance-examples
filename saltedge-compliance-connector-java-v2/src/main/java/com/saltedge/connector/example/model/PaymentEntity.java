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

import com.saltedge.connector.example.model.converter.FeesConverter;
import com.saltedge.connector.example.model.converter.ObjectMapConverter;
import com.saltedge.connector.sdk.models.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.saltedge.connector.sdk.SDKConstants.PAYMENT_PRODUCT_FASTER_PAYMENT_SERVICE;

@Entity(name = "Payment")
@Table(name = "Payment")
public class PaymentEntity extends BaseEntity implements Serializable {

  @Column(name = "description", nullable = false)
  public String description;

  @Column(name = "status", nullable = false)
  public PaymentStatus status = PaymentStatus.PENDING;

  @Column(name = "amount", nullable = false)
  public double amount;

  @Column(name = "total", nullable = false)
  public double total;

  @Column(name = "currency")
  public String currency;

  @Column(name = "account_id", nullable = false)
  public Long accountId;

  @Column(name = "fees", nullable = false)
  @Convert(converter = FeesConverter.class)
  public List<Fee> fees;

  @Column(name = "payment_attributes", nullable = false)//TODO investigate usage
  @Convert(converter = ObjectMapConverter.class)
  public Map<String, String> paymentAttributes;

  @Column(name = "extra", nullable = false)
  public String extra;

  @Column(name = "confirmation_code")
  public String confirmationCode;

  @Column(name = "from_iban")
  public String fromIban;

  @Column(name = "from_bic")
  public String fromBic;

  @Column(name = "from_bban")
  public String fromBban;

  @Column(name = "from_sort_code")
  public String fromSortCode;

  @Column(name = "to_iban")
  public String toIban;

  @Column(name = "to_bic")
  public String toBic;

  @Column(name = "to_bban")
  public String toBban;

  @Column(name = "to_sort_code")
  public String toSortCode;

  @Column(name = "to_account_name")
  public String toAccountName;

  @Column(name = "payment_product", nullable = false)
  public String paymentProduct;

  @ManyToOne
  @JoinColumn
  public UserEntity user;

  public PaymentEntity() {
  }

  public boolean isConfirmed() {
    return PaymentStatus.CONFIRMED.equals(status);
  }

  public boolean isFps() {
    return PAYMENT_PRODUCT_FASTER_PAYMENT_SERVICE.equals(paymentProduct);
  }

  @Override
  public String toString() {
    return "PaymentEntity{" +
            "description='" + description + '\'' +
            ", status=" + status +
            ", amount=" + amount +
            ", total=" + total +
            ", currency='" + currency + '\'' +
            ", accountId=" + accountId +
            ", fees=" + fees +
            ", paymentAttributes=" + paymentAttributes +
            ", extra='" + extra + '\'' +
            ", confirmationCode='" + confirmationCode + '\'' +
            ", fromIban='" + fromIban + '\'' +
            ", fromBic='" + fromBic + '\'' +
            ", fromBban='" + fromBban + '\'' +
            ", fromSortCode='" + fromSortCode + '\'' +
            ", toIban='" + toIban + '\'' +
            ", toBic='" + toBic + '\'' +
            ", toBban='" + toBban + '\'' +
            ", toSortCode='" + toSortCode + '\'' +
            ", toAccountName='" + toAccountName + '\'' +
            ", paymentProduct='" + paymentProduct + '\'' +
            ", user=" + user +
            '}';
  }
}
