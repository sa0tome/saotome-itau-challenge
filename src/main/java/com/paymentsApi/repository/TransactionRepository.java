package com.paymentsApi.repository;

import com.paymentsApi.entity.Client;
import com.paymentsApi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySender(Client sender);

    List<Transaction> findByReceiver(Client receiver);
}