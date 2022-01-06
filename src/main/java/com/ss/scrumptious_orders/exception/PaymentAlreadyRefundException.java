package com.ss.scrumptious_orders.exception;

public class PaymentAlreadyRefundException extends IllegalStateException {
    public PaymentAlreadyRefundException(long orderId){
        super("payment already refund for order: " + orderId);
    }
}
