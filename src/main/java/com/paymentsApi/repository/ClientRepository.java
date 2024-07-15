package com.paymentsApi.repository;

import com.paymentsApi.entity.Client;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    /*
     * When searching a Client from its accountNumber, the application should lock these entities to avoid wrong impact
     * on account balance.
     *
     * Optional class is used here to handle possible null values from this custom repository method.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Client c WHERE c.accountNumber = :accountNumber")
    Optional<Client> findByAccountNumber(Long accountNumber);
}