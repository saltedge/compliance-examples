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
import com.saltedge.connector.sdk.api.controllers.BaseV2Controller;
import com.saltedge.connector.sdk.api.models.EmptyJsonModel;
import com.saltedge.connector.sdk.api.models.err.BadRequest;
import com.saltedge.connector.sdk.api.models.requests.CreatePiisTokenRequest;
import com.saltedge.connector.sdk.api.models.requests.FundsConfirmationRequest;
import com.saltedge.connector.sdk.api.models.requests.RevokeTokenRequest;
import com.saltedge.connector.sdk.api.models.responses.FundsConfirmationResponse;
import com.saltedge.connector.sdk.models.domain.PiisToken;
import com.saltedge.connector.sdk.services.priora.CreatePiisTokenService;
import com.saltedge.connector.sdk.services.priora.FundsService;
import com.saltedge.connector.sdk.services.priora.RevokeTokenByPrioraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * This controller is responsible for checking whether a specific amount is available at point of time of the request
 * on an account addressed by IBAN or other available identifiers.
 * https://priora.saltedge.com/docs/aspsp/v2/piis
 */
@RestController
@RequestMapping(PiisV2Controller.BASE_PATH)
@Validated
public class PiisV2Controller extends BaseV2Controller {
  public final static String BASE_PATH = SDKConstants.API_BASE_PATH + "/funds_confirmations";
  private static final Logger log = LoggerFactory.getLogger(PiisV2Controller.class);
  @Autowired
  CreatePiisTokenService createTokenService;
  @Autowired
  RevokeTokenByPrioraService revokeService;
  @Autowired
  FundsService checkFundsService;

  /**
   * Create an access token with a set of access rights, named scopes for PIIS.
   * As a result, Connector will send an update or fail callback to Salt Edge PSD2 Compliance with result of the operation,
   * be it success, fail or request for additional steps.
   *
   * @param request for token creation
   * @return empty JSON object
   */
  @PostMapping("/tokens")
  public ResponseEntity<EmptyJsonModel> createToken(@Valid CreatePiisTokenRequest request) {
    createTokenService.startAuthorization(request);
    return super.createEmptyOkResponseEntity();
  }

  /**
   * Revoke an already existing and active PIIS access token.
   *
   * @param aisToken linked to Access-Token header
   * @param request with sessionSecret
   * @return empty JSON object
   */
  @DeleteMapping("/tokens")
  public ResponseEntity<EmptyJsonModel> revokeToken(PiisToken token, @Valid RevokeTokenRequest request) {
    revokeService.revokeTokenAsync(token);
    return super.createEmptyOkResponseEntity();
  }

  /**
   * Checks whether a specific amount is available at point of time of the request on an account addressed by IBAN
   * or other available identifiers.
   *
   * @param token Token
   * @param request FundsConfirmationRequest
   * @return response
   */
  @PostMapping
  public ResponseEntity<FundsConfirmationResponse> checkFunds(
      @NotNull PiisToken token,
      @Valid FundsConfirmationRequest request
  ) {
    boolean result = checkFundsService.confirmFundsAvailability(token, request);
    return new ResponseEntity<>(new FundsConfirmationResponse(result), HttpStatus.OK);
  }
}
