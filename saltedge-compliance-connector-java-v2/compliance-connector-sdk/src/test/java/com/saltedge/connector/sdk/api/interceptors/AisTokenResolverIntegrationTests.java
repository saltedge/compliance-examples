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
package com.saltedge.connector.sdk.api.interceptors;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.TestTools;
import com.saltedge.connector.sdk.api.controllers.ais.AccountsV2Controller;
import com.saltedge.connector.sdk.api.models.requests.DefaultRequest;
import com.saltedge.connector.sdk.api.models.responses.ErrorResponse;
import com.saltedge.connector.sdk.config.ApplicationProperties;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.models.domain.AisTokensRepository;
import com.saltedge.connector.sdk.tools.JsonTools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AisTokenResolverIntegrationTests {
    @LocalServerPort
    protected int port = 0;

    @Autowired
    ApplicationProperties applicationProperties;
    @Autowired
    AisTokensRepository aisTokensRepository;
    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Test
    public void givenHeaderWithNoAccessToken_whenMakeRequest_thenReturnAccessTokenMissing() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().errorClass).isEqualTo("AccessTokenMissing");
        assertThat(response.getBody().errorMessage).isEqualTo("Access Token is missing.");
    }

    @Test
    public void givenHeaderWithInvalidAccessToken_whenMakeRequest_thenReturnTokenNotFoundError() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(SDKConstants.HEADER_ACCESS_TOKEN, "invalidToken");

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().errorClass).isEqualTo("TokenNotFound");
    }

    @Test
    public void givenHeaderWithExpiredToken_whenMakeRequest_thenReturnTokenExpiredError() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(SDKConstants.HEADER_ACCESS_TOKEN, "validToken");

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        ErrorResponse body = response.getBody();
        assertThat(body.errorClass).isEqualTo("TokenExpired");
    }

    @BeforeEach
    public void setUp() {
        if (aisTokensRepository.count() == 0) {
            AisToken token = new AisToken("sessionSecret", "tppAppName", "authTypeCode", "tppRedirectUrl", Instant.now().minusSeconds(1), null);
            token.id = 1L;
            token.accessToken = "validToken";
            aisTokensRepository.save(token);
        }
    }

    private LinkedMultiValueMap<String, String> createHeaders() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(SDKConstants.HEADER_CLIENT_ID, "clientId");
        String auth = JsonTools.createAuthorizationHeaderValue(new DefaultRequest(), TestTools.getInstance().getRsaPrivateKey());
        headers.add(SDKConstants.HEADER_AUTHORIZATION, auth);
        return headers;
    }

    private ResponseEntity<ErrorResponse> doRequest(LinkedMultiValueMap<String, String> headers) {
        return testRestTemplate.exchange(
                createURLWithPort(AccountsV2Controller.BASE_PATH),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ErrorResponse.class
        );
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
