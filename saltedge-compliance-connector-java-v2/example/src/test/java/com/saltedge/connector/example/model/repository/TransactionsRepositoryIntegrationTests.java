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

import com.saltedge.connector.example.compliance_connector.ProviderService;
import com.saltedge.connector.example.config.DatabaseInitializer;
import com.saltedge.connector.example.model.TransactionEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionsRepositoryIntegrationTests {
	@Autowired
	private DatabaseInitializer databaseInitializer;
	@Autowired
	private TransactionsRepository testRepository;

	@Test
	public void givenTransactions_whenFindByAccountIdAndMadeOnBetween_thenReturnPage() {
		// given
		databaseInitializer.seed();

		//then
		List<TransactionEntity> allTransactions = testRepository.findAll();
		assertThat(allTransactions.size()).isEqualTo(93);
		Long accountId = allTransactions.get(0).account.id;
		LocalDate fromDate = allTransactions.get(0).madeOn;
		LocalDate toDate = allTransactions.get(92).madeOn;

		Page<TransactionEntity> page = testRepository.findByAccountIdAndMadeOnBetween(accountId, fromDate, toDate, PageRequest.of(0, ProviderService.PAGE_SIZE));
		assertThat(page.getContent().size()).isEqualTo(30);
		assertTrue(page.hasNext());

		page = testRepository.findByAccountIdAndMadeOnBetween(accountId, fromDate, toDate, PageRequest.of(1, ProviderService.PAGE_SIZE));
		assertThat(page.getContent().size()).isEqualTo(30);
		assertTrue(page.hasNext());

		page = testRepository.findByAccountIdAndMadeOnBetween(accountId, fromDate, toDate, PageRequest.of(2, ProviderService.PAGE_SIZE));
		assertThat(page.getContent().size()).isEqualTo(30);
		assertTrue(page.hasNext());

		page = testRepository.findByAccountIdAndMadeOnBetween(accountId, fromDate, toDate, PageRequest.of(3, ProviderService.PAGE_SIZE));
		assertThat(page.getContent().size()).isEqualTo(3);
		assertFalse(page.hasNext());
	}
}
