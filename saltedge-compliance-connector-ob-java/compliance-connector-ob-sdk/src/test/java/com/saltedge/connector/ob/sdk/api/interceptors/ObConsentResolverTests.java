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
package com.saltedge.connector.ob.sdk.api.interceptors;

import com.saltedge.connector.ob.sdk.TestTools;
import com.saltedge.connector.ob.sdk.api.ApiConstants;
import com.saltedge.connector.ob.sdk.api.controllers.ObAccountsController;
import com.saltedge.connector.ob.sdk.api.models.request.DefaultRequest;
import com.saltedge.connector.ob.sdk.api.models.response.ErrorResponse;
import com.saltedge.connector.ob.sdk.config.ApplicationProperties;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.model.jpa.ConsentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ObConsentResolverTests {
    @LocalServerPort
    private int port = 0;
    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private ConsentsRepository consentsRepository;
    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    @BeforeEach
    public void setUp() {
        consentsRepository.deleteAll();
        Consent consent = new Consent("tppAppName", "1", "AwaitingAuthorization");
        consent.accessToken = "validToken";
        consentsRepository.save(consent);
    }

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
    public void givenHeaderWithInvalidAccessToken_whenMakeRequest_thenReturnConsentNotFoundError() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(ApiConstants.HEADER_ACCESS_TOKEN, "invalidToken");

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().errorClass).isEqualTo("ConsentNotFound");
    }

    @Test
    public void givenHeaderWithUnauthorizedToken_whenMakeRequest_thenReturnConsentUnauthorizedError() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(ApiConstants.HEADER_ACCESS_TOKEN, "validToken");

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getBody().errorClass).isEqualTo("ConsentUnauthorized");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void givenHeaderWithExpiredToken_whenMakeRequest_thenReturnConsentExpiredError() {
        // given
        Consent consent = consentsRepository.findFirstByAccessToken("validToken");
        consent.status = "Authorised";
        consent.userId = "1";
        consent.permissions = Collections.emptyList();
        consent.permissionsExpiresAt = Instant.now().minusSeconds(9999);
        consentsRepository.save(consent);
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.add(ApiConstants.HEADER_ACCESS_TOKEN, "validToken");

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getBody().errorClass).isEqualTo("ConsentExpired");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private LinkedMultiValueMap<String, String> createHeaders() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(ApiConstants.HEADER_CLIENT_ID, "clientId");
        String auth = TestTools.createAuthorizationHeaderValue(new DefaultRequest(), TestTools.getInstance().getRsaPrivateKey());
        headers.add(ApiConstants.HEADER_AUTHORIZATION, auth);
        return headers;
    }

    private ResponseEntity<ErrorResponse> doRequest(LinkedMultiValueMap<String, String> headers) {
        return testRestTemplate.exchange(
                createURLWithPort(ObAccountsController.BASE_PATH),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ErrorResponse.class
        );
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
