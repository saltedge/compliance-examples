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
package com.saltedge.connector.ob.sdk.model.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltedge.connector.ob.sdk.provider.dto.payment.ObRiskData;
import com.saltedge.connector.ob.sdk.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.AttributeConverter;
import java.io.IOException;

/**
 * JPA mapper for ObRiskData type
 */
public class PaymentRiskDataConverter implements AttributeConverter<ObRiskData, String> {
    private static final Logger log = LoggerFactory.getLogger(PaymentRiskDataConverter.class);
    private final ObjectMapper objectMapper = JsonTools.createDefaultMapper();

    @Override
    public String convertToDatabaseColumn(ObRiskData data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("PaymentRiskDataConverter: JSON writing error", e);
            return null;
        }
    }

    @Override
    public ObRiskData convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, ObRiskData.class);
        } catch (IOException e) {
            log.error("PaymentRiskDataConverter: JSON reading error", e);
            return null;
        }
    }
}
