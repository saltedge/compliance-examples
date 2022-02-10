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

package com.saltedge.connector.sdk;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SDKConstantsTest {
	@Test
	public void valuesTest() {
		assertThat(SDKConstants.API_BASE_PATH).isEqualTo("/api/priora/v2");
		assertThat(SDKConstants.CALLBACK_BASE_PATH).isEqualTo("/api/connectors/v2");
		assertThat(SDKConstants.HEADER_AUTHORIZATION).isEqualTo("authorization");
		assertThat(SDKConstants.HEADER_CLIENT_ID).isEqualTo("client-id");
		assertThat(SDKConstants.HEADER_ACCESS_TOKEN).isEqualTo("access-token");
		assertThat(SDKConstants.STATUS_WAITING_CONFIRMATION_CODE).isEqualTo("waiting_confirmation_code");
		assertThat(SDKConstants.STATUS_RECEIVED).isEqualTo("received");
		assertThat(SDKConstants.STATUS_RCVD).isEqualTo("RCVD");
	}
}
