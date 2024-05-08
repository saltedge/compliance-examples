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
import com.saltedge.connector.sdk.models.domain.BaseEntity;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Entity(name = "CardAccount")
@Table(name = "CardAccount")
public class CardAccountEntity extends BaseEntity implements Serializable {

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "pan", nullable = false)
    public String pan;

    @Column(name = "currency_code", nullable = false)
    public String currencyCode;

    @Column(name = "product", nullable = false)
    public String product;

    @Column(name = "status", nullable = false)
    public String status;

    @Column(name = "available_amount", nullable = false)
    public String availableAmount;

    @Column(name = "balance", nullable = false)
    public String balance;

    @Column(name = "credit_limit", nullable = false)
    public String creditLimit;

    @Column(name = "extra", nullable = false)
    @Convert(converter = StringMapConverter.class)
    public Map<String, String> extra;

    @ManyToOne
    @JoinColumn
    public UserEntity user;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "cardAccount")
    public List<CardTransactionEntity> cardTransactions;

    public CardAccountEntity() {
    }

    public CardAccountEntity(
            String name,
            String pan,
            String currencyCode,
            String product,
            String status,
            String availableAmount,
            String balance,
            String creditLimit,
            Map<String, String> extra,
            UserEntity user
    ) {
        this.name = name;
        this.pan = pan;
        this.currencyCode = currencyCode;
        this.product = product;
        this.status = status;
        this.availableAmount = availableAmount;
        this.balance = balance;
        this.creditLimit = creditLimit;
        this.extra = extra;
        this.user = user;
    }
}
