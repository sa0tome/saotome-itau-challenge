package com.paymentsApi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.paymentsApi.dto.TransactionRequest;
import com.paymentsApi.entity.Client;
import com.paymentsApi.entity.Transaction;
import com.paymentsApi.enums.TransactionStatus;
import com.paymentsApi.repository.ClientRepository;
import com.paymentsApi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* Test classes have package scope as default, so "public" access modifier is not needed.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerV1IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Client sender;
    private Client receiver;
    private Transaction transaction1;
    private Transaction transaction2;
    private ObjectMapper objectMapper;

    @BeforeEach
    @Transactional
    public void setUp() {
        // Client client sender
        sender = new Client();
        sender.setName("Felipe Matos");
        sender.setAccountBalance(1000.00);
        sender.setAccountNumber(123456L);
        clientRepository.save(sender);

        // Client client receiver
        receiver = new Client();
        receiver.setName("Gisele Almeida");
        receiver.setAccountBalance(3000.00);
        receiver.setAccountNumber(654321L);
        clientRepository.save(receiver);

        // Transaction transaction1
        transaction1 = new Transaction();
        transaction1.setSender(sender);
        transaction1.setReceiver(receiver);
        transaction1.setTransactionTime(LocalDateTime.now());
        transaction1.setStatus(TransactionStatus.SUCCESS.getMessage());
        transactionRepository.save(transaction1);

        // Transaction transaction2
        transaction2 = new Transaction();
        transaction2.setSender(receiver);
        transaction2.setReceiver(sender);
        transaction2.setTransactionTime(LocalDateTime.now());
        transaction2.setStatus(TransactionStatus.FAIL.getMessage());
        transactionRepository.save(transaction2);

        // ObjectMapper object
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // this dependency is needed to handle LocalDate
    }

    @Test
    @Transactional
    void givenGetTransactionsByAccountNumber_whenAccountNumberIsRetrieved_then202IsSuccess() throws Exception {
        // Set up
        List<Transaction> expectedTransactions = Arrays.asList(transaction2, transaction1);
        TypeReference<List<Transaction>> jacksonTypeReference = new TypeReference<List<Transaction>>() {
        };

        // Act
        MvcResult result = mockMvc.perform(get("/v1/transactions/123456")).andExpect(status().isOk()).andReturn();
        List<Transaction> actualTransactions = objectMapper.readValue(result.getResponse().getContentAsString(), jacksonTypeReference);

        // Assert
        assertEquals(expectedTransactions.get(0).getId(), actualTransactions.get(0).getId());
        assertEquals(expectedTransactions.get(1).getId(), actualTransactions.get(1).getId());
    }

    @Test
    @Transactional
    void givenGetTransactionsByAccountNumber_whenAccountNumberIsNotRetrieved_then400IsBadRequest() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/v1/transactions/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void givenProcessPayment_whenPaymentSucess_then200isOK() throws Exception {
        // Set up
        // Transaction Request DTO
        TransactionRequest request = new TransactionRequest();
        request.setSenderAccountNumber(sender.getAccountNumber());
        request.setReceiverAccountNumber(receiver.getAccountNumber());
        request.setAmount(1000.00);

        // Serialized Transaction Request
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        MvcResult result = mockMvc.perform(post("/v1/transactions/pay").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        Transaction actualTransaction = objectMapper.readValue(responseBody, Transaction.class);

        // Assert
        assertEquals(sender.getName(), actualTransaction.getSender().getName());
        assertEquals(receiver.getName(), actualTransaction.getReceiver().getName());
        assertEquals(TransactionStatus.SUCCESS.getMessage(), actualTransaction.getStatus());
        assertEquals(0.0, actualTransaction.getSender().getAccountBalance());
        assertEquals(4000.0, actualTransaction.getReceiver().getAccountBalance());
    }
}