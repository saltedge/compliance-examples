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
package com.saltedge.connector.example.controllers.consent;

import com.saltedge.connector.sdk.SDKConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class UserAuthenticateController extends ConsentBaseController {
  public final static String BASE_PATH = "/user/consent";
  private static final Logger log = LoggerFactory.getLogger(UserAuthenticateController.class);

  // Show SignIn page
  @GetMapping(BASE_PATH)
  public ModelAndView showSignIn(
      @RequestParam(name = SDKConstants.KEY_SCOPE) Scope scope,
      @RequestParam(name = SDKConstants.KEY_SESSION_SECRET, required = false) String sessionSecret,
      @RequestParam(name = SDKConstants.KEY_PAYMENT_ID, required = false) String paymentId
  ) {
    if (scope == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid scope");
    if (!StringUtils.hasLength(sessionSecret) && !StringUtils.hasLength(paymentId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no session_secret or payment_id");
    }

    ModelAndView result = createSignInModel(scope);
    if (Scope.payments.equals(scope)) {
      return result.addObject(SDKConstants.KEY_PAYMENT_ID, paymentId);
    } else {
      return result.addObject(SDKConstants.KEY_SESSION_SECRET, sessionSecret);
    }
  }

  // Receive SignIn page credentials
  @PostMapping(BASE_PATH)
  public ModelAndView onSubmitCredentials(
      @RequestParam(name = "submit") boolean positiveAction,
      @RequestParam(name = SDKConstants.KEY_SCOPE) Scope scope,
      @RequestParam(name = SDKConstants.KEY_SESSION_SECRET, required = false) String sessionSecret,//AIS or PIIS session secret
      @RequestParam(name = SDKConstants.KEY_PAYMENT_ID, required = false) String paymentId,//PIS
      @RequestParam String username,
      @RequestParam String password
  ) {
    if (!positiveAction) {
      switch (scope) {
        case accounts: return onAisDenied(sessionSecret);
        case payments: return onPisDenied(Long.parseLong(paymentId));
        case funds: return onPiisDenied(sessionSecret);
      }
    }

    if (scope == null || (!StringUtils.hasLength(sessionSecret) && !StringUtils.hasLength(paymentId))) {
      return createSignInModel(scope).addObject("error", "Unauthorized access.");
    }

    Long userId = findUser(username, password);// Find user by credentials
    if (userId == null) return createSignInModel(scope).addObject("error", "Invalid credentials.");

    ModelAndView consentRedirect = new ModelAndView("redirect:" + ConsentController.BASE_PATH + "/" + scope);
    consentRedirect.addObject(SDKConstants.KEY_SCOPE, scope.toString());
    consentRedirect.addObject(SDKConstants.KEY_USER_ID, userId);
    if (sessionSecret != null) consentRedirect.addObject(SDKConstants.KEY_SESSION_SECRET, sessionSecret);
    if (paymentId != null) consentRedirect.addObject(SDKConstants.KEY_PAYMENT_ID, paymentId);

    switch (scope) {
      case payments:
      case funds:
        return consentRedirect;
      case accounts:
        if (connectorCallbackService.isUserConsentRequired(sessionSecret)) {//Redirect to bank Offered Consent Page
          return consentRedirect;
        } else {
          return onAisSuccess(sessionSecret, String.valueOf(userId), null);
        }
      default:
        return createSignInModel(scope).addObject("error", "Unknown scope.");
    }
  }

  private ModelAndView createSignInModel(Scope scope) {
    ModelAndView result = new ModelAndView("user_oauth_sign_in");
    result.addObject("input_title", createInputTitle(scope));
    result.addObject(SDKConstants.KEY_SCOPE, scope.toString());
    return result;
  }

  private String createInputTitle(Scope scope) {
    switch (scope) {
      case payments: return "Authorization of payment initiation";
      case accounts: return "Authorization of access to accounts information";
      case funds: return "Authorization of access to funds confirmation information";
    }
    return "Input credentials";
  }

  public enum Scope {
    accounts, payments, funds
  }
}
