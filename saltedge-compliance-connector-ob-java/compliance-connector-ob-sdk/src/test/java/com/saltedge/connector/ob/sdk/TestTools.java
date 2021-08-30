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
package com.saltedge.connector.ob.sdk;

import com.saltedge.connector.ob.sdk.tools.JsonTools;
import com.saltedge.connector.ob.sdk.tools.KeyTools;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

public class TestTools {
    private static final TestTools instance = new TestTools();
    private String rsaPublicKeyString = null;
    private PublicKey rsaPublicKey = null;
    private String rsaPrivateKeyString = null;
    private PrivateKey rsaPrivateKey = null;

    private TestTools() {
        try {
            String publicKeyFileName = "test_public_key.pem";
            rsaPublicKeyString = readTestKeyFile(publicKeyFileName);
            rsaPublicKey = KeyTools.convertPemStringToPublicKey(rsaPublicKeyString);
            String privateKeyFileName = "test_private_key.pem";
            rsaPrivateKeyString = readTestKeyFile(privateKeyFileName);
            rsaPrivateKey = KeyTools.convertPemStringToPrivateKey(rsaPrivateKeyString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static TestTools getInstance(){
        return instance;
    }

    public String getRsaPublicKeyString() {
        return rsaPublicKeyString;
    }

    public PublicKey getRsaPublicKey() {
        return rsaPublicKey;
    }

    public String getRsaPrivateKeyString() {
        return rsaPrivateKeyString;
    }

    public PrivateKey getRsaPrivateKey() {
        return rsaPrivateKey;
    }

    public PrivateKey getAlternateRsaPrivateKey() {
        try {
            return KeyTools.convertPemStringToPrivateKey(readTestKeyFile("test_private_alt_key.pem"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String createAuthorizationHeaderValue(Object requestData, PrivateKey key) {
        return createAuthorizationHeaderValue(requestData, key, Instant.now().plus(5, ChronoUnit.MINUTES));
    }

    public static String createAuthorizationHeaderValue(Object requestData, PrivateKey key, Instant expirationTime) {
        return "Bearer " + Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(JsonTools.createDefaultMapper()))
                .claim(SDKConstants.KEY_DATA, requestData)
                .signWith(key)
                .setExpiration(Date.from(expirationTime))
                .compact();
    }

    private String readTestKeyFile(String filename) throws FileNotFoundException {
        StringBuilder result = new StringBuilder("");
        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).getFile());

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }
}
