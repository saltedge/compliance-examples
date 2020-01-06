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
import com.saltedge.connector.example.model.Payment;
import com.saltedge.connector.example.model.PaymentsRepository;
import com.saltedge.connector.sdk.config.Constants;
import com.saltedge.connector.sdk.provider.ProviderCallback;
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

@Controller
@RequestMapping(ConfirmCodeController.BASE_PATH)
public class ConfirmCodeController {
    private static Logger log = LoggerFactory.getLogger(ConfirmCodeController.class);
    public final static String BASE_PATH = "/oauth/confirm";
    @Autowired
    ConnectorService connectorService;
    @Autowired
    PaymentsRepository paymentsRepository;
    @Autowired
    ProviderCallback providerCallback;

    @GetMapping
    public ModelAndView confirm(@RequestParam(name = Constants.KEY_PAYMENT_ID, required = false) String paymentId) {
        ModelAndView result = new ModelAndView("confirm");
        result.addObject(Constants.KEY_PAYMENT_ID, paymentId);
        result.addObject("submit_to", ConfirmCodeController.BASE_PATH);
        return result;
    }

    @PostMapping
    public ModelAndView confirm(
            @RequestParam(name = Constants.KEY_PAYMENT_ID) String paymentIdString,
            @RequestParam(name = Constants.KEY_CONFIRMATION_CODE) String confirmationCode
    ) {
        Long paymentId = Long.valueOf(paymentIdString);
        Payment payment = paymentsRepository.findById(paymentId).orElse(null);

        if (payment == null || confirmationCode.isEmpty()) return new ModelAndView("confirm_error");
        boolean result = connectorService.confirmPayment(payment, confirmationCode);
        String returnToUrl = (result) ? providerCallback.paymentConfirmationOAuthSuccess(paymentIdString, payment.extra) : null;
        return new ModelAndView(StringUtils.isEmpty(returnToUrl) ? "confirm_error" : "redirect:" + returnToUrl);
    }
}
