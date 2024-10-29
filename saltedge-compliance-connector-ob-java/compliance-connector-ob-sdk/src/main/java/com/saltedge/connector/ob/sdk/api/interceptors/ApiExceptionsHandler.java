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

import com.saltedge.connector.ob.sdk.api.models.errors.BadRequest;
import com.saltedge.connector.ob.sdk.api.models.errors.HttpErrorParams;
import com.saltedge.connector.ob.sdk.api.models.errors.NotFound;
import com.saltedge.connector.ob.sdk.api.models.errors.Unauthorized;
import com.saltedge.connector.ob.sdk.api.models.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

/**
 * Global error handler for a Spring REST API
 */
@ControllerAdvice
@Order(0)
public class ApiExceptionsHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionsHandler.class);

    @ExceptionHandler({
      BadRequest.class,
      NotFound.class,
      Unauthorized.class
    })
    public ResponseEntity<ErrorResponse> handleCustomException(Exception ex, WebRequest request) {
        HttpStatus errorStatus = ex instanceof HttpErrorParams ? ((HttpErrorParams) ex).getErrorStatus() : HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(ex);
        log.error(error.toString());
        return ResponseEntity.status(errorStatus).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        ErrorResponse error = new ErrorResponse("WrongRequestFormat", ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
