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
package com.saltedge.connector.sdk.api.controllers;

import com.saltedge.connector.sdk.TestTools;
import com.saltedge.connector.sdk.api.mapping.DefaultRequest;
import com.saltedge.connector.sdk.api.mapping.ErrorResponse;
import com.saltedge.connector.sdk.config.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KycV1ControllerIntegrationTests extends ControllerIntegrationTests {
    @Before
    public void setUp() {
        seedTokensRepository();
    }

    @Test
    public void givenHeaderWithInvalidClientId_whenMakeRequest_thenReturnInvalidClientIdError() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        headers.set(Constants.HEADER_CLIENT_ID, "");
        String auth = TestTools.createAuthorizationHeaderValue(new DefaultRequest(),
                TestTools.getInstance().getAlternateRsaPrivateKey());
        headers.add(Constants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getErrorClass()).isEqualTo("InvalidClientId");
    }

    @Test
    public void givenHeaderWithClientId_whenMakeRequest_thenReturnOK() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        String auth = TestTools.createAuthorizationHeaderValue(new DefaultRequest(),
                TestTools.getInstance().getRsaPrivateKey());
        headers.add(Constants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenHeaderWithInvalidAuthorization_whenMakeRequest_thenReturnJWTDecodeError() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        String auth = TestTools.createAuthorizationHeaderValue(new DefaultRequest(),
                TestTools.getInstance().getAlternateRsaPrivateKey());
        headers.add(Constants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorClass()).isEqualTo("JWTDecodeError");
    }

    @Test
    public void givenHeaderWithExpiredAuthorization_whenMakeRequest_thenReturnJWTExpiredSignature() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        String auth = TestTools.createAuthorizationHeaderValue(new DefaultRequest(),
                TestTools.getInstance().getRsaPrivateKey(), LocalDateTime.now().minusSeconds(1));

        headers.add(Constants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorClass()).isEqualTo("JWTExpiredSignature");
    }

    @Test
    public void givenHeaderWithInvalidAuthorizationPayload_whenMakeRequest_thenReturnJWTDecodeError() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        String auth = TestTools.createAuthorizationHeaderValue("test string",
                TestTools.getInstance().getRsaPrivateKey());
        headers.add(Constants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorClass()).isEqualTo("JWTDecodeError");
    }

    @Test
    public void givenHeaderWithValidAuthorization_whenMakeRequest_thenReturnOK() {
        // given
        LinkedMultiValueMap<String, String> headers = createHeaders();
        String auth = TestTools.createAuthorizationHeaderValue(new DefaultRequest(),
                TestTools.getInstance().getRsaPrivateKey());
        headers.add(Constants.HEADER_AUTHORIZATION, auth);

        // when
        ResponseEntity<ErrorResponse> response = doRequest(headers);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<ErrorResponse> doRequest(LinkedMultiValueMap<String, String> headers) {
        return testRestTemplate.exchange(
                createURLWithPort(KycV1Controller.BASE_PATH), HttpMethod.GET, new HttpEntity<>(headers), ErrorResponse.class
        );
    }
}
