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
package com.saltedge.connector.sdk.provider;

import com.saltedge.connector.sdk.provider.models.ConsentData;

import java.util.List;
import java.util.Map;

/**
 * Interface for call back communication from Provider to Connector
 */
public interface ProviderCallback {
    /**
     * Notify about oAuth authorization success
     *
     * @param sessionSecret of authorization session
     * @param userId of authorized User
     * @param consents list of checked by user account consents
     *
     * @return returnUrl where should be redirected user
     */
    String authorizationOAuthSuccess(String sessionSecret, String userId, List<ConsentData> consents);

    /**
     * Notify about oAuth authorization fail
     *
     * @param sessionSecret of authorization session
     * @param errorMessage of authorization session
     *
     * @return returnUrl where should be redirected user
     */
    String authorizationOAuthError(String sessionSecret, String errorMessage);

    /**
     * Notify about payment's oAuth confirmation success
     *
     * @param paymentId of confirmed Payment
     * @param extra data from Payment
     *
     * @return returnUrl where should be redirected user
     */
    String paymentConfirmationOAuthSuccess(String paymentId, Map<String, String> extra);
}
