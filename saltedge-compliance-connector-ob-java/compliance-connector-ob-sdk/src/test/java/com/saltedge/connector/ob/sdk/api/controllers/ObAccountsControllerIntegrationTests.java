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

import com.saltedge.connector.ob.sdk.TestTools;
import com.saltedge.connector.ob.sdk.api.ApiConstants;
import com.saltedge.connector.ob.sdk.api.models.request.DefaultRequest;
import com.saltedge.connector.ob.sdk.api.models.response.AccountsResponse;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import com.saltedge.connector.ob.sdk.model.jpa.ConsentsRepository;
import com.saltedge.connector.ob.sdk.provider.ProviderServiceAbs;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAccount;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObAmount;
import com.saltedge.connector.ob.sdk.provider.dto.account.ObBalance;
import com.saltedge.connector.ob.sdk.tools.JsonTools;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class ObAccountsControllerIntegrationTests {
    private final String userId = "1";
    private Consent consent;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @MockBean
    protected ConsentsRepository consentsRepository;
    @MockBean
    protected ProviderServiceAbs mockProviderService;

    @BeforeEach
    void setUp() {
        consent = Consent.createAisConsent("tppAppName", "1", "Authorised", Collections.emptyList(), null, null, null);
        consent.id = 1L;
        consent.userId = userId;
        consent.accessToken = "validToken";
        given(consentsRepository.findFirstByAccessToken("validToken")).willReturn(consent);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void whenList_thenReturnStatus200AndJson() throws Exception {
        //given
        List<ObAccount> testData = getTestAccountsData();
        given(mockProviderService.getAccountsOfUser(userId)).willReturn(testData);
        AccountsResponse expectedResponse = new AccountsResponse(testData);
        final String expectedResponseContent = JsonTools.createDefaultMapper().writeValueAsString(expectedResponse);
        String authorization = TestTools.createAuthorizationHeaderValue(new DefaultRequest(), TestTools.getInstance().getRsaPrivateKey());

        //when
        String responseString = mockMvc
            .perform(MockMvcRequestBuilders
                .get(ObAccountsController.BASE_PATH)
                .header(ApiConstants.HEADER_CLIENT_ID, "clientId")
                .header(ApiConstants.HEADER_ACCESS_TOKEN, consent.accessToken)
                .header(ApiConstants.HEADER_AUTHORIZATION, authorization)
            )
            .andReturn().getResponse().getContentAsString();

        //then
        assertThat(responseString).isEqualTo(expectedResponseContent);
        assertThat(responseString).contains("2021-01-01T10:15:30Z");
    }

    private List<ObAccount> getTestAccountsData() {
        List<ObBalance> balances = Lists.list(
            new ObBalance(new ObAmount("1000.0", "GBP"), "debit", "closingAvailable", Instant.parse("2021-01-01T10:15:30.00Z")),
            new ObBalance(new ObAmount("2000.0", "GBP"), "debit", "openingAvailable", Instant.now())
        );
        return Lists.list(new ObAccount(
            "1",
            "GBP",
            "account",
            "active",
            balances
        ));
    }
}
