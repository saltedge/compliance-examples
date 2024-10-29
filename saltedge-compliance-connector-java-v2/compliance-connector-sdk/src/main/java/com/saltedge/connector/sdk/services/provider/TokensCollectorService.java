/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2022 Salt Edge.
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
package com.saltedge.connector.sdk.services.provider;

import com.saltedge.connector.sdk.models.ConsentStatus;
import com.saltedge.connector.sdk.models.ParticipantAccount;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tokens collector.
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TokensCollectorService extends BaseService {

    public List<AisToken> collectAisTokensByUserId(@NotEmpty String userId) {
        return aisTokensRepository.findAllByUserId(userId);
    }

    public List<PiisToken> collectPiisTokensByUserId(@NotEmpty String userId) {
        return piisTokensRepository.findAllByUserId(userId);
    }

    public List<String> collectActiveAccessTokensByUserId(@NotEmpty String userId) {
        Stream<String> aisStream = aisTokensRepository.findAllByUserId(userId).stream()
            .filter(item -> item.status == ConsentStatus.CONFIRMED)
            .map(item -> item.accessToken);
        Stream<String> piisStream = piisTokensRepository.findAllByUserId(userId).stream()
            .filter(item -> item.status == ConsentStatus.CONFIRMED)
            .map(item -> item.accessToken);
        return Stream.concat(aisStream, piisStream).collect(Collectors.toList());
    }

    public ParticipantAccount collectFundsConfirmationConsentData(@NotEmpty String sessionSecret) {
        PiisToken token = piisTokensRepository.findFirstBySessionSecret(sessionSecret);
        return token == null ? null : token.account;
    }
}
