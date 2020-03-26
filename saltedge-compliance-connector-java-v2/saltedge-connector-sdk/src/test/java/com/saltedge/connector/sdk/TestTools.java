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

import com.saltedge.connector.sdk.provider.ProviderServiceAbs;
import com.saltedge.connector.sdk.tools.KeyTools;
import io.jsonwebtoken.Jwts;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

import static org.mockito.BDDMockito.given;

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

    public static void initProviderApiMocks(ProviderServiceAbs providerService) {
        given(providerService.getAuthorizationTypeByCode("login_password_sms")).willReturn(AuthorizationTypes.LOGIN_PASSWORD_SMS_AUTH_TYPE);
        given(providerService.getAuthorizationTypeByCode("login_password")).willReturn(AuthorizationTypes.LOGIN_PASSWORD_AUTH_TYPE);
        given(providerService.getAuthorizationTypeByCode("oauth")).willReturn(AuthorizationTypes.OAUTH_AUTH_TYPE);
//        given(providerApi.getPaymentTemplateByCode("1", PaymentTemplates.TYPE_INTERNAL_TRANSFER)).willReturn(PaymentTemplates.INTERNAL_TRANSFER);
//        given(providerApi.getPaymentTemplateByCode("1", PaymentTemplates.TYPE_SWIFT)).willReturn(PaymentTemplates.SWIFT);
//        given(providerApi.getPaymentTemplateByCode("1", PaymentTemplates.TYPE_SEPA)).willReturn(PaymentTemplates.SEPA);
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
        return createAuthorizationHeaderValue(requestData, key, LocalDateTime.now().plusMinutes(5));
    }

    public static String createAuthorizationHeaderValue(Object requestData, PrivateKey key, LocalDateTime expirationTime) {
        return "Bearer " + Jwts.builder().claim(SDKConstants.KEY_DATA, requestData)
                .signWith(key)
                .setExpiration(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()))
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
