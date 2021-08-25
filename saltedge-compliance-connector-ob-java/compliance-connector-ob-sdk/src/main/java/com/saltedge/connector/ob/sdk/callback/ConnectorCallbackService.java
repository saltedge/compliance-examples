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
package com.saltedge.connector.ob.sdk.callback;

import com.saltedge.connector.ob.sdk.api.ApiConstants;
import com.saltedge.connector.ob.sdk.api.models.request.AuthorizationCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.AuthorizationUpdateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.PaymentUpdateRequest;
import com.saltedge.connector.ob.sdk.api.models.response.AuthorizationsCreateResponse;
import com.saltedge.connector.ob.sdk.api.models.response.AuthorizationsUpdateResponse;
import com.saltedge.connector.ob.sdk.api.models.response.EmptyJsonResponse;
import com.saltedge.connector.ob.sdk.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

/**
 * Priora callback service.
 * Send callback to CALLBACK_BASE_PATH based url
 */
@Service
public class ConnectorCallbackService extends CallbackRestClient {
    private static final Logger log = LoggerFactory.getLogger(ConnectorCallbackService.class);

    public AuthorizationsCreateResponse createAuthorization(AuthorizationCreateRequest params) {
        String url = getAuthorizationsUrl("");
        Response<AuthorizationsCreateResponse> response = sendCallback(url, HttpMethod.POST, params, AuthorizationsCreateResponse.class);
        if (StringUtils.hasText(response.error)) {//In some cases Priora can respond with 4xx and valid payload
            return JsonTools.parseObject(response.error, AuthorizationsCreateResponse.class);
        }
        return response.success;
    }

    public AuthorizationsUpdateResponse updateAuthorization(String authorizationId, AuthorizationUpdateRequest params) {
        String url = getAuthorizationsUrl(authorizationId);
        Response<AuthorizationsUpdateResponse> response = sendCallback(url, HttpMethod.PUT, params, AuthorizationsUpdateResponse.class);
        if (StringUtils.hasText(response.error)) {//In some cases Priora can respond with 4xx and valid payload
            return JsonTools.parseObject(response.error, AuthorizationsUpdateResponse.class);
        }
        return response.success;
    }

    /**
     * Send request to SaltEdge payment update endpoint.
     * https://priora.saltedge.com/docs/aspsp/ob/pis#salt-edge-endpoints-payments-payments-update
     *
     * @param paymentId SaltEdge Compliance service unique identifier of payment
     * @param params for payment update
     */
    public void updatePayment(String paymentId, PaymentUpdateRequest params) {
        String url = getPaymentsUrl(paymentId);
        Response<EmptyJsonResponse> response = sendCallback(url, HttpMethod.PUT, params, EmptyJsonResponse.class);
        if (StringUtils.hasText(response.error)) {//In some cases Priora can respond with 4xx and valid payload
            //TODO handle error
        }
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    private String getAuthorizationsUrl(String authorizationId) {
        String idPath = StringUtils.hasText(authorizationId) ? "/" + authorizationId : "";
        return createCallbackRequestUrl(ApiConstants.CALLBACK_BASE_PATH + "/authorizations" + idPath);
    }

    private String getPaymentsUrl(String paymentId) {
        String idPath = StringUtils.hasText(paymentId) ? "/" + paymentId : "";
        return createCallbackRequestUrl(ApiConstants.CALLBACK_BASE_PATH + "/payments" + idPath);
    }

    private <T> Response<T> sendCallback(String url, HttpMethod method, Object params, Class<T> clazz) {
        LinkedMultiValueMap<String, String> headers = createCallbackRequestHeaders();
        String payload = createJwtPayload(params);
        printPayload(url, method, headers, params, payload);
        return doCallbackRequest(url, method, headers, payload, clazz);
    }
}
