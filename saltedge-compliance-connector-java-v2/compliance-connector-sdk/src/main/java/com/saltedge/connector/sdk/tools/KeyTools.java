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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotNull;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Set of key tools
 */
public class KeyTools {
    private static final Logger log = LoggerFactory.getLogger(KeyTools.class);

    /**
     * Generates random base64 encoded string with desired length
     *
     * @param tokenSize length of target string
     * @return random string
     */
    public static String generateToken(int tokenSize) {
        SecureRandom random = new SecureRandom();
        byte[] values = new byte[tokenSize];
        random.nextBytes(values);
        return Base64.getUrlEncoder().encodeToString(values);
    }

    /**
     * Converts string which contains public key in PEM format to PublicKey object
     *
     * @param pemString public key in PEM format
     * @return PublicKey or null
     */
    public static PublicKey convertPemStringToPublicKey(String pemString) {
        try {
            String keyContent = pemString
                    .replace("\\n", "")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "");
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getMimeDecoder().decode(keyContent));
            return kf.generatePublic(keySpecX509);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Converts string which contains private key (PKCS8) in PEM format to PrivateKey object
     *
     * @param pemString private key in PEM format
     * @return PrivateKey or null
     */
    public static PrivateKey convertPemStringToPrivateKey(@NotNull String pemString) {
        try {
            String keyContent = pemString.replace("\\n", "")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(keyContent));
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
