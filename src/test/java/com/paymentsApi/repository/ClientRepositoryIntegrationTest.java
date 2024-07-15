package com.paymentsApi.repository;

import com.paymentsApi.entity.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/* Test classes have package scope as default, so "public" access modifier is not needed. */
@DataJpaTest
public class ClientRepositoryIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testUniqueAccountNumber() {
        // Create and save the first client
        Client client1 = new Client();
        client1.setName("Felipe Matos");
        client1.setAccountBalance(1392.40);
        client1.setAccountNumber(123456L);
        clientRepository.save(client1);

        // Create and save the second client with the same account number
        Client client2 = new Client();
        client2.setName("Gisele Almeida");
        client2.setAccountBalance(2000.10);
        client2.setAccountNumber(123456L);

        // Assert that saving the second client throws a DataIntegrityViolationException
        assertThrows(DataIntegrityViolationException.class, () -> {
            clientRepository.saveAndFlush(client2);
        });
    }

    @Test
    void testNotNullableClientFields() {
        Client client1 = new Client();

        // Try saving Client with null fields and except exception
        assertThrows(DataIntegrityViolationException.class, () -> {
            clientRepository.saveAndFlush(client1);
        });
    }
}
