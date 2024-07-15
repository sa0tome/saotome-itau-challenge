package com.paymentsApi.service;

import com.paymentsApi.entity.Client;
import com.paymentsApi.entity.Transaction;
import com.paymentsApi.enums.TransactionStatus;
import com.paymentsApi.exception.InsufficientFundsException;
import com.paymentsApi.repository.ClientRepository;
import com.paymentsApi.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;

    @Autowired
    Environment environment;

    public TransactionService(TransactionRepository transactionRepository, ClientRepository clientRepository) {
        this.transactionRepository = transactionRepository;
        this.clientRepository = clientRepository;
    }

    /*
     * Process the transaction.
     *
     * @param Long senderAccountNumber   Client sender account number
     * @param Long receiverAccountNumber Client receiver account number
     * @param Double amount              Value amount of the transaction
     *
     * @return                           Transaction object.
     */

    @Transactional
    public Transaction processTransaction(Long senderAccountNumber, Long receiverAccountNumber, Double amount) {
        // Find clients
        Client sender = clientRepository.findByAccountNumber(senderAccountNumber).orElseThrow(() -> new IllegalArgumentException("Invalid sender Account Number"));
        Client receiver = clientRepository.findByAccountNumber(receiverAccountNumber).orElseThrow(() -> new IllegalArgumentException("Invalid receiver Account Number"));

        // Create new Transaction
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);

        // Executes the Transaction business logic and logs the result
        String result = this.executeTransaction(sender, receiver, amount);
        transaction.setStatus(result);
        transaction.setTransactionTime(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    /*
     * Returns all Transactions.
     * @return List<Transaction> List of Transaction objects.
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /*
     * Returns all Transactions from a Client by Account Number in desc order.
     *
     * @param Long accountNumber Client Account Number
     *
     * @return                   List of Transaction objects.
     */
    public List<Transaction> getTransactionsByAccountNumber(Long accountNumber) {
        Client client = clientRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new IllegalArgumentException("Invalid Account Number"));
        // Collect all transactions from Client in a List
        List<Transaction> bySender = transactionRepository.findBySender(client);
        List<Transaction> byReceiver = transactionRepository.findByReceiver(client);
        List<Transaction> allTransactions = new ArrayList<>(bySender);
        allTransactions.addAll(byReceiver);
        // Sort list in descending order by transactionTime
        allTransactions = allTransactions.stream().sorted(Comparator.comparing(Transaction::getTransactionTime).reversed()).collect(Collectors.toList());

        return allTransactions;
    }

    /*
     * Private method that holds business logic for the transaction process.
     *
     * @param Client sender   Object
     * @param Client receiver Object
     * @param Double amount   Value amount of the transaction
     *
     * @return                Status message of the transaction
     */
    @Transactional
    protected String executeTransaction(Client sender, Client receiver, Double amount) {
        try {
            // Simulate a long-running transaction only in test environment for ProcessPaymentConcurrentTest
            if (environment != null && Arrays.asList(environment.getActiveProfiles()).contains("transactionControllerTest")) {
                Thread.sleep(5000); // 5 seconds delay
            }
            if (sender.getAccountBalance() < amount) {
                throw new InsufficientFundsException(TransactionStatus.FAIL.getMessage());
            }
            sender.setAccountBalance(sender.getAccountBalance() - amount);
            receiver.setAccountBalance(receiver.getAccountBalance() + amount);
            clientRepository.save(sender);
            clientRepository.save(receiver);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return TransactionStatus.SUCCESS.getMessage();
    }
}