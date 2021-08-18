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

import com.saltedge.connector.example.model.repository.UsersRepository;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.provider.ConnectorCallbackAbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping
public class UserDashboardController {
    private static Logger log = LoggerFactory.getLogger(UserDashboardController.class);
    public final static String BASE_PATH = "/users/dashboard";

    @Autowired
    protected ConnectorCallbackAbs connectorCallbackService;
    @Autowired
    protected UsersRepository usersRepository;

    // Show Dashboard page
    @GetMapping(BASE_PATH)
    public ModelAndView showDashboard(
            @RequestParam(value = SDKConstants.KEY_USER_ID) Long userId
    ) {
        List<String> accessTokens = connectorCallbackService.getActiveAccessTokens(String.valueOf(userId));
        ModelAndView result = new ModelAndView("users_dashboard");
        result.addObject(SDKConstants.KEY_USER_ID, userId);
        result.addObject("tokens", accessTokens);
        return result;
    }

    // Receive SignIn page credentials
    @PostMapping(BASE_PATH + "/revoke")
    public ModelAndView onSubmitCredentials(
            @RequestParam(value = SDKConstants.KEY_USER_ID) Long userId,
            @RequestParam(value = "access_token") String accessToken
    ) {
        connectorCallbackService.revokeAccountInformationConsent(String.valueOf(userId), accessToken);
        String redirect = "redirect:" + UserDashboardController.BASE_PATH
                + "?" + SDKConstants.KEY_USER_ID + "=" + userId;
        return new ModelAndView(redirect);
    }
}
