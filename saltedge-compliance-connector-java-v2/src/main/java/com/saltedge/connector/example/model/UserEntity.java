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

import com.saltedge.connector.sdk.models.domain.BaseEntity;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity(name = "User")
@Table(name = "User")
public class UserEntity extends BaseEntity implements Serializable {

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "email", nullable = false)
    public String email;

    @Column(name = "address", nullable = false)
    public String address;

    @Column(name = "dob", nullable = false)
    public String dob;

    @Column(name = "phone", nullable = false)
    public String phone;

    @Column(name = "username", nullable = false, unique = true)
    public String username;

    @Column(name = "password", nullable = false)
    public String password;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "user")//, fetch = FetchType.EAGER
    public List<AccountEntity> accounts;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "user")
    public List<CardAccountEntity> cardAccounts;

    public UserEntity() {
    }

    public UserEntity(String name, String email, String address, String dob, String phone, String username, String password) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.dob = dob;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }
}
