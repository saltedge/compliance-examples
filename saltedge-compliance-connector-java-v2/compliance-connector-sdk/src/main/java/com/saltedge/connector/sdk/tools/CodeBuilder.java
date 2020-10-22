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
package com.saltedge.connector.sdk.tools;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class CodeBuilder {
    public static final String DEFAULT_SALT = CodeBuilder.generateRandomString(16);

    public static String generateRandomString() {
        return generateRandomString(32);
    }

    public static String generateRandomString(Integer size) {
        int arraySize = (size == null) ? 32 : size;
        byte[] array = new byte[arraySize];
        new Random().nextBytes(array);
        return new String(Base64.getUrlEncoder().encode(array)).substring(0, arraySize);
    }

    public static String generatePaymentAuthorizationCode(
            String payeeDetails,
            String amount,
            Long createdAt,
            String userId,
            String description
    ) {
        String templateString = payeeDetails + "|" + amount + "|" + createdAt + "|" + userId + "|" + description + "|" + DEFAULT_SALT;
        byte[] hashBytes = templateString.getBytes(StandardCharsets.UTF_8);
        try {
            hashBytes = MessageDigest.getInstance("SHA-256").digest(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().withoutPadding().encodeToString(hashBytes);
    }

    public static String generateAuthorizationCode(String userId, String title, String description, Long timeStamp) {
        String templateString = title + "|" + timeStamp + "|" + userId + "|" + description + "|" + DEFAULT_SALT;
        byte[] hashBytes = templateString.getBytes(StandardCharsets.UTF_8);
        try {
            hashBytes = MessageDigest.getInstance("SHA-256").digest(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(hashBytes);
    }
}
