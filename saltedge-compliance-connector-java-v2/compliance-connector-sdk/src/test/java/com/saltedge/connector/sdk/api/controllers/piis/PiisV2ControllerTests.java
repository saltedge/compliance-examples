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
package com.saltedge.connector.sdk.api.controllers.piis;

import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.requests.FundsConfirmationRequest;
import com.saltedge.connector.sdk.api.models.responses.FundsConfirmationResponse;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import com.saltedge.connector.sdk.provider.ProviderServiceAbs;
import com.saltedge.connector.sdk.services.priora.FundsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class PiisV2ControllerTests {
    FundsService mockFundsService = Mockito.mock(FundsService.class);
    ProviderServiceAbs mockProviderService = Mockito.mock(ProviderServiceAbs.class);
    PiisV2Controller controller = createController();

    @Test
    public void basePathTest() {
        assertThat(PiisV2Controller.BASE_PATH).isEqualTo(SDKConstants.API_BASE_PATH + "/funds_confirmations");
    }

    @Test
    public void whenCreate_thenReturnStatus200AndEmptyResponse() {
        PiisToken token = new PiisToken("1");
        FundsConfirmationRequest request = new FundsConfirmationRequest();
        ResponseEntity<FundsConfirmationResponse> result = controller.checkFunds(token, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        verify(mockFundsService).confirmFundsAvailability(token, request);
    }

    private PiisV2Controller createController() {
        PiisV2Controller controller = new PiisV2Controller();
        controller.checkFundsService = mockFundsService;
        controller.providerService = mockProviderService;
        return controller;
    }
}
