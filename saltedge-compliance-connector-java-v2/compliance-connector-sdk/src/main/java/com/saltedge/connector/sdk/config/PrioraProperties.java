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
package com.saltedge.connector.sdk.config;

import com.saltedge.connector.sdk.tools.KeyTools;
import com.saltedge.connector.sdk.tools.ResourceTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;

/**
 * Priora object properties from application.yml
 *
 * Example of application.yml
 * connector:
 *   private_key_name: connector_private_prod.pem
 *   priora:
 *     app_code: spring_connector_example
 *     app_id: xxxxxxxxx
 *     app_secret: xxxxxxxxx
 *     base_url: https://priora.saltedge.com/
 *     public_key_name: priora_public_prod.pem
 *     public_key_pem: -----BEGIN PUBLIC KEY-----\nXXXXX\n-----END PUBLIC KEY-----
 *     public_key_file_path: /Users/bank/priora_public_prod.pem
 */
@Configuration
public class PrioraProperties {
    private static final Logger log = LoggerFactory.getLogger(PrioraProperties.class);
    /**
     * Registered Connector code
     * (https://priora.saltedge.com/providers/settings#details)
     */
    @NotBlank
    private String appCode;

    /**
     * Unique Connector's App ID
     * (https://priora.saltedge.com/providers/settings#details)
     */
    @NotBlank
    private String appId;

    /**
     * Unique Connector's App Secret
     * (https://priora.saltedge.com/providers/settings#details)
     */
    @NotBlank
    private String appSecret;

    /**
     * Salt Edge Compliance base url.
     * By default: `https://priora.saltedge.com/`
     */
    @NotBlank
    private String baseUrl = "https://priora.saltedge.com/";

    /**
     * Path in application resources of public key file of Salt Edge Compliance Service (PEM format).
     * Use for JWT signature
     */
    private String publicKeyName;

    /**
     * Public key string of Salt Edge Compliance Service (PEM format).
     * Use for JWT signature
     */
    private String publicKeyPem;

    /**
     * Path in file system of public key file of Salt Edge Compliance Service (PEM format).
     * Use for JWT signature
     */
    private String publicKeyFilePath;

    private PublicKey publicKey;

    public URL getPrioraBaseUrl() {
        try {
            return new URL(baseUrl);
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public PublicKey getPrioraPublicKey() {
        if (publicKey == null) {
            if (StringUtils.hasText(publicKeyName)) {
                publicKey = KeyTools.convertPemStringToPublicKey(ResourceTools.readResourceFile(publicKeyName));
            } else if (StringUtils.hasText(publicKeyPem)) {
                publicKey = KeyTools.convertPemStringToPublicKey(publicKeyPem);
            } else if (StringUtils.hasText(publicKeyFilePath)) {
                publicKey = KeyTools.convertPemStringToPublicKey(ResourceTools.readFile(publicKeyFilePath));
            }
        }
        return publicKey;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPublicKeyName() {
        return publicKeyName;
    }

    public void setPublicKeyName(String publicKeyName) {
        this.publicKeyName = publicKeyName;
    }

    public String getPublicKeyFilePath() {
        return publicKeyFilePath;
    }

    public void setPublicKeyFilePath(String publicKeyFilePath) {
        this.publicKeyFilePath = publicKeyFilePath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKeyPem() {
        return publicKeyPem;
    }

    public void setPublicKeyPem(String publicKeyPem) {
        this.publicKeyPem = publicKeyPem;
    }
}
