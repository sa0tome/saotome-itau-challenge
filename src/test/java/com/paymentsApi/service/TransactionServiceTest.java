package com.paymentsApi.service;

import com.paymentsApi.entity.Client;
import com.paymentsApi.entity.Transaction;
import com.paymentsApi.enums.TransactionStatus;
import com.paymentsApi.repository.ClientRepository;
import com.paymentsApi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/* Test classes have package scope as default, so "public" access modifier is not needed. */
@ExtendWith(MockitoExtension.class) // Mockito will handle injection
public class TransactionServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Client sender;
    private Client receiver;

    private Transaction transaction;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        // Create transaction
        transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTransactionTime(LocalDateTime.now());

        // Create transaction 2
        transaction2 = new Transaction();
        transaction2.setSender(receiver);
        transaction2.setReceiver(sender);
        transaction2.setTransactionTime(LocalDateTime.now());

        // Create Client sender
        sender = new Client();
        sender.setAccountNumber(123456L);
        sender.setAccountBalance(2000.00);

        // Create Client receiver
        receiver = new Client();
        receiver.setAccountNumber(654321L);
        receiver.setAccountBalance(1000.00);
    }

    @Test
    void testGetAllTransactions() {
        // Set up
        List<Transaction> expectedTransactions = Arrays.asList(transaction, transaction2);

        when(transactionRepository.findAll()).thenReturn(expectedTransactions);

        // Act
        List<Transaction> actualTransactions = transactionService.getAllTransactions();

        // Assert
        assertEquals(expectedTransactions, actualTransactions);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testGetTransactionsByAccountNumber() {
        // Set up
        when(clientRepository.findByAccountNumber(1L)).thenReturn(Optional.of(sender));
        when(transactionRepository.findBySender(sender)).thenReturn(Collections.singletonList(transaction));
        when(transactionRepository.findByReceiver(sender)).thenReturn(Collections.singletonList(transaction2));
        List<Transaction> expectedTransactions = Stream.of(transaction, transaction2).sorted(Comparator.comparing(Transaction::getTransactionTime).reversed()).toList();

        // Act
        List<Transaction> actualTransactions = transactionService.getTransactionsByAccountNumber(1L);

        // Assert
        assertEquals(expectedTransactions, actualTransactions);
        verify(clientRepository, times(1)).findByAccountNumber(1L);
        verify(transactionRepository, times(1)).findBySender(sender);
        verify(transactionRepository, times(1)).findByReceiver(sender);
    }

    /*
     * The tests for processTransaction method will use ArgumentCaptor.
     * ArgumentCaptor can capture modifications made in a class object. With this tool, it is possible to verify
     * if an object is being properly created without engaging database layer.
     *
     * In this test, I need to check if a Transaction object was created with the correct status, and if Client objects
     * have their accountBalance updated properly.
     */
    @Test
    void testProcessTransaction_Success() {
        // Set up
        when(clientRepository.findByAccountNumber(123456L)).thenReturn(Optional.of(sender));
        when(clientRepository.findByAccountNumber(654321L)).thenReturn(Optional.of(receiver));

        // Act
        transactionService.processTransaction(123456L, 654321L, 1000.00);

        // Capture and verify the Transaction object
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());
        Transaction capturedTransaction = transactionCaptor.getValue();

        assertNotNull(capturedTransaction);
        assertEquals(sender, capturedTransaction.getSender());
        assertEquals(receiver, capturedTransaction.getReceiver());
        assertEquals(TransactionStatus.SUCCESS.getMessage(), capturedTransaction.getStatus());

        // Capture and verify if Client objects were updated properly
        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository, times(2)).save(clientCaptor.capture());
        Client updatedSender = clientCaptor.getAllValues().get(0);
        Client updatedReceiver = clientCaptor.getAllValues().get(1);

        assertEquals(1000.00, updatedSender.getAccountBalance());
        assertEquals(2000.00, updatedReceiver.getAccountBalance());
    }

    /*
     * The tests for processTransaction method will use ArgumentCaptor.
     * ArgumentCaptor can capture modifications made in a class object. With this tool, it is possible to verify
     * if an object is being properly created without engaging database layer.
     *
     * In this test, I need to check if a Transaction object was created with the correct status, even if the payment
     * was not effective, and if Client objects were not updated at all.
     */
    @Test
    void testProcessTransaction_InsufficientFunds() {
        // Set up
        when(clientRepository.findByAccountNumber(123456L)).thenReturn(Optional.of(sender));
        when(clientRepository.findByAccountNumber(654321L)).thenReturn(Optional.of(receiver));

        // Act
        transactionService.processTransaction(123456L, 654321L, 2001.00);

        // Capture and verify the Transaction object
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());
        Transaction capturedTransaction = transactionCaptor.getValue();

        assertNotNull(capturedTransaction);
        assertEquals(sender, capturedTransaction.getSender());
        assertEquals(receiver, capturedTransaction.getReceiver());
        assertEquals(TransactionStatus.FAIL.getMessage(), capturedTransaction.getStatus());

        // Check if Client objects were not updated
        verify(clientRepository, never()).save(sender);
        verify(clientRepository, never()).save(receiver);
    }
}
