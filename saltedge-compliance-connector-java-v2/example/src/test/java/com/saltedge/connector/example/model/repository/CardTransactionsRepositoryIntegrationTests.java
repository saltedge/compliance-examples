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
package com.saltedge.connector.example.model.repository;

import com.saltedge.connector.example.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CardTransactionsRepositoryIntegrationTests {
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private CardTransactionsRepository testRepository;

	@Test
	public void whenCollectTransactions_thenReturnList() {
		// given
		UserEntity user = new UserEntity("name", "email", "address", "dob", "phone", "username", "password");
		entityManager.persist(user);
		CardAccountEntity account1 = new CardAccountEntity("account1", "pan1", "currencyCode", "product1", "status", "100.0", "100.0", "100.0", null, user);
		entityManager.persist(account1);
		CardAccountEntity account2 = new CardAccountEntity("account2", "pan2", "currencyCode", "product1", "status", "200.0", "200.0", "200.0", null, user);
		entityManager.persist(account2);

		CardTransactionEntity transaction11 = new CardTransactionEntity(
				"11.0",
				"EUR",
				"t11",
				LocalDate.parse("2019-11-18"),
				"posted",
				null,
				null,
				account1
		);
		entityManager.persist(transaction11);
		CardTransactionEntity transaction12 = new CardTransactionEntity(
				"12.0",
				"EUR",
				"t12",
				LocalDate.parse("2019-12-30"),
				"posted",
				null,
				null,
				account1
		);
		entityManager.persist(transaction12);
		CardTransactionEntity transaction21 = new CardTransactionEntity(
				"21.0",
				"EUR",
				"t21",
				LocalDate.parse("2019-11-18"),
				"posted",
				null,
				null,
				account2
		);
		entityManager.persist(transaction21);
		CardTransactionEntity transaction22 = new CardTransactionEntity(
				"22.0",
				"EUR",
				"t22",
				LocalDate.parse("2019-12-18"),
				"posted",
				null,
				null,
				account2
		);
		entityManager.persist(transaction22);

		entityManager.flush();
		LocalDate fromDate = LocalDate.parse("2019-11-01");
		LocalDate toDate = LocalDate.parse("2019-11-30");

		// when
		Page<CardTransactionEntity> found = testRepository.findByCardAccountIdAndMadeOnBetween(account1.id, fromDate, toDate, PageRequest.of(0, 2));

		// then
		assertThat(found.getContent().size()).isEqualTo(1);
	}
}
