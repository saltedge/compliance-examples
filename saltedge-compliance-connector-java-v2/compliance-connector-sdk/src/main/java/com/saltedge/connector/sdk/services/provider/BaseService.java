/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2022 Salt Edge.
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
package com.saltedge.connector.sdk.services.provider;

import com.saltedge.connector.sdk.callback.SessionsCallbackService;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.models.domain.AisTokensRepository;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import com.saltedge.connector.sdk.models.domain.PiisTokensRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base service for services which processes provider requests
 */
abstract class BaseService {
    @Autowired
    protected SessionsCallbackService callbackService;
    @Autowired
    protected AisTokensRepository aisTokensRepository;
    @Autowired
    protected PiisTokensRepository piisTokensRepository;

    public AisToken findAisTokenBySessionSecret(String sessionSecret) {
        return aisTokensRepository.findFirstBySessionSecret(sessionSecret);
    }

    public PiisToken findPiisTokenBySessionSecret(String sessionSecret) {
        return piisTokensRepository.findFirstBySessionSecret(sessionSecret);
    }
}
