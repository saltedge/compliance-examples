/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2022 Salt Edge.
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
import com.saltedge.connector.example.model.PaymentEntity;
import com.saltedge.connector.example.model.PaymentStatus;
import com.saltedge.connector.sdk.SDKConstants;
import com.saltedge.connector.sdk.api.models.ProviderConsents;
import com.saltedge.connector.sdk.tools.KeyTools;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.constraints.NotEmpty;

abstract public class ConsentBaseController extends BaseController {

    protected ModelAndView onAisSuccess(
            @NotEmpty String sessionSecret,
            @NotEmpty String userId,
            ProviderConsents consents
    ) {
        String returnToUrl = connectorCallbackService.onAccountInformationAuthorizationSuccess(
                sessionSecret,
                userId,
                KeyTools.generateToken(32),
                consents
        );
        if (returnToUrl == null) return redirectToAccountsAuth(sessionSecret, userId);
        else return new ModelAndView("redirect:" + returnToUrl);
    }

    protected ModelAndView onAisDenied(@NotEmpty String sessionSecret, String userId) {
        String returnToUrl = connectorCallbackService.onAccountInformationAuthorizationFail(sessionSecret, userId);
        if (returnToUrl == null) return redirectToAccountsAuth(sessionSecret, null);
        else return new ModelAndView("redirect:" + returnToUrl);
    }

    protected ModelAndView onPisDenied(long paymentId, String userId) {
        PaymentEntity payment = paymentsRepository.findById(paymentId).orElse(null);
        String extra = "";
        if (payment != null) {
            extra = payment.extra;
            payment.status = PaymentStatus.FAILED;
            paymentsRepository.save(payment);
        }
        String returnToUrl = connectorCallbackService.onPaymentInitiationAuthorizationFail(extra);

        if (returnToUrl != null) return new ModelAndView("redirect:" + returnToUrl);
        return redirectToPaymentsAuth(String.valueOf(paymentId), null);
    }

    protected ModelAndView onPiisDenied(@NotEmpty String sessionSecret, String userId) {
        String returnToUrl = connectorCallbackService.onFundsConfirmationConsentAuthorizationFail(sessionSecret, userId);
        if (returnToUrl == null) return redirectToFundsAuth(sessionSecret, null);
        else return new ModelAndView("redirect:" + returnToUrl);
    }

    protected ModelAndView redirectToPaymentsAuth(String state, String userId) {
        return redirectToModelAuth(UserAuthenticateController.Scope.payments.toString(),state, userId);
    }

    protected ModelAndView redirectToFundsAuth(String state, String userId) {
        return redirectToModelAuth(UserAuthenticateController.Scope.funds.toString(),state, userId);
    }

    private ModelAndView redirectToAccountsAuth(String state, String userId) {
        return redirectToModelAuth(UserAuthenticateController.Scope.accounts.toString(),state, userId);
    }

    private ModelAndView redirectToModelAuth(String scope, String state, String userId) {
        return new ModelAndView("redirect:" + UserAuthenticateController.BASE_PATH)
                .addObject(SDKConstants.KEY_SCOPE, scope)
                .addObject(SDKConstants.KEY_STATE, state)
                .addObject(SDKConstants.KEY_USER_ID, userId);
    }
}
