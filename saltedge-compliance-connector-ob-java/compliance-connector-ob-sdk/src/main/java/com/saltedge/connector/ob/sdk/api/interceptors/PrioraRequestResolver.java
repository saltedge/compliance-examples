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
package com.saltedge.connector.ob.sdk.api.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltedge.connector.ob.sdk.SDKConstants;
import com.saltedge.connector.ob.sdk.api.ApiConstants;
import com.saltedge.connector.ob.sdk.api.models.errors.BadRequest;
import com.saltedge.connector.ob.sdk.api.models.request.*;
import com.saltedge.connector.ob.sdk.api.models.response.EmptyJsonResponse;
import com.saltedge.connector.ob.sdk.config.ApplicationProperties;
import com.saltedge.connector.ob.sdk.tools.JsonTools;
import io.jsonwebtoken.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Resolves controller's method parameters into argument values in the context of a given request
 */
@Component
public class PrioraRequestResolver implements HandlerMethodArgumentResolver {
    private static final Logger log = LoggerFactory.getLogger(PrioraRequestResolver.class);
    private final ObjectMapper mapper = JsonTools.createDefaultMapper();
    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> type = parameter.getParameterType();
        List<Class<? extends PrioraBaseRequest>> supportedTypes = Arrays.asList(
          AccountsConsentsCreateRequest.class,
          ConsentsRevokeRequest.class,
          CreateBaseRequest.class,
          TransactionsIndexRequest.class,
          DefaultRequest.class,
          ErrorsRequest.class,
          FundsConfirmationRequest.class,
          FundsConsentsCreateRequest.class,
          PaymentConsentsCreateRequest.class,
          PaymentCreateRequest.class,
          PaymentFundsConfirmationRequest.class,
          PaymentUpdateRequest.class
        );
        return supportedTypes.contains(type) || type.equals(EmptyJsonResponse.class);
    }

    @Override
    public Object resolveArgument(
        @NotNull MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        String authorization = webRequest.getHeader(ApiConstants.HEADER_AUTHORIZATION);
        if (StringUtils.hasText(authorization)) return parsePayloadAndValidate(authorization, parameter.getParameterType());
        else throw new BadRequest.AuthorizationMissing();
    }

    private <T> T parsePayloadAndValidate(String authorization, Class<T> clazz) throws BadRequest.JWTExpiredSignature, BadRequest.JWTDecodeError {
        try {
            String bearerToken = authorization.replace("Bearer ", "");
            Jws<Claims> claims = Jwts.parser()
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
