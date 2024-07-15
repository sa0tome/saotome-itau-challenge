package com.paymentsApi.service;

import com.paymentsApi.entity.Client;
import com.paymentsApi.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/* Test classes have package scope as default, so "public" access modifier is not needed. */
@ExtendWith(MockitoExtension.class) // Mockito will handle injection
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client client1;
    private Client client2;

    @BeforeEach
    void setUp() {
        // Initialize Client 1
        client1 = new Client();
        client1.setName("Felipe Matos");
        client1.setAccountBalance(1392.40);
        client1.setAccountNumber(123456L);

        // Initialize Client 2
        client2 = new Client();
        client2.setName("Gisele Almeida");
        client2.setAccountBalance(2000.10);
        client2.setAccountNumber(123457L);
    }

    @Test
    void testGetAllClients() {
        // Set up
        List<Client> expectedClients = Arrays.asList(client1, client2);

        // Instruct ClientRepository to return Client 1 and Client 2
        when(clientRepository.findAll()).thenReturn(expectedClients);

        // Act
        List<Client> actualClients = clientService.getAllClients();

        // Assert
        assertEquals(expectedClients, actualClients);
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void testGetClientById() {
        // Set up
        // Should be an Optional object due to CrudRepository parent class method return type
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client1));

        // Act
        Client actualClient = clientService.getClientById(1L);

        // Assert
        assertEquals(client1, actualClient);
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void testGetClientByAccountNumber() {
        // Set up
        when(clientRepository.findByAccountNumber(123456L)).thenReturn(Optional.of(client1));

        // Act
        Client actualClient = clientService.getClientByAccountNumber(123456L);

        // Assert
        assertEquals(client1, actualClient);
        verify(clientRepository, times(1)).findByAccountNumber(123456L);
    }

    @Test
    void testSaveClient() {
        // Set up
        when(clientRepository.save(client1)).thenReturn(client1);

        // Act
        Client savedClient = clientService.saveClient(client1);

        // Assert
        assertEquals(client1, savedClient);
        verify(clientRepository, times(1)).save(client1);
    }

    @Test
    void testDeleteClient() {
        // Act
        clientService.deleteClient(1L);

        // Only assert if method is called, because return is none
        verify(clientRepository, times(1)).deleteById(1L);
    }
}