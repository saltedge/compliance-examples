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

import com.saltedge.connector.example.connector.ConnectorService;
import com.saltedge.connector.example.connector.config.AuthorizationTypes;
import com.saltedge.connector.sdk.Constants;
import com.saltedge.connector.sdk.provider.ConnectorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(UserAuthorizeController.BASE_PATH)
public class UserAuthorizeController {
    public final static String BASE_PATH = "/oauth/authorize";
    private static Logger log = LoggerFactory.getLogger(UserAuthorizeController.class);
    @Autowired
    ConnectorService connectorService;
    @Autowired
    ConnectorCallback providerCallback;

    @GetMapping
    public ModelAndView signIn(@RequestParam(value = Constants.KEY_SESSION_SECRET, required = false) String sessionSecret) {
        ModelAndView result = new ModelAndView("user_sign_in");
        result.addObject(Constants.KEY_SESSION_SECRET, sessionSecret);
        return result;
    }

    @PostMapping
    public ModelAndView authorizeAndShowConsent(
            @RequestParam(name = Constants.KEY_SESSION_SECRET) String sessionSecret,
            @RequestParam String username,
            @RequestParam String password
    ) {
        Map<String, String> params = new HashMap<>();
        params.put("login", username);
        params.put("password", password);
        String userId = connectorService.authorizeUser(AuthorizationTypes.LOGIN_PASSWORD_AUTH_TYPE.code, params);
        if (userId == null) {
            return createErrorModel(sessionSecret);
        } else {
            ModelAndView result = new ModelAndView("redirect:/oauth/authorize/consent");
            result.addObject(Constants.KEY_SESSION_SECRET, sessionSecret);
            result.addObject(Constants.KEY_USER_ID, userId);
            return result;
        }
    }

    @PostMapping("/error")
    public ModelAndView signInError(
            @RequestParam(name = Constants.KEY_SESSION_SECRET) String sessionSecret
    ) {
        String returnToUrl = providerCallback.onOAuthAuthorizationError(sessionSecret);
        return returnToUrl == null ? createErrorModel(sessionSecret) : new ModelAndView("redirect:" + returnToUrl);
    }

    private ModelAndView createErrorModel(String sessionSecret) {
        ModelAndView result = new ModelAndView("user_sign_error");
        result.addObject(Constants.KEY_SESSION_SECRET, sessionSecret);
        return result;
    }
}
