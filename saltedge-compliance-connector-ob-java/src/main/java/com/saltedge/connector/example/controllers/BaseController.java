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
package com.saltedge.connector.example.controllers;

import com.saltedge.connector.example.model.repository.AccountsRepository;
import com.saltedge.connector.example.model.repository.PaymentsRepository;
import com.saltedge.connector.example.model.repository.TransactionsRepository;
import com.saltedge.connector.example.model.repository.UsersRepository;
import com.saltedge.connector.ob.sdk.provider.ConnectorSDKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

abstract public class BaseController {
    @Autowired
    protected ConnectorSDKService connectorSDKService;
    @Autowired
    protected UsersRepository usersRepository;
    @Autowired
    protected AccountsRepository accountsRepository;

    protected Long findUser(String username, String password) {
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            return usersRepository.findFirstByUsernameAndPassword(username, password).map(user -> user.id).orElse(null);
        } else {
            return null;
        }
    }
}
