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
package com.saltedge.connector.example.compliance_connector;

import com.saltedge.connector.example.config.DatabaseInitializer;
import com.saltedge.connector.example.model.TransactionEntity;
import com.saltedge.connector.example.model.repository.TransactionsRepository;
import com.saltedge.connector.sdk.models.TransactionsPage;
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
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderServiceTests {
	@Autowired
	private DatabaseInitializer databaseInitializer;
	@Autowired
	private ProviderService testService;

	@Test
	public void givenTransactions_whenGetTransactionsOfAccount_thenReturnPage() {
		// given
		databaseInitializer.seed();

		List<TransactionEntity> allTransactions = databaseInitializer.transactionsRepository.findAll();
		assertThat(allTransactions.size()).isEqualTo(93);
		Long userId = allTransactions.get(0).account.user.id;
		Long accountId = allTransactions.get(0).account.id;
		LocalDate fromDate = allTransactions.get(0).madeOn;
		LocalDate toDate = allTransactions.get(92).madeOn;
		String fromID = null;

		//when
		TransactionsPage result = testService.getTransactionsOfAccount(userId.toString(), accountId.toString(), fromDate, toDate, fromID);
		fromID = result.nextId;

		//then
		assertThat(result.transactions.size()).isEqualTo(30);
		assertThat(fromID).isEqualTo("1");

		//when
		result = testService.getTransactionsOfAccount(userId.toString(), accountId.toString(), fromDate, toDate, fromID);
		fromID = result.nextId;

		//then
		assertThat(result.transactions.size()).isEqualTo(30);
		assertThat(fromID).isEqualTo("2");

		//when
		result = testService.getTransactionsOfAccount(userId.toString(), accountId.toString(), fromDate, toDate, fromID);
		fromID = result.nextId;

		//then
		assertThat(result.transactions.size()).isEqualTo(30);
		assertThat(fromID).isEqualTo("3");

		//when
		result = testService.getTransactionsOfAccount(userId.toString(), accountId.toString(), fromDate, toDate, fromID);

		//then
		assertThat(result.transactions.size()).isEqualTo(3);
		assertNull(result.nextId);

		//when
		result = testService.getTransactionsOfAccount(userId.toString(), accountId.toString(), fromDate, toDate, "4");

		//then
		assertTrue(result.transactions.isEmpty());
		assertNull(result.nextId);
	}
}
