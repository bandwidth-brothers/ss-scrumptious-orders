package com.ss.scrumptious_orders.exception;

import com.stripe.exception.InvalidRequestException;

public class RefundException extends IllegalStateException {
    public RefundException(long orderId) {
        super("refund failed for order: " + orderId);
    }
}
