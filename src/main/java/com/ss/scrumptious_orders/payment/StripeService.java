package com.ss.scrumptious_orders.payment;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.stripe.model.Refund;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

@Service
public class StripeService {

    @Value("${STRIPE_SECRET_KEY}")
    String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public Charge charge(Long orderId, Integer totalCost, String token)
            throws StripeException{
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", totalCost);
        chargeParams.put("currency", "USD");
        chargeParams.put("description", "orderId: " + orderId);
        chargeParams.put("source", token);
        return Charge.create(chargeParams);
    }

    public Refund refund(String stripeId) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("charge", stripeId);
        return Refund.create(params);
    }
}
