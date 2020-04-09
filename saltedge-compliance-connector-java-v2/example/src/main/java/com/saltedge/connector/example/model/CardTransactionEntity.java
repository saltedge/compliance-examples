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
import com.saltedge.connector.example.model.converter.StringMapConverter;
import com.saltedge.connector.sdk.models.persistence.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity(name = "CardTransaction")
@Table(name = "CardTransaction")
public class CardTransactionEntity extends BaseEntity implements Serializable {

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

    @Column(name = "pan", nullable = true)
    public String pan;

    @Column(name = "fees", nullable = false)
    @Convert(converter = FeesConverter.class)
    public List<Fee> fees;

    @Column(name = "extra", nullable = false)
    @Convert(converter = StringMapConverter.class)
    public Map<String, String> extra;

    @ManyToOne
    @JoinColumn
    public CardAccountEntity cardAccount;

    public CardTransactionEntity() {
    }

    public CardTransactionEntity(String amount,
                                 String currencyCode,
                                 String description,
                                 LocalDate madeOn,
                                 String status,
                                 List<Fee> fees,
                                 Map<String, String> extra,
                                 CardAccountEntity cardAccount
    ) {
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.description = description;
        this.madeOn = madeOn;
        this.status = status;
        this.fees = fees;
        this.extra = extra;
        this.cardAccount = cardAccount;
    }
}
