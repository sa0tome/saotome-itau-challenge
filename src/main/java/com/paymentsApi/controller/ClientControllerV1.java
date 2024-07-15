package com.paymentsApi.controller;

import com.paymentsApi.entity.Client;
import com.paymentsApi.service.ClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("v1/clients")
public class ClientControllerV1 {

    private final ClientService clientService;

    public ClientControllerV1(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{accountNumber}")
    public Client getClientByAccountNumber(@PathVariable Long accountNumber) {
        return clientService.getClientByAccountNumber(accountNumber);
    }

    @PostMapping("/create")
    public Client createClient(@RequestBody Client client) {
        return clientService.saveClient(client);
    }

}
