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

import com.saltedge.connector.ob.sdk.api.models.request.AuthorizationCreateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.AuthorizationUpdateRequest;
import com.saltedge.connector.ob.sdk.api.models.request.PaymentUpdateRequest;
import com.saltedge.connector.ob.sdk.api.models.response.AuthorizationsCreateResponse;
import com.saltedge.connector.ob.sdk.api.models.response.AuthorizationsUpdateResponse;
import com.saltedge.connector.ob.sdk.api.models.response.EmptyJsonResponse;
import com.saltedge.connector.ob.sdk.config.ApplicationProperties;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ConnectorCallbackServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Autowired
    ApplicationProperties applicationProperties;
    @InjectMocks
    private ConnectorCallbackService testService = new ConnectorCallbackService();

    @BeforeEach
    public void setUp() {
        testService.applicationProperties = applicationProperties;
    }

    @Test
    public void givenOkResponse_whenCreateAuthorization_shouldBeCalledExchangeWithParams() {
        //given
        AuthorizationsCreateResponse response = new AuthorizationsCreateResponse(new AuthorizationsCreateResponse.Data("1", "consentId", "accessToken"));
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AuthorizationsCreateResponse.class)))
            .willReturn(ResponseEntity.ok(response));

        //when
        AuthorizationsCreateResponse result = testService.createAuthorization(new AuthorizationCreateRequest("authorizeUrl", "authCode", null));

        //then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<String>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), entityCaptor.capture(), eq(AuthorizationsCreateResponse.class));

        assertThat(urlCaptor.getValue()).isEqualTo("http://localhost/api/connectors/ob/v1/authorizations");
        assertThat(entityCaptor.getValue().getHeaders().getAccept()).isEqualTo(Lists.list(MediaType.APPLICATION_JSON));
        assertThat(entityCaptor.getValue().getHeaders().getContentType().toString()).isEqualTo("application/jwt");
        assertThat(entityCaptor.getValue().getHeaders().get("App-Id")).isEqualTo(Lists.list("QWERTY"));
        assertThat(entityCaptor.getValue().getHeaders().get("App-Secret")).isEqualTo(Lists.list("ASDFG"));
        assertThat(entityCaptor.getValue().getBody()).isNotEmpty();
        assertThat(result).isEqualTo(response);
    }

    @Test
    public void given4xxResponse_whenCreateAuthorization_shouldBeCalledExchangeWithParams() {
        //given
        AuthorizationsCreateResponse response = new AuthorizationsCreateResponse(new AuthorizationsCreateResponse.Data("1", "consentId", "accessToken"));
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AuthorizationsCreateResponse.class)))
            .willReturn(ResponseEntity.badRequest().body(response));

        //when
        AuthorizationsCreateResponse result = testService.createAuthorization(new AuthorizationCreateRequest("authorizeUrl", "authCode", null));

        //then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AuthorizationsCreateResponse.class));

        assertThat(urlCaptor.getValue()).isEqualTo("http://localhost/api/connectors/ob/v1/authorizations");
        assertThat(result).isEqualTo(response);
    }

    @Test
    public void given5xxResponse_whenCreateAuthorization_shouldBeCalledExchangeWithParams() {
        //given
        AuthorizationsCreateResponse response = new AuthorizationsCreateResponse(new AuthorizationsCreateResponse.Data("1", "consentId", "accessToken"));
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AuthorizationsCreateResponse.class)))
            .willReturn(ResponseEntity.internalServerError().build());

        //when
        AuthorizationsCreateResponse result = testService.createAuthorization(new AuthorizationCreateRequest("authorizeUrl", "authCode", null));

        //then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AuthorizationsCreateResponse.class));

        assertThat(urlCaptor.getValue()).isEqualTo("http://localhost/api/connectors/ob/v1/authorizations");
        assertThat(result).isNull();
    }

    @Test
    public void whenUpdateAuthorization_shouldBeCalledExchangeWithParams() {
        //given
        String authorizationId = "1";
        AuthorizationsUpdateResponse response = new AuthorizationsUpdateResponse(new AuthorizationsUpdateResponse.Data(authorizationId, "accepted", "accessToken"));
        given(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(AuthorizationsUpdateResponse.class)))
            .willReturn(ResponseEntity.ok(response));

        //when
        AuthorizationsUpdateResponse result = testService.updateAuthorization(authorizationId, new AuthorizationUpdateRequest("userId", "accepted", Collections.emptyList()));

        //then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<String>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.PUT), entityCaptor.capture(), eq(AuthorizationsUpdateResponse.class));

        assertThat(urlCaptor.getValue()).isEqualTo("http://localhost/api/connectors/ob/v1/authorizations/1");
        assertThat(entityCaptor.getValue().getHeaders().getAccept()).isEqualTo(Lists.list(MediaType.APPLICATION_JSON));
        assertThat(entityCaptor.getValue().getHeaders().getContentType().toString()).isEqualTo("application/jwt");
        assertThat(entityCaptor.getValue().getHeaders().get("App-Id")).isEqualTo(Lists.list("QWERTY"));
        assertThat(entityCaptor.getValue().getHeaders().get("App-Secret")).isEqualTo(Lists.list("ASDFG"));
        assertThat(entityCaptor.getValue().getBody()).isNotEmpty();
        assertThat(result).isEqualTo(response);
    }

    @Test
    public void whenUpdatePayment_shouldBeCalledExchangeWithParams() {
        //given
        String paymentId = "1";
        EmptyJsonResponse response = new EmptyJsonResponse();
        given(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(EmptyJsonResponse.class)))
            .willReturn(ResponseEntity.ok(response));

        //when
        testService.updatePayment(paymentId, new PaymentUpdateRequest("consentId", "approved", "tppAppName"));

        //then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<String>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.PUT), entityCaptor.capture(), eq(EmptyJsonResponse.class));

        assertThat(urlCaptor.getValue()).isEqualTo("http://localhost/api/connectors/ob/v1/payments/1");
        assertThat(entityCaptor.getValue().getHeaders().getAccept()).isEqualTo(Lists.list(MediaType.APPLICATION_JSON));
        assertThat(entityCaptor.getValue().getHeaders().getContentType().toString()).isEqualTo("application/jwt");
        assertThat(entityCaptor.getValue().getHeaders().get("App-Id")).isEqualTo(Lists.list("QWERTY"));
        assertThat(entityCaptor.getValue().getHeaders().get("App-Secret")).isEqualTo(Lists.list("ASDFG"));
        assertThat(entityCaptor.getValue().getBody()).isNotEmpty();
    }
}
