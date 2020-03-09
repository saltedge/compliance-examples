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
import com.saltedge.connector.example.model.converter.StringMapConverter;
import com.saltedge.connector.sdk.models.persistence.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Entity(name = "Payment")
@Table(name = "Payment")
public class PaymentEntity extends BaseEntity implements Serializable {

    @Column(name = "description", nullable = false)
    public String description;

    @Column(name = "status", nullable = false)
    public Status status = Status.PENDING;

    @Column(name = "amount", nullable = false)
    public double amount;

    @Column(name = "total", nullable = false)
    public double total;

    @Column(name = "account_id", nullable = false)
    public Long accountId;

    @Column(name = "fees", nullable = false)
    @Convert(converter = FeesConverter.class)
    public List<Fee> fees;

    @Column(name = "payment_attributes", nullable = false)
    @Convert(converter = ObjectMapConverter.class)
    public Map<String, String> paymentAttributes;

    @Column(name = "extra", nullable = false)
    @Convert(converter = StringMapConverter.class)
    public Map<String, String> extra;

    @Column(name = "payment_template_code")
    public String paymentTemplateCode;

    @Column(name = "confirmation_code")
    public String confirmationCode;

    @ManyToOne
    @JoinColumn
    public UserEntity user;

    public PaymentEntity() {
    }

    public PaymentEntity(String paymentTemplateCode,
                         String description,
                         Status status,
                         double amount,
                         List<Fee> fees,
                         double total,
                         Long fromAccountId,
                         Map<String, String> paymentAttributes,
                         Map<String, String> extra,
                         UserEntity user) {
        this.paymentTemplateCode = paymentTemplateCode;
        this.description = description;
        this.status = status;
        this.amount = amount;
        this.fees = fees;
        this.total = total;
        this.accountId = fromAccountId;
        this.paymentAttributes = paymentAttributes;
        this.extra = extra;
        this.user = user;
    }

    public static enum Status {
        PENDING, CONFIRMED, FAILED, CLOSED
    }

    public boolean isConfirmed() {
        return PaymentEntity.Status.CONFIRMED.equals(status);
    }
}
