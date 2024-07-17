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
package com.saltedge.connector.sdk.api.models;

import javax.validation.constraints.NotBlank;

/**
 * Unique Authorization Type which corresponds to data registered in `Dashboard/Settings/Authorization types` for Customer.
 * (<a href="https://priora.saltedge.com/providers/settings#authorization_types">...</a>)
 */
public class AuthorizationType {
    /**
     * registered authorization mode: OAUTH or EMBEDDED
     * @see AuthMode
     */
    @NotBlank
    public AuthMode mode;

    /**
     * registered unique identification code (e.g. `login_password`)
     */
    @NotBlank
    public String code;

    /**
     * registered human readable name of authorization type (e.g. `Login and Password`)
     */
    @NotBlank
    public String name;

    /**
     * collection of primary credentials input fields
     * @see InputField
     */
    public InputField[] primaryAuthFields;

    /**
     * collection of MFA credentials input fields
     * @see InputField
     */
    public InputField[] interactiveFields;

    public AuthorizationType(
            @NotBlank AuthMode mode,
            @NotBlank String code,
            @NotBlank String name
    ) {
        this.mode = mode;
        this.code = code;
        this.name = name;
    }

    public AuthorizationType(
            @NotBlank AuthMode mode,
            @NotBlank String code,
            @NotBlank String name,
            InputField[] primaryAuthFields,
            InputField[] interactiveAuthFields
    ) {
        this.mode = mode;
        this.code = code;
        this.name = name;
        this.primaryAuthFields = primaryAuthFields;
        this.interactiveFields = interactiveAuthFields;
    }

    /**
     * Check if Authorization Type is Interactive (has MFA input fields)
     *
     * @return true if interactiveFields list is not empty
     */
    public boolean isInteractive() {
        return interactiveFields != null && interactiveFields.length > 0;
    }

    /**
     * Returns code of the first interactive input field
     *
     * @return code string or null if no interactive fields
     */
    public String getInteractiveFieldCode() {
        return isInteractive() ? interactiveFields[0].code : null;
    }
}
