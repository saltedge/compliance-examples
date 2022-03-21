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
package com.saltedge.connector.sdk.services.provider;

import com.saltedge.connector.sdk.api.models.err.NotFound;
import com.saltedge.connector.sdk.models.ConsentStatus;
import com.saltedge.connector.sdk.models.domain.AisToken;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RevokeTokenByProviderService extends BaseService {

    public AisToken revokeAisTokenBySessionSecret(String sessionSecret) {
        return revokeAisToken(findAisTokenBySessionSecret(sessionSecret));
    }

    public AisToken revokeAisTokenByUserIdAndAccessToken(String userId, String accessToken) {
        return revokeAisToken(aisTokensRepository.findFirstByUserIdAndAccessToken(userId, accessToken));
    }

    public PiisToken revokePiisTokenBySessionSecret(String sessionSecret) {
        return revokePiisToken(piisTokensRepository.findFirstBySessionSecret(sessionSecret));
    }

    private AisToken revokeAisToken(AisToken token) {
        if (token == null) throw new NotFound.TokenNotFound();

        token.status = ConsentStatus.REVOKED;
        return aisTokensRepository.save(token);
    }

    private PiisToken revokePiisToken(PiisToken token) {
        if (token == null) throw new NotFound.TokenNotFound();

        token.status = ConsentStatus.REVOKED;
        return piisTokensRepository.save(token);
    }
}
