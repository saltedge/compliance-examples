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
package com.saltedge.connector.ob.sdk.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltedge.connector.ob.sdk.config.ApplicationProperties;
import com.saltedge.connector.ob.sdk.config.PrioraProperties;
import com.saltedge.connector.ob.sdk.tools.JsonTools;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivateKey;
import java.util.Objects;

/**
 * Base rest client for Salt Edge Compliance callbacks
 */
public abstract class CallbackRestClient {
    @Autowired
    public ApplicationProperties applicationProperties;

//    @Qualifier("saltEdgeCallbackRestTemplateBean")
    @Autowired
    public RestTemplate callbackRestTemplate;

    protected ObjectMapper mapper = JsonTools.createDefaultMapper();

    protected abstract Logger getLogger();

    public String createCallbackRequestUrl(String path) {
        try {
            PrioraProperties priora = applicationProperties.getPriora();
            URL baseUrl = priora.getPrioraBaseUrl();
            return new URL(baseUrl, path).toString();
        } catch (MalformedURLException e) {
            getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    public LinkedMultiValueMap<String, String> createCallbackRequestHeaders() {
        LinkedMultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        headersMap.add(HttpHeaders.ACCEPT, "application/json");
        headersMap.add(HttpHeaders.CONTENT_TYPE, "application/jwt");
        headersMap.add("App-Id", applicationProperties.getPrioraAppId());
        headersMap.add("App-Secret", applicationProperties.getPrioraAppSecret());
        return headersMap;
    }

    public String createJwtPayload(Object requestData) {
        PrivateKey privateKey = applicationProperties.getPrivateKey();
        if (requestData != null && privateKey != null) {
            return JsonTools.createAuthorizationPayloadValue(requestData, privateKey);
        } else if (privateKey == null) {
            getLogger().error("Can not create Authorization header: no private key");
            return "";
        } else {
            return "";
        }
    }

    public <T> Response<T> doCallbackRequest(String url, HttpMethod method, LinkedMultiValueMap<String, String> headers, String payload, Class<T> clazz) {
        try {
            if (method == HttpMethod.PATCH) {
                headers.add("X-HTTP-Method-Override", "PATCH");
                method = HttpMethod.POST;
            }
            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            ResponseEntity<T> response = callbackRestTemplate.exchange(url, method, request, clazz);
            getLogger().info("doCallbackRequest: response status:" + response.getStatusCodeValue() + ", body: " + Objects.requireNonNull(response.getBody()));
            return new Response<T>(response.getBody());
        } catch (HttpClientErrorException e) {
            getLogger().error("HttpClientErrorException:", e);
            if (e.getStatusCode().is4xxClientError()) {
                return new Response<T>(e.getResponseBodyAsString());
            }
        } catch (HttpServerErrorException e) {
            getLogger().error("HttpServerErrorException:", e);
        } catch (UnknownHttpStatusCodeException e) {
            getLogger().error("UnknownHttpStatusCodeException:", e);
        } catch (Exception e) {
            getLogger().error("Exception:", e);
        }
        return new Response<T>("");
    }

    public void printPayload(String url, HttpMethod method, LinkedMultiValueMap<String, String> headers, Object params, String payload) {
        try {
            getLogger().info("CallbackRequest:"
              + "\nURL: " + url
              + "\nMethod: " + method.toString()
              + "\nHeaders: " + mapper.writeValueAsString(headers)
              + "\nUnpacked params: " + ((params == null) ? "null" : mapper.writeValueAsString(params))
              + "\nJWT payload: " + payload
              + "\n"
            );
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

//    @Bean
//    @Qualifier("saltEdgeCallbackRestTemplateBean")
//    public RestTemplate createRestTemplate() {
//        return new RestTemplate();
//    }

    public static class Response<T> {
        public T success;
        public String error;

        public Response(T success) {
            this.success = success;
            this.error = null;
        }

        public Response(String error) {
            this.success = null;
            this.error = error;
        }

        public Response(T success, String error) {
            this.success = success;
            this.error = error;
        }
    }
}
