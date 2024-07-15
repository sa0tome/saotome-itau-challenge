package com.paymentsApi.controller;

import com.paymentsApi.dto.TransactionRequest;
import com.paymentsApi.entity.Transaction;
import com.paymentsApi.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/transactions")
public class TransactionControllerV1 {

    private final TransactionService transactionService;

    public TransactionControllerV1(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{accountNumber}")
    public List<Transaction> getTransactionsByAccountNumber(@PathVariable Long accountNumber) {
        return transactionService.getTransactionsByAccountNumber(accountNumber);
    }

    @PostMapping("/pay")
    public Transaction processPayment(@RequestBody TransactionRequest request) {
        return transactionService.processTransaction(request.getSenderAccountNumber(), request.getReceiverAccountNumber(), request.getAmount());
    }
}
