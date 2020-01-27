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
package com.saltedge.connector.example.connector.config;

import com.saltedge.connector.example.connector.ConnectorTypeConverters;
import com.saltedge.connector.sdk.provider.models.InputField;
import com.saltedge.connector.sdk.provider.models.PaymentTemplate;

public class PaymentTemplates {
    public final static String KEY_AMOUNT = "amount";
    public final static String KEY_DESCRIPTION = "description";
    public final static String TYPE_INTERNAL_TRANSFER = "internal_transfer";
    public final static String TYPE_SWIFT = "swift";
    public final static String TYPE_SEPA = "sepa";

    public static PaymentTemplate INTERNAL_TRANSFER = new PaymentTemplate(
            TYPE_INTERNAL_TRANSFER,
            "Internal Transfer",
            ConnectorTypeConverters.convertCodesToInputFields(new String[]{"from_account"}),
            ConnectorTypeConverters.convertCodesToInputFields(new String[]{"to_account"})
    );

    public static PaymentTemplate SWIFT = new PaymentTemplate(
            TYPE_SWIFT,
            "swift",
            ConnectorTypeConverters.convertCodesToInputFields(new String[]{
                    "from_account_number",
                    "from_swift_code"
            }),
            ConnectorTypeConverters.convertCodesToInputFields(new String[]{
                    "to_account_number",
                    "to_swift_code"
            })
    );

    public static PaymentTemplate SEPA = new PaymentTemplate(
            TYPE_SEPA,
            "sepa",
            ConnectorTypeConverters.convertCodesToInputFields(new String[]{"from_iban"}),
            ConnectorTypeConverters.convertCodesToInputFields(new String[]{"to_iban"}),
            new InputField("sms", "sms"),
            "input sms code"
    );
}
