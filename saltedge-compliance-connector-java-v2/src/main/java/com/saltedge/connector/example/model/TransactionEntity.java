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
import com.saltedge.connector.sdk.models.domain.BaseEntity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity(name = "Transaction")
@Table(name = "Transaction")
public class TransactionEntity extends BaseEntity implements Serializable {

    @Column(name = "amount", nullable = false)
    public String amount;

    @Column(name = "description", nullable = false)
    public String description;

    @Column(name = "madeOn", nullable = false)
    public LocalDate madeOn;

    @Column(name = "postDate", nullable = true)
    public LocalDate postDate;

    @Column(name = "status", nullable = false)
    public String status;

    @Column(name = "currency_code", nullable = false)
    public String currencyCode;

    @Column(name = "to_iban", nullable = true)
    public String toIban;

    @Column(name = "to_account_name", nullable = true)
    public String toAccountName;

    @Column(name = "to_currency_code", nullable = true)
    public String toCurrencyCode;


    @Column(name = "fees", nullable = false)
    @Convert(converter = FeesConverter.class)
    public List<Fee> fees;

    @Column(name = "extra", nullable = false)
    public String paymentExtra;

    @ManyToOne
    @JoinColumn
    public AccountEntity account;

    public TransactionEntity() {
    }

    public TransactionEntity(
            String amount,
            String currencyCode,
            String description,
            LocalDate madeOn,
            String status,
            List<Fee> fees,
            String paymentExtra,
            AccountEntity account
    ) {
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.description = description;
        this.madeOn = madeOn;
        this.status = status;
        this.fees = fees;
        this.paymentExtra = paymentExtra;
        this.account = account;
    }
}
