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
package com.saltedge.connector.sdk.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Set of URL tools
 */
public class UrlTools {
    private static final Logger log = LoggerFactory.getLogger(UrlTools.class);

    /**
     * Appends params to url string
     *
     * @param url string (e.g. http://www.example.com)
     * @param paramKey string
     * @param paramValue string
     * @return resulting url string (e.g. http://www.example.com?key=value)
     */
    public static String appendParam(String url, String paramKey, String paramValue) {
        try {
            return UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam(paramKey, paramValue)
                    .build().toString();
        } catch (Exception e) {
            log.error("appendParam " + paramKey + "=" + paramValue  + " to url:" + url, e);
            return url;
        }
    }
}
