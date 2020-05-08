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
package com.saltedge.connector.example.controllers;

import com.saltedge.connector.sdk.SDKConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.NotEmpty;

@Controller
@RequestMapping
public class UserAuthorizeController extends UserBaseController {
    private static Logger log = LoggerFactory.getLogger(UserAuthorizeController.class);
    public final static String TYPE_ACCOUNTS = "accounts";
    public final static String TYPE_PAYMENTS = "payments";
    private final static String BASE_PATH = "/oauth/authorize";
    public final static String ACCOUNTS_BASE_PATH = BASE_PATH + "/" + TYPE_ACCOUNTS;
    public final static String PAYMENTS_BASE_PATH = BASE_PATH + "/" + TYPE_PAYMENTS;
    public final static String SESSION_TYPE = "session_type";

    // Show SignIn page
    @GetMapping(BASE_PATH + "/{" + SESSION_TYPE + "}")
    public ModelAndView showSignInForAccounts(
            @PathVariable(SESSION_TYPE) @NotEmpty String sessionType,
            @RequestParam(value = SDKConstants.KEY_SESSION_SECRET, required = false) String sessionSecret,
            @RequestParam(value = SDKConstants.KEY_PAYMENT_ID, required = false) String paymentId
    ) {
        if (TYPE_PAYMENTS.equals(sessionType)) {
            return createSignInModel(sessionType).addObject(SDKConstants.KEY_PAYMENT_ID, paymentId);
        } else {
            return createSignInModel(sessionType).addObject(SDKConstants.KEY_SESSION_SECRET, sessionSecret);
        }
    }

    // Receive SignIn page credentials
    @PostMapping(BASE_PATH + "/{" + SESSION_TYPE + "}")
    public ModelAndView onSubmitCredentials(
            @PathVariable(SESSION_TYPE) @NotEmpty String sessionType,
            @RequestParam(value = SDKConstants.KEY_SESSION_SECRET, required = false) String sessionSecret,
            @RequestParam(value = SDKConstants.KEY_PAYMENT_ID, required = false) String paymentId,
            @RequestParam String username,
            @RequestParam String password
    ) {
        if ((!TYPE_PAYMENTS.equals(sessionType) && !TYPE_ACCOUNTS.equals(sessionType))
                || (StringUtils.isEmpty(sessionSecret) && StringUtils.isEmpty(paymentId))
        ) {
            return createSignInModel(sessionType).addObject("error", "Unauthorized access.");
        }

        // Find user by credentials
        Long userId = findUser(username, password);
        if (userId == null) return createSignInModel(sessionType).addObject("error", "Invalid credentials.");

        //Redirect to bank Offered Consent Page
        if (connectorCallbackService.isUserConsentRequired(sessionSecret)) {
            ModelAndView result = new ModelAndView("redirect:/oauth/consent/"+sessionType);
            result.addObject(SDKConstants.KEY_USER_ID, userId);
            if (TYPE_PAYMENTS.equals(sessionType)) result.addObject(SDKConstants.KEY_PAYMENT_ID, paymentId);
            if (TYPE_ACCOUNTS.equals(sessionType)) result.addObject(SDKConstants.KEY_SESSION_SECRET, sessionSecret);
            return result;
        } else {
            return onAccountInformationAuthorizationSuccess(sessionSecret, String.valueOf(userId), null);
        }
    }

    private ModelAndView createSignInModel(String sessionType) {
        ModelAndView result = new ModelAndView("user_sign_in");
        result.addObject("input_title", createInputTitle(sessionType));
        result.addObject(SESSION_TYPE, sessionType);
        return result;
    }

    private String createInputTitle(String sessionType) {
        if (TYPE_PAYMENTS.equals(sessionType)) return "Input credentials to authenticate payment";
        else if (sessionType.equals(TYPE_ACCOUNTS)) return "Input credentials to access accounts information";
        return "Input credentials";
    }
}
