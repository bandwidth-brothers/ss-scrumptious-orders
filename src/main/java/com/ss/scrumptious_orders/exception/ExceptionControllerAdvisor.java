package com.ss.scrumptious_orders.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvisor {

    public static final String ERROR_KEY = "error";
    public static final String STATUS_KEY = "status";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PaymentAlreadyRefundException.class)
    public Map<String, Object> handlePaymentAlreadyRefundExceptions(PaymentAlreadyRefundException ex) {
        log.error(ex.getMessage());
        return baseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PaymentException.class)
    public Map<String, Object> handlePaymentExceptions(PaymentException ex) {
        log.error(ex.getMessage());
        return baseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RefundException.class)
    public Map<String, Object> handleRefundExceptions(RefundException ex) {
        log.error(ex.getMessage());
        return baseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> baseResponse(String errorMsg, HttpStatus status) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put(ERROR_KEY, errorMsg);
        response.put(STATUS_KEY, status.value());
        return response;
    }
}
