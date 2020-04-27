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
package com.saltedge.connector.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static com.saltedge.connector.example.ExampleApplication.CONNECTOR_PACKAGE;
import static com.saltedge.connector.example.ExampleApplication.EXAMPLE_PACKAGE;

/**
 * Example Application which simulates work of ASPSP/Bank application.
 * This application is just a POC (Proof Of Concept).
 */
@SpringBootApplication(scanBasePackages = {EXAMPLE_PACKAGE, CONNECTOR_PACKAGE})
@EnableJpaRepositories(basePackages = {EXAMPLE_PACKAGE, CONNECTOR_PACKAGE})
@EntityScan(basePackages = {EXAMPLE_PACKAGE, CONNECTOR_PACKAGE})
public class ExampleApplication {
    public static final String EXAMPLE_PACKAGE = "com.saltedge.connector.example";
    public static final String CONNECTOR_PACKAGE = "com.saltedge.connector.sdk";

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
