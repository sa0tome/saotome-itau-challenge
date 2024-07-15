package com.paymentsApi.service;

import com.paymentsApi.entity.Client;
import com.paymentsApi.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /*
     * List all Clients.
     *
     * @return List of Clients
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /*
     * Get Client by id.
     *
     * @param Long id
     *
     * @return Client
     */
    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Client with id " + id + " not found"));
    }

    /*
     * Get Client by Account Number.
     *
     * @param Long accountNumber
     *
     * @return Client
     */
    public Client getClientByAccountNumber(Long accountNumber) {
        return clientRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new IllegalArgumentException("Client with accountNumber " + accountNumber + " not found"));
    }

    /*
     * List all Clients.
     *
     * @param Client client
     *
     * @return List of Clients
     */
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    /*
     * Delete a Client by id.
     *
     * @param Long id
     */
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}