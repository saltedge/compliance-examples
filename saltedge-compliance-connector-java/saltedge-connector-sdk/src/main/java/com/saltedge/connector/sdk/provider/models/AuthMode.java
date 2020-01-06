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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Salt Edge Compliance solution supports next Authorization modes.
 *
 * Oauth: For the OAuth flow authentication is made via redirect on ASPSP side, and PSU redirected back to the app.
 * Embedded: In Embedded flow login widget is served from the same app without redirecting the user to another domain.
 *           The credentials are then sent to the authentication provider for authentication.
 */
public enum AuthMode {
    OAUTH, EMBEDDED;

    private static Logger log = LoggerFactory.getLogger(AuthMode.class);

    public static AuthMode stringToAuthType(String authType) {
        try {
            return AuthMode.valueOf(authType.toUpperCase());
        } catch (Exception e) {
            log.error("stringToAuthType(" + authType + ") exception", e);
        }
        return null;
    }
}
