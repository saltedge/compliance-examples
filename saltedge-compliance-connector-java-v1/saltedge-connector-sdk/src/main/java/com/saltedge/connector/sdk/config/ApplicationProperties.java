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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Configuration properties from application.yml
 *
 * Example of application.yml
 * connector:
 *   private_key_name: connector_private_prod.pem
 *   public_key_name: connector_public_prod.pem
 *   connection_expires_in_minutes: 5
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
@ConfigurationProperties("connector")
public class ApplicationProperties {
    /**
     * Name of Connector's public key file in PEM format
     */
    @NotBlank
    private String publicKeyName;

    /**
     * Name of Connector's private key file in PEM format
     */
    @NotBlank
    private String privateKeyName;

    /**
     * Salt Edge Compliance related params
     * @see PrioraProperties
     */
    @NotNull
    private PrioraProperties priora;

    /**
     * Expiration period of Access Token for Salt Edge Compliance Solution.
     */
    @NotNull
    public static Integer connectionExpiresInMinutes = 24 * 60;

    private PublicKey publicKey;
    private PrivateKey privateKey;


    public String getPrioraAppCode() {
        return priora.getAppCode();
    }

    public String getPrioraAppId() {
        return priora.getAppId();
    }

    public String getPrioraAppSecret() {
        return priora.getAppSecret();
    }

    public PrioraProperties getPriora() {
        return priora;
    }

    public PublicKey getPublicKey() {
        if (publicKey == null) {
            publicKey = KeyTools.convertPemStringToPublicKey(ResourceTools.readKeyFile(publicKeyName));
        }
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        if (privateKey == null) {
            privateKey = KeyTools.convertPemStringToPrivateKey(ResourceTools.readKeyFile(privateKeyName));
        }
        return privateKey;
    }

    public String getPublicKeyName() {
        return publicKeyName;
    }

    public void setPublicKeyName(String publicKeyName) {
        this.publicKeyName = publicKeyName;
    }

    public String getPrivateKeyName() {
        return privateKeyName;
    }

    public void setPrivateKeyName(String privateKeyName) {
        this.privateKeyName = privateKeyName;
    }

    public void setPriora(PrioraProperties priora) {
        this.priora = priora;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPrioraPublicKey() {
        return getPriora().getPrioraPublicKey();
    }
}
