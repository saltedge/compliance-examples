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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Configuration properties from application.yml
 * Example of application.yml
 * spring:
 *   profiles: dev
 * connector:
 *   private_key_name: connector_private_prod.pem
 *   private_key_pem: -----BEGIN PRIVATE KEY-----\nXXXXX\n-----END PRIVATE KEY-----
 *   private_key_file_path: /Users/bank/connector_private_prod.pem
 *   priora:
 *     app_code: spring_connector_example
 *     app_id: xxxxxxxxx
 *     app_secret: xxxxxxxxx
 *     base_url: https://priora.saltedge.com/
 *     public_key_name: priora_public_prod.pem
 */
@Configuration
@ConfigurationProperties(prefix = "connector")
public class ApplicationProperties {
    /**
     * Path in application resources of private key file of Connector/ASPSP application (PEM format)
     * Use for JWT signature
     */
    private String privateKeyName;

    /**
     * Private key string of Connector/ASPSP application (PEM format)
     * Use for JWT signature
     */
    private String privateKeyPem;

    /**
     * Path in file system of private key file of Connector/ASPSP application (PEM format)
     * Use for JWT signature
     */
    private String privateKeyFilePath;

    /**
     * Salt Edge Compliance related params
     * @see PrioraProperties
     */
    @NotNull
    private PrioraProperties priora;

    private PrivateKey privateKey;

    //Getters and Setters

    public String getPrivateKeyName() {
        return privateKeyName;
    }

    public void setPrivateKeyName(String privateKeyName) {
        this.privateKeyName = privateKeyName;
    }

    public String getPrivateKeyPem() {
        return privateKeyPem;
    }

    public void setPrivateKeyPem(String privateKeyPem) {
        this.privateKeyPem = privateKeyPem;
    }

    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public void setPrivateKeyFilePath(String privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }

    public PrivateKey getPrivateKey() {
        if (privateKey == null) {
            if (StringUtils.hasText(privateKeyName)) {
                privateKey = KeyTools.convertPemStringToPrivateKey(ResourceTools.readResourceFile(privateKeyName));
            } else if (StringUtils.hasText(privateKeyPem)) {
                privateKey = KeyTools.convertPemStringToPrivateKey(privateKeyPem);
            } else if (StringUtils.hasText(privateKeyFilePath)) {
                privateKey = KeyTools.convertPemStringToPrivateKey(ResourceTools.readFile(privateKeyFilePath));
            }
        }
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PrioraProperties getPriora() {
        return priora;
    }

    public void setPriora(PrioraProperties priora) {
        this.priora = priora;
    }

    public String getPrioraAppCode() {
        return priora.getAppCode();
    }

    public String getPrioraAppId() {
        return priora.getAppId();
    }

    public String getPrioraAppSecret() {
        return priora.getAppSecret();
    }

    public PublicKey getPrioraPublicKey() {
        return priora.getPrioraPublicKey();
    }

}
