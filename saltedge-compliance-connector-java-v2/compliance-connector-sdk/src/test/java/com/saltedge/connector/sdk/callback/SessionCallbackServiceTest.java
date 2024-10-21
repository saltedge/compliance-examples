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
package com.saltedge.connector.sdk.callback;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.api.models.responses.ErrorResponse;
import com.saltedge.connector.sdk.callback.mapping.BaseCallbackRequest;
import com.saltedge.connector.sdk.callback.mapping.BaseFailRequest;
import com.saltedge.connector.sdk.callback.mapping.SessionSuccessCallbackRequest;
import com.saltedge.connector.sdk.config.ApplicationProperties;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class SessionCallbackServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Autowired
    ApplicationProperties applicationProperties;
    @InjectMocks
    private SessionsCallbackService service = new SessionsCallbackService();

    @BeforeEach
    public void setUp() throws Exception {
        service.applicationProperties = applicationProperties;
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .willReturn(new ResponseEntity<>("{}", HttpStatus.OK));
    }

    @Test
    public void givenMockingRestTemplate_whenSendUpdateCallback_thenCallExchangeWithParams() {
        // when
        service.sendUpdateCallback("sessionSecret", new BaseCallbackRequest());

        // then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), entityCaptor.capture(), eq(Object.class));

        assertThat(urlCaptor.getValue()).isEqualTo("http://localhost/api/connectors/v2/sessions/sessionSecret/update");
        assertThat(entityCaptor.getValue().getHeaders().get("X-HTTP-Method-Override")).isEqualTo(Lists.list("PATCH"));
        assertThat(entityCaptor.getValue().getHeaders().getAccept()).isEqualTo(Lists.list(MediaType.APPLICATION_JSON));
        assertThat(entityCaptor.getValue().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(entityCaptor.getValue().getHeaders().get("App-id")).isEqualTo(Lists.list("QWERTY"));
        assertThat(entityCaptor.getValue().getHeaders().get("App-secret")).isEqualTo(Lists.list("ASDFG"));
        assertThat(entityCaptor.getValue().getHeaders().get(SDKConstants.HEADER_AUTHORIZATION).get(0)).startsWith("Bearer ");
    }

    @Test
    public void givenMockingRestTemplate_whenSendSuccessCallback_thenCallExchangeWithParams() throws InterruptedException {
        // when
        SessionSuccessCallbackRequest request = new SessionSuccessCallbackRequest();
        request.token = "accessToken";
        service.sendSuccessCallback("sessionSecret", request);

        // then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), entityCaptor.capture(), eq(Object.class));
        assertThat(urlCaptor.getValue()).isEqualTo("http://localhost/api/connectors/v2/sessions/sessionSecret/success");
        assertThat(entityCaptor.getValue().getHeaders().get("App-id")).isEqualTo(Lists.list("QWERTY"));
        assertThat(entityCaptor.getValue().getHeaders().get("App-secret")).isEqualTo(Lists.list("ASDFG"));
        assertThat(entityCaptor.getValue().getHeaders().get(SDKConstants.HEADER_AUTHORIZATION).get(0)).startsWith("Bearer ");
    }

    @Test
    public void givenRestException_whenSendSuccessCallback_thenReturnError() throws ExecutionException, InterruptedException {
        //given
        String response = "{\"error_class\":\"SessionExpired\",\"error_message\":\"Session with secret: '37180f0a' has expired.\"}";
        HttpClientErrorException exception = HttpClientErrorException.Unauthorized.create(
                HttpStatus.UNAUTHORIZED,
                "SessionExpired",
                null,
                response.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class))).willThrow(exception);

        // when
        SessionSuccessCallbackRequest request = new SessionSuccessCallbackRequest();
        request.token = "accessToken";
        CompletableFuture<ErrorResponse> result = service.sendSuccessCallback("sessionSecret", request);
        assertThat(result.get().errorClass).isEqualTo("Unauthorized");

        // then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), entityCaptor.capture(), eq(Object.class));
        assertThat(urlCaptor.getValue()).isEqualTo("http://localhost/api/connectors/v2/sessions/sessionSecret/success");
        assertThat(entityCaptor.getValue().getHeaders().get("App-id")).isEqualTo(Lists.list("QWERTY"));
        assertThat(entityCaptor.getValue().getHeaders().get("App-secret")).isEqualTo(Lists.list("ASDFG"));
        assertThat(entityCaptor.getValue().getHeaders().get(SDKConstants.HEADER_AUTHORIZATION).get(0)).startsWith("Bearer ");
    }

    @Test
    public void givenMockingRestTemplate_whenSendFailCallback_thenCallExchangeWithParams() {
        // when
        service.sendFailCallback("sessionSecret", new BaseFailRequest());

        // then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), entityCaptor.capture(), eq(Object.class));
        assertThat(urlCaptor.getValue()).isEqualTo("http://localhost/api/connectors/v2/sessions/sessionSecret/fail");
        assertThat(entityCaptor.getValue().getHeaders().get("App-id")).isEqualTo(Lists.list("QWERTY"));
        assertThat(entityCaptor.getValue().getHeaders().get("App-secret")).isEqualTo(Lists.list("ASDFG"));
        assertThat(entityCaptor.getValue().getHeaders().get(SDKConstants.HEADER_AUTHORIZATION).get(0)).startsWith("Bearer ");
    }

    @Test
    public void givenMockingRestTemplate_whenSendFailCallbackWithException_thenCallExchangeWithParams() {
        // when
        service.sendFailCallback("sessionSecret", new NotFound.AccountNotFound());

        // then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), entityCaptor.capture(), eq(Object.class));
        assertThat(urlCaptor.getValue()).isEqualTo("http://localhost/api/connectors/v2/sessions/sessionSecret/fail");
        assertThat(entityCaptor.getValue().getHeaders().get("App-id")).isEqualTo(Lists.list("QWERTY"));
        assertThat(entityCaptor.getValue().getHeaders().get("App-secret")).isEqualTo(Lists.list("ASDFG"));
        assertThat(entityCaptor.getValue().getHeaders().get(SDKConstants.HEADER_AUTHORIZATION).get(0)).startsWith("Bearer ");
    }
}
