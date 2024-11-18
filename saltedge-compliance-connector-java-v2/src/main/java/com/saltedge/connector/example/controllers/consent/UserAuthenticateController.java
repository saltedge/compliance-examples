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
            @RequestParam(name = SDKConstants.KEY_STATE, required = false) String state
    ) {
        if (scope == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid scope");
        if (!StringUtils.hasLength(state)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no state");
        }

        return createSignInModel(scope).addObject(SDKConstants.KEY_STATE, state);
    }

    // Receive SignIn page credentials
    @PostMapping(BASE_PATH)
    public ModelAndView onSubmitCredentials(
            @RequestParam(name = "submit") boolean positiveAction,
            @RequestParam(name = SDKConstants.KEY_SCOPE) Scope scope,
            @RequestParam(name = SDKConstants.KEY_STATE, required = false) String state,//AIS, PIS, PIIS session secret
            @RequestParam String username,
            @RequestParam String password
    ) {
        Long userId = findUser(username, password);// Find user by credentials

        if (!positiveAction) {
            return switch (scope) {
                case accounts -> onAisDenied(state, String.valueOf(userId));
                case payments -> onPisDenied(Long.parseLong(state), String.valueOf(userId));
                case funds -> onPiisDenied(state, String.valueOf(userId));
            };
        }
        if (scope == null || !StringUtils.hasLength(state)) {
            return createSignInModel(scope).addObject("error", "Unauthorized access.");
        }

        if (userId == null) return createSignInModel(scope).addObject("error", "Invalid credentials.");

        ModelAndView consentRedirect = new ModelAndView("redirect:" + ConsentController.BASE_PATH + "/" + scope);
        consentRedirect.addObject(SDKConstants.KEY_SCOPE, scope.toString());
        consentRedirect.addObject(SDKConstants.KEY_USER_ID, userId);
        consentRedirect.addObject(SDKConstants.KEY_STATE, state);

        switch (scope) {
            case payments:
            case funds:
                return consentRedirect;
            case accounts:
                if (connectorCallbackService.isAccountSelectionRequired(state)) {//Redirect to bank Offered Consent Page
                    return consentRedirect;
                } else {
                    return onAisSuccess(state, String.valueOf(userId), null);
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
        return switch (scope) {
            case payments -> "Authorization of payment initiation";
            case accounts -> "Authorization of access to accounts information";
            case funds -> "Authorization of access to funds confirmation information";
        };
    }

    public enum Scope {
        accounts, payments, funds
    }
}
