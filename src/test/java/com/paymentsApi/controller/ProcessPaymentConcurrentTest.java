package com.paymentsApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.paymentsApi.dto.TransactionRequest;
import com.paymentsApi.entity.Client;
import com.paymentsApi.entity.Transaction;
import com.paymentsApi.enums.TransactionStatus;
import com.paymentsApi.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("transactionControllerTest")
public class ProcessPaymentConcurrentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    private Client sender;
    private Client receiver;

    @BeforeEach
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
    }

    @Test
    public void givenConcurrentProcessPayment_whenDuplicateRequest_then400IsBadRequest() throws Exception {
        // Set up
        // ObjectMapper object
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // this dependency is needed to handle LocalDate

        // Transaction Request DTO
        TransactionRequest request = new TransactionRequest();
        request.setSenderAccountNumber(sender.getAccountNumber());
        request.setReceiverAccountNumber(receiver.getAccountNumber());
        request.setAmount(1000.00);

        String requestBody = objectMapper.writeValueAsString(request);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Act
        Future<MvcResult> future1 = executorService.submit(() -> mockMvc.perform(post("/v1/transactions/pay").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn());
        Thread.sleep(1000);
        Future<MvcResult> future2 = executorService.submit(() -> mockMvc.perform(post("/v1/transactions/pay").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn());

        MvcResult result1 = future1.get();
        MvcResult result2 = future2.get();

        Transaction result1Transaction = objectMapper.readValue(result1.getResponse().getContentAsString(), Transaction.class);

        // Assert
        // Result 1
        assertEquals(200, result1.getResponse().getStatus());
        assertEquals(0.0, result1Transaction.getSender().getAccountBalance());
        assertEquals(4000.0, result1Transaction.getReceiver().getAccountBalance());
        assertEquals(TransactionStatus.SUCCESS.getMessage(), result1Transaction.getStatus());

        // Result 2
        assertEquals(500, result2.getResponse().getStatus());
    }
}
