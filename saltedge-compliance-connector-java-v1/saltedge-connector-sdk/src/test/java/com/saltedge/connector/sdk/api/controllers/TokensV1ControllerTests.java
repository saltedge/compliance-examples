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

import com.saltedge.connector.sdk.api.mapping.ConfirmTokenRequest;
import com.saltedge.connector.sdk.api.mapping.CreateTokenRequest;
import com.saltedge.connector.sdk.api.mapping.DefaultRequest;
import com.saltedge.connector.sdk.api.mapping.EmptyJsonModel;
import com.saltedge.connector.sdk.api.services.tokens.ConfirmTokenService;
import com.saltedge.connector.sdk.api.services.tokens.CreateTokenService;
import com.saltedge.connector.sdk.api.services.tokens.ReconnectTokenService;
import com.saltedge.connector.sdk.api.services.tokens.RevokeTokenService;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.models.persistence.Token;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class TokensV1ControllerTests {
    CreateTokenService mockCreateTokenService = Mockito.mock(CreateTokenService.class);
    ReconnectTokenService mockReconnectTokenService = Mockito.mock(ReconnectTokenService.class);
    ConfirmTokenService mockConfirmTokenService = Mockito.mock(ConfirmTokenService.class);
    RevokeTokenService mockRevokeTokenService = Mockito.mock(RevokeTokenService.class);
    TokensV1Controller controller = createController();

    @Test
    public void basePathTest() throws Exception {
        assertThat(TokensV1Controller.BASE_PATH).isEqualTo(Constants.API_BASE_PATH + "/v1/tokens");
    }

    @Test
    public void whenCreate_thenReturnStatus200AndEmptyResponse() {
        CreateTokenRequest request = new CreateTokenRequest();
        ResponseEntity<EmptyJsonModel> result = controller.create(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockCreateTokenService).startAuthorization(request);
        verifyNoInteractions(mockConfirmTokenService, mockRevokeTokenService);
    }

    @Test
    public void whenConfirm_thenReturnStatus200AndEmptyResponse() {
        ConfirmTokenRequest request = new ConfirmTokenRequest();
        request.sessionSecret = "sessionSecret";
        ResponseEntity<EmptyJsonModel> result = controller.confirm(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockConfirmTokenService).confirmToken("sessionSecret", new HashMap<>());
        verifyNoInteractions(mockCreateTokenService, mockRevokeTokenService);
    }

    @Test
    public void whenReconnect_thenReturnStatus200AndEmptyResponse() {
        DefaultRequest request = new DefaultRequest();
        request.sessionSecret = "sessionSecret";
        Token token = new Token("1");
        ResponseEntity<EmptyJsonModel> result = controller.reconnect(token, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockReconnectTokenService).reconnect(token, "sessionSecret");
        verifyNoInteractions(mockConfirmTokenService, mockRevokeTokenService);
    }

    @Test
    public void whenRevoke_thenReturnStatus200AndEmptyResponse() {
        Token token = new Token();
        token.accessToken = "accessToken";
        DefaultRequest request = new DefaultRequest();
        ResponseEntity<EmptyJsonModel> result = controller.revoke(token, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockRevokeTokenService).revokeTokenAsync(token);
        verifyNoInteractions(mockConfirmTokenService, mockCreateTokenService);
    }

    @Test
    public void whenCancel_thenReturnStatus200AndEmptyResponse() {
        DefaultRequest request = new DefaultRequest();
        request.sessionSecret = "sessionSecret";
        ResponseEntity<EmptyJsonModel> result = controller.cancel(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockRevokeTokenService).revokeTokenBySessionSecret("sessionSecret");
        verifyNoInteractions(mockConfirmTokenService, mockCreateTokenService);
    }

    private TokensV1Controller createController() {
        TokensV1Controller controller = new TokensV1Controller();
        controller.createTokenService = mockCreateTokenService;
        controller.reconnectTokenService = mockReconnectTokenService;
        controller.confirmService = mockConfirmTokenService;
        controller.revokeService = mockRevokeTokenService;
        return controller;
    }
}
