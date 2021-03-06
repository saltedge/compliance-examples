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
package com.saltedge.connector.sdk.api.controllers;

import com.saltedge.connector.sdk.api.mapping.EmptyJsonModel;
import com.saltedge.connector.sdk.api.mapping.ErrorsRequest;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller is responsible for handling `errors` requests
 */
@RestController
@RequestMapping(ErrorsV1Controller.BASE_PATH)
public class ErrorsV1Controller extends BaseV1Controller {
    public final static String BASE_PATH = Constants.API_BASE_PATH + "/v1/errors";
    private static Logger log = LoggerFactory.getLogger(AccountsV1Controller.class);

    /**
     * This endpoint is responsible for receiving validation errors of responses which Connector sends to Salt Edge PSD2 Compliance Solution
     *
     * @param token linked to Access-Token header
     * @param request which contains error data
     *
     * @return empty JSON object
     */
    @PostMapping
    public ResponseEntity<EmptyJsonModel> postError(Token token, @NotNull ErrorsRequest request) {
        log.error(request.toString());
        return super.createEmptyOkResponseEntity();
    }
}
