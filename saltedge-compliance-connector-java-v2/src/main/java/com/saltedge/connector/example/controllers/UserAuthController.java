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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class UserAuthController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(UserAuthController.class);
    public final static String BASE_PATH = "/users/auth";

    // Show SignIn page
    @GetMapping(BASE_PATH)
    public ModelAndView showSignIn() {
        return createSignInModel();
    }

    // Receive SignIn page credentials
    @PostMapping(BASE_PATH)
    public ModelAndView onSubmitCredentials(@RequestParam String username, @RequestParam String password) {
        // Find user by credentials
        Long userId = findUser(username, password);
        if (userId == null) {
            return createSignInModel().addObject("error", "Invalid credentials.");
        } else {
            //Redirect to User Dashboard Page
            String redirect = "redirect:" + UserDashboardController.BASE_PATH
                    + "?" + SDKConstants.KEY_USER_ID + "=" + userId;
            return new ModelAndView(redirect);
        }
    }

    private ModelAndView createSignInModel() {
        ModelAndView result = new ModelAndView("users_sign_in");
        result.addObject("input_title", "Input credentials to access dashboard");
        return result;
    }
}
