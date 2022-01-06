package com.ss.scrumptious_orders.exception;

public class PaymentException extends IllegalStateException {
    public PaymentException(long orderId){
        super("payment failed for order: " + orderId);
    }
}

