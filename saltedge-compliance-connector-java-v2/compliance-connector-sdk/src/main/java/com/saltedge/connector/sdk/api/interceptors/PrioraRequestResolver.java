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
package com.saltedge.connector.sdk.api.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.EmptyJsonModel;
import com.saltedge.connector.sdk.api.models.err.BadRequest;
import com.saltedge.connector.sdk.api.models.requests.*;
import com.saltedge.connector.sdk.config.ApplicationProperties;
import com.saltedge.connector.sdk.tools.JsonTools;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

/**
 * Resolves controller's method parameters into argument values in the context of a given request
 */
@Component
public class PrioraRequestResolver implements HandlerMethodArgumentResolver {
    private static final Logger log = LoggerFactory.getLogger(PrioraRequestResolver.class);
    @Autowired
    ApplicationProperties applicationProperties;
    private final ObjectMapper mapper = JsonTools.createDefaultMapper();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {//TODO TRY TO USE ONLY PARENT CLASS
        Class<?> type = parameter.getParameterType();
        return type.equals(CreateAisTokenRequest.class)
                || type.equals(RevokeTokenRequest.class)
                || type.equals(TransactionsRequest.class)
                || type.equals(CreatePaymentRequest.class)
                || type.equals(CreatePiisTokenRequest.class)
                || type.equals(FundsConfirmationRequest.class)
                || type.equals(ErrorsRequest.class)
                || type.equals(DefaultRequest.class)
                || type.equals(EmptyJsonModel.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String authorization = webRequest.getHeader(SDKConstants.HEADER_AUTHORIZATION);
        return parsePayloadAndValidate(authorization, parameter.getParameterType());
    }

    private <T> T parsePayloadAndValidate(String authorization, Class<T> clazz) throws BadRequest.JWTExpiredSignature, BadRequest.JWTDecodeError {
        try {
            String bearerToken = authorization.replace("Bearer ", "");
            Jws<Claims> claims = Jwts.parserBuilder()
              .setSigningKey(applicationProperties.getPrioraPublicKey())
              .build()
              .parseClaimsJws(bearerToken);
            return mapper.convertValue(claims.getBody().get(SDKConstants.KEY_DATA, Map.class), clazz);
        } catch (ExpiredJwtException e) {
            throw new BadRequest.JWTExpiredSignature();
        } catch (JwtException e) {
            throw new BadRequest.JWTDecodeError(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequest.WrongRequestFormat(e.getMessage());
        }
    }
}
