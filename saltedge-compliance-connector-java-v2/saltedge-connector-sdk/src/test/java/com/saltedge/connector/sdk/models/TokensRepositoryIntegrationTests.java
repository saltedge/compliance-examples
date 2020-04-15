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
package com.saltedge.connector.sdk.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TokensRepositoryIntegrationTests {
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private TokensRepository tokensRepository;

	@Test
	public void whenFindFirstBySessionSecret_thenReturnToken() {
		// given
		Token token = new Token("secret1", "tppAppName", "authTypeCode", null);
		token.accessToken = "123456";
		entityManager.persist(token);
		entityManager.flush();

		// when
		Token found = tokensRepository.findFirstBySessionSecret("secret1");

		// then
		assertThat(found.accessToken).isEqualTo("123456");
		assertThat(found.sessionSecret).isEqualTo("secret1");
		assertThat(found.id).isGreaterThan(0L);

		found.setCreatedAt(new Date(0));
		found.setUpdatedAt(new Date(0));

		assertThat(found.getCreatedAt()).isEqualTo(new Date(0));
		assertThat(found.getUpdatedAt()).isEqualTo(new Date(0));
	}

	@Test
	public void whenFindFirstByAccessToken_thenReturnToken() {
		// given
		Token token = new Token("secret1", "tppAppName", "authTypeCode", null);
		token.accessToken = "123456";
		token.tokenExpiresAt = Instant.parse("2019-11-18T16:04:50.915Z");
		entityManager.persist(token);
		entityManager.flush();

		// when
		Token found = tokensRepository.findFirstByAccessToken("123456");

		// then
		assertThat(found.accessToken).isEqualTo("123456");
		assertThat(found.tokenExpiresAt.toString()).isEqualTo("2019-11-18T16:04:50.915Z");
		assertThat(found.sessionSecret).isEqualTo("secret1");
	}

	@Test
	public void whenFindFirstByAccessToken_thenReturnNull() {
		// given
		Token token = new Token("secret1", "tppAppName", "authTypeCode", null);
		token.accessToken = "123456";
		entityManager.persist(token);
		entityManager.flush();

		// when
		Token found = tokensRepository.findFirstByAccessToken("654321");

		// then
		assertThat(found).isNull();
	}
}
