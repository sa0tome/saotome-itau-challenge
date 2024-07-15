package com.paymentsApi.dto;

/* A Data Transfer Object for Transaction Request.
 *
 * Long   senderAccountNumber   Client sender account number
 * Long   receiverAccountNumber Client receiver account number
 * Double amount                Payment amount
 */
public class TransactionRequest {
    private Long senderAccountNumber;
    private Long receiverAccountNumber;
    private Double amount;

    // Getters and setters
    public Long getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(Long senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public Long getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(Long receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}