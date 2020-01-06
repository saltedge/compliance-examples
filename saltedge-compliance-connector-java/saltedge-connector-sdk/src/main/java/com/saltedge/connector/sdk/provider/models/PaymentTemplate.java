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
package com.saltedge.connector.sdk.provider.models;

/**
 * Unique Payment Template registered in `Dashboard/Payment Templates` for Customer.
 * (https://priora.saltedge.com/providers/templates)
 */
public class PaymentTemplate {
    /**
     * registered unique identification code (e.g. `swift`)
     */
    public String code;

    /**
     * registered human readable name (e.g. `Swift payment`)
     */
    public String name;

    /**
     * collection of debtor input data
     * @see InputField
     */
    public InputField[] debtorFields;

    /**
     * collection of creditor input data
     * @see InputField
     */
    public InputField[] creditorFields;

    /**
     * input field for Payment confirmation
     * @see InputField
     */
    public InputField interactiveField;

    /**
     * human readable instructions for Payment confirmation
     * @see InputField
     */
    public String interactiveInstruction;

    public PaymentTemplate(String code, String name, InputField[] debtorFields, InputField[] creditorFields) {
        this.code = code;
        this.name = name;
        this.debtorFields = debtorFields;
        this.creditorFields = creditorFields;
    }

    public PaymentTemplate(String code, String name, InputField[] debtorFields, InputField[] creditorFields,
                           InputField interactiveField, String interactiveInstruction) {
        this(code, name, debtorFields, creditorFields);
        this.interactiveField = interactiveField;
        this.interactiveInstruction = interactiveInstruction;
    }

    /**
     * Check if Payment Template is Interactive (has MFA input fields)
     *
     * @return true if interactiveField exist
     */
    public boolean isInteractive() {
        return interactiveField != null;
    }
}
