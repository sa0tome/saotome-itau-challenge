package com.paymentsApi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentsApi.entity.Client;
import com.paymentsApi.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* Test classes have package scope as default, so "public" access modifier is not needed. */
@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerV1IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    private Client sender;
    private Client receiver;
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

        // ObjectMapper object
        objectMapper = new ObjectMapper();
    }

    @Test
    @Transactional
    void testGetAllClients() throws Exception {
        // Set up
        List<Client> expectedClients = clientRepository.findAll();
        TypeReference<List<Client>> jacksonTypeReference = new TypeReference<List<Client>>() {
        };

        // Act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/clients").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        List<Client> actualClients = objectMapper.readValue(result.getResponse().getContentAsString(), jacksonTypeReference);

        // Assert
        assertEquals(expectedClients.get(0).getId(), actualClients.get(0).getId());
        assertEquals(expectedClients.get(1).getId(), actualClients.get(1).getId());
    }

    @Test
    @Transactional
    void givenGetClientByAccountNumber_whenAccountNumberIsValid_then200OK() throws Exception {
        // Act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/clients/123456").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        Client actualClient = objectMapper.readValue(responseBody, Client.class);

        // Assert
        assertEquals(sender.getId(), actualClient.getId());
    }

    @Test
    @Transactional
    void givenGetClientByAccountNumber_whenAccountNumberIsNotValid_then400isBadRequest() throws Exception {
        // Act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/clients/1").contentType(MediaType.APPLICATION_JSON)).andReturn();

        // Assert
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    @Transactional
    void givenCreateClient_whenCreateIsSuccess_then200OK() throws Exception {
        // Set up
        Client newClient = new Client();
        newClient.setName("Alicia Jones");
        newClient.setAccountBalance(1500.00);
        newClient.setAccountNumber(1000L);

        // Act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/clients/create").content(objectMapper.writeValueAsString(newClient)).contentType(MediaType.APPLICATION_JSON)).andReturn();

        // Assert
        assertEquals(200, result.getResponse().getStatus());
        Client actualClient = objectMapper.readValue(result.getResponse().getContentAsString(), Client.class);
        assertEquals(newClient.getAccountNumber(), actualClient.getAccountNumber());
    }

    @Test
    @Transactional
    void givenCreateClient_whenCreateIsNotSuccess_then409isConflict() throws Exception {
        // Set up
        Client newClient = new Client();
        newClient.setName("Alicia Jones");
        newClient.setAccountBalance(1500.00);
        newClient.setAccountNumber(654321L); // Same accountNumber from Client receiver

        // Act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/clients/create").content(objectMapper.writeValueAsString(newClient)).contentType(MediaType.APPLICATION_JSON)).andReturn();

        // Assert
        assertEquals(409, result.getResponse().getStatus());
    }
}
