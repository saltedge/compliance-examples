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

import com.saltedge.connector.sdk.TestTools;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyToolsTest {
	@Test
	public void generateTokenTest() {
		assertThat(new KeyTools()).isNotNull();

		String token1 = KeyTools.generateToken(32);
		String token2 = KeyTools.generateToken(32);

		assertThat(token1).isNotEmpty();
		assertThat(token1).isNotEqualTo(token2);
	}

	@Test
	public void convertPemStringToPublicKeyTest1() {
		assertThat(KeyTools.convertPemStringToPublicKey(null)).isNull();
		assertThat(KeyTools.convertPemStringToPublicKey("ABC")).isNull();
	}

	@Test
	public void convertPemStringToPublicKeyTest2() {
		assertThat(KeyTools.convertPemStringToPublicKey(TestTools.getInstance().getRsaPublicKeyString())).isNotNull();
	}

	@Test
	public void convertPemStringToPrivateKeyTest1() {
		assertThat(KeyTools.convertPemStringToPrivateKey(null)).isNull();
		assertThat(KeyTools.convertPemStringToPrivateKey("ABC")).isNull();
	}

	@Test
	public void convertPemStringToPrivateKeyTest2() {
		assertThat(KeyTools.convertPemStringToPrivateKey(TestTools.getInstance().getRsaPrivateKeyString())).isNotNull();
	}
}
