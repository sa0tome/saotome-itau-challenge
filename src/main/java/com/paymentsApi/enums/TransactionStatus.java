package com.paymentsApi.enums;

/* TransactionStatus
 * Class to define a Transaction status message.
 */
public enum TransactionStatus {
    SUCCESS("SUCCESS: Transaction executed."), FAIL("FAILED: Sender does not have sufficient funds to proceed payment");

    private final String message;

    TransactionStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}