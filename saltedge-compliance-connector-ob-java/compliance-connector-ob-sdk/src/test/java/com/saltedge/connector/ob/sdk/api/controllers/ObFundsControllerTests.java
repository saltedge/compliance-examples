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
package com.saltedge.connector.ob.sdk.api.controllers;

import com.saltedge.connector.ob.sdk.api.ApiConstants;
import com.saltedge.connector.ob.sdk.api.models.errors.Unauthorized;
import com.saltedge.connector.ob.sdk.api.models.request.FundsConfirmationRequest;
import com.saltedge.connector.ob.sdk.api.models.response.FundsConfirmationResponse;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.provider.ProviderServiceAbs;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccountIdentifier;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObFundsControllerTests {
    ProviderServiceAbs mockProviderService = Mockito.mock(ProviderServiceAbs.class);
    private final String userId = "1";
    private final ObAccountIdentifier debtorAccount = new ObAccountIdentifier("UK.OBIE.SortCodeAccountNumber", "123456");
    private ObFundsController testController;

    @BeforeEach
    void setUp() {
        testController = new ObFundsController();
        testController.providerService = mockProviderService;
    }

    @Test
    public void basePathTest() {
        assertThat(ObFundsController.BASE_PATH).isEqualTo(ApiConstants.API_BASE_PATH + "/funds_check");
    }

    @Test
    public void givenValidParams_whenFundsConfirmation_thenReturnStatus200AndEmptyResponse() {
        //given
        Consent consent = new Consent();
        consent.userId = userId;
        consent.debtorAccount = debtorAccount;
        ObAmount instructedAmount = new ObAmount("100.0", "GBP");
        FundsConfirmationRequest request = new FundsConfirmationRequest();
        request.instructedAmount = instructedAmount;

        //when
        ResponseEntity<FundsConfirmationResponse> result = testController.fundsConfirmation(consent, request);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        Mockito.verify(mockProviderService).confirmFunds(userId, debtorAccount, instructedAmount);
    }

    @Test
    public void givenInvalidConsent_whenFundsConfirmation_thenReturnUnauthorized() {
        assertThrows(Unauthorized.AccessDenied.class, () -> {
            //given
            Consent consent = new Consent();
            consent.userId = userId;
            consent.debtorAccount = null;
            ObAmount instructedAmount = new ObAmount("100.0", "GBP");
            FundsConfirmationRequest request = new FundsConfirmationRequest();
            request.instructedAmount = instructedAmount;

            //when
            ResponseEntity<FundsConfirmationResponse> result = testController.fundsConfirmation(consent, request);
        });
    }
}
