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
package com.saltedge.connector.sdk;

public class SDKConstants {
    public final static String CONNECTOR_PACKAGE = "com.saltedge.connector.sdk";
    public final static String API_BASE_PATH = "/api/priora/v2";
    public final static String CALLBACK_BASE_PATH = "/api/connectors/v2";

    public final static String HEADER_AUTHORIZATION = "authorization";
    public final static String HEADER_CLIENT_ID = "client-id";
    public final static String HEADER_ACCESS_TOKEN = "access-token";

    public final static String STATUS_WAITING_CONFIRMATION_CODE = "waiting_confirmation_code";
    public final static String STATUS_REDIRECT = "redirect";

    public final static int CONSENT_MAX_PERIOD = 1;

    public final static String KEY_DATA = "data";
    public final static String KEY_META = "meta";
    public final static String KEY_ID = "id";
    public final static String KEY_USER_ID = "user_id";
    public final static String KEY_EXTRA = "extra";
    public final static String KEY_REDIRECT_URL = "redirect_url";
    public final static String KEY_RETURN_TO_URL = "return_to_url";
    public final static String KEY_ERROR_MESSAGE = "error_message";
    public final static String KEY_ERROR_CLASS = "error_class";
    public final static String KEY_CONFIRMATION_CODE = "confirmation_code";
    public final static String KEY_PROVIDER_CODE = "provider_code";
    public final static String KEY_STATUS = "status";
    public final static String KEY_CURRENCY = "currency";
    public final static String KEY_CURRENCY_CODE = "currency_code";
    public final static String KEY_ACCOUNT = "account";
    public final static String KEY_AMOUNT = "amount";
    public final static String KEY_INSTRUCTED_AMOUNT = "instructed_amount";
    public final static String KEY_ACCOUNT_ID = "account_id";
    public final static String KEY_IBAN = "iban";
    public final static String KEY_BBAN = "bban";
    public final static String KEY_BIC = "bic";
    public final static String KEY_SORT_CODE = "sort_code";
    public final static String KEY_MSISDN = "msisdn";
    public final static String KEY_MASKED_PAN = "masked_pan";
    public final static String KEY_NAME = "name";
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_SESSION_SECRET = "session_secret";
    public final static String KEY_PAYMENT_ID = "payment_id";
    public final static String KEY_END_TO_END_IDENTIFICATION = "end_to_end_identification";
    public final static String KEY_ACCESS = "access";
    public final static String KEY_BALANCES = "balances";
    public final static String KEY_PRODUCT = "product";
    public final static String KEY_APP_NAME = "app_name";
}
