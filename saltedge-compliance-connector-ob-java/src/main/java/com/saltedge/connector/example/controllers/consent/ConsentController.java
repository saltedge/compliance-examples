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
package com.saltedge.connector.example.controllers.consent;

import com.saltedge.connector.example.controllers.BaseController;
import com.saltedge.connector.example.model.AccountEntity;
import com.saltedge.connector.ob.sdk.SDKConstants;
import com.saltedge.connector.ob.sdk.config.ApplicationProperties;
import com.saltedge.connector.ob.sdk.model.jpa.Consent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Handle Authentication and consent confirmation pages
 */
@Controller
@RequestMapping
public class ConsentController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ConsentController.class);
    private final static String BASE_PATH = "/consent";
    private final static String AUTHENTICATION_PATH = BASE_PATH + "/auth";
    private final static String CONSENT_PATH = BASE_PATH + "/confirm";
    @Autowired
    public ApplicationProperties applicationProperties;

    // Show Login page
    @GetMapping(AUTHENTICATION_PATH)
    public ModelAndView showLogin(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString() + "?" + request.getQueryString();
        if (requestURL.startsWith("http://")) requestURL = requestURL.replaceFirst("http://", "https://");
        String authCode = UUID.randomUUID().toString();

        String redirectUri = connectorSDKService.onUserInitiateConsentAuthorization(
          requestURL,
          authCode,
          Instant.now().plus(10, ChronoUnit.MINUTES)
        );
        if (StringUtils.hasText(redirectUri)) {
            return new ModelAndView("redirect:" + redirectUri);
        }
        return createLoginModel(authCode);
    }

    // Receive Login page credentials
    @PostMapping(AUTHENTICATION_PATH)
    public ModelAndView onSubmitCredentials(
            @RequestParam(name = SDKConstants.KEY_AUTH_CODE, required = false) String authCode,
            @RequestParam String username,
            @RequestParam String password
    ) {
        // Find user by credentials
        Long userId = findUser(username, password);

        if (userId == null) {
            log.error("User " + username + " not exists.");
            return createLoginModel(authCode).addObject("error", "Invalid credentials.");
        } else {
            return new ModelAndView("redirect:" + CONSENT_PATH)
              .addObject(SDKConstants.KEY_USER_ID, userId)
              .addObject(SDKConstants.KEY_AUTH_CODE, authCode);
        }
    }

    @GetMapping(CONSENT_PATH)
    public ModelAndView showConsent(
      @RequestParam(name = SDKConstants.KEY_AUTH_CODE) String authCode,
      @RequestParam(name = SDKConstants.KEY_USER_ID) Long userId
    ) {
        Consent consent = connectorSDKService.getConsent(authCode);
        if (consent == null) return createConsentModel(authCode, userId, "No related consent.");

        AccountEntity firstAccount = null;
        if (consent.isPisConsent()) {
            List<AccountEntity> accounts = accountsRepository.findByUserId(userId);
            firstAccount = accounts.stream().findFirst().orElse(null);
        }

        return createConsentModel(
          authCode,
          userId,
          consent,
          firstAccount != null ? firstAccount.accountNumber : null
        );
    }

    // Receive Confirm/Deny consent
    @PostMapping(CONSENT_PATH)
    public ModelAndView onAuthorizeConsent(
      @RequestParam(name = "consent_auth_code") String authCode,
      @RequestParam(name = SDKConstants.KEY_USER_ID) Long userId,
      @RequestParam(name = "identifier", required = false) String identifier,
      @RequestParam(name = "action", required = false) String action
    ) {
        Consent consent = connectorSDKService.getConsent(authCode);
        if (consent == null) return createConsentModel(authCode, userId, "No related consent.");

        String redirectUri;
        if ("confirm".equals(action)) {
            if (identifier != null) {
                redirectUri = connectorSDKService.onConsentApprove(
                  authCode,
                  String.valueOf(userId),
                  Collections.singletonList(identifier)
                );
            } else {
                redirectUri = connectorSDKService.onConsentApprove(authCode, String.valueOf(userId));
            }
        } else {
            redirectUri = connectorSDKService.onConsentDeny(authCode, String.valueOf(userId));
        }
        if (redirectUri == null) {
            return createConsentModel(authCode, userId, "Can not redirect back.");
        } else {
            return new ModelAndView("redirect:" + redirectUri);
        }
    }

    private ModelAndView createLoginModel(String authCode) {
        ModelAndView result = new ModelAndView("consent_auth");
        result.addObject(SDKConstants.KEY_AUTH_CODE, authCode);
        return result;
    }

    private ModelAndView createConsentModel(String authCode, Long userId, Consent consent, String identifier) {
        ModelAndView result = new ModelAndView("consent_confirm");
        result.addObject(SDKConstants.KEY_USER_ID, userId);
        result.addObject(SDKConstants.KEY_AUTH_CODE, authCode);
        result.addObject("title", createConsentTitle(consent));
        result.addObject("description", createConsentDescription(userId, consent));
        if (identifier != null) {
            result.addObject("identifier", identifier);
        }
        return result;
    }

    private ModelAndView createConsentModel(String authCode, Long userId, String error) {
        ModelAndView result = new ModelAndView("consent_confirm");
        result.addObject(SDKConstants.KEY_USER_ID, userId);
        result.addObject(SDKConstants.KEY_AUTH_CODE, authCode);
        result.addObject("title", "Consent error");
        result.addObject("error", error);
        return result;
    }

    private String createConsentTitle(Consent consent) {
        if (consent.isPiisConsent()) return "Funds confirmation";
        else if (consent.isPisConsent()) return "Payment initiation";
        else return "Access to accounts information";
    }

    private String createConsentDescription(Long userId, Consent consent) {
        String prefix = consent.tppName + " requests ";
        String action = "access to accounts information";
        if (consent.isPiisConsent()) action = "funds confirmation";
        else if (consent.isPisConsent()) action = "payment initiation";
        return prefix + action;
    }
}
