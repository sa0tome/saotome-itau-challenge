package com.paymentsApi.exception;

public class InsufficientFundsException extends RuntimeException {

    /*
     * Custom exception when an amount is greater than sender's funds.
     *
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}