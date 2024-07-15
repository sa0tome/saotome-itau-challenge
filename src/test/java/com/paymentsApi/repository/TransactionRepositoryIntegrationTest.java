package com.paymentsApi.repository;

import com.paymentsApi.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/* Test classes have package scope as default, so "public" access modifier is not needed. */
@DataJpaTest
public class TransactionRepositoryIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testNotNullableTransactionFields() {
        Transaction transaction = new Transaction();

        // Try saving Client with null fields and except exception
        assertThrows(DataIntegrityViolationException.class, () -> {
            transactionRepository.save(transaction);
        });
    }

}
