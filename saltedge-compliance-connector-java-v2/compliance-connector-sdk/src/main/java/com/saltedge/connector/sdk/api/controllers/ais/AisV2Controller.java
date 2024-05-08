/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2023 Salt Edge.
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
package com.saltedge.connector.sdk.api.controllers.ais;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.controllers.BaseV2Controller;
import com.saltedge.connector.sdk.api.models.EmptyJsonModel;
import com.saltedge.connector.sdk.api.models.Meta;
import com.saltedge.connector.sdk.api.models.requests.AisRefreshRequest;
import com.saltedge.connector.sdk.api.models.requests.CreatePaymentRequest;
import com.saltedge.connector.sdk.api.models.requests.DefaultRequest;
import com.saltedge.connector.sdk.api.models.requests.TransactionsRequest;
import com.saltedge.connector.sdk.api.models.responses.AccountsResponse;
import com.saltedge.connector.sdk.api.models.responses.TransactionsResponse;
import com.saltedge.connector.sdk.models.TransactionsPage;
import com.saltedge.connector.sdk.models.domain.AisToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * This controller is responsible for receiving refresh request of account information for Account Information Service.
 * <a href="https://priora.saltedge.com/docs/aspsp/v2/ais">AIS</a>
 */
@RestController
@RequestMapping(AisV2Controller.BASE_PATH)
@Validated
public class AisV2Controller extends BaseV2Controller {
    public final static String BASE_PATH = SDKConstants.API_BASE_PATH + "/ais";
    private static final Logger log = LoggerFactory.getLogger(AisV2Controller.class);

    /**
     * This endpoint is responsible for refreshing account information on connector side.
     *
     * @param token linked to Access-Token header
     * @param accountId unique id of bank account
     * @param request data
     * @return list of transactions data with nextId of next page
     */
    @PostMapping(path = "/refresh")
    public ResponseEntity<EmptyJsonModel> create(@Valid AisRefreshRequest request) {
        providerService.refresh(request.getProviderCode(), request.getSessionSecret());
        return super.createEmptyOkResponseEntity();
    }
}
