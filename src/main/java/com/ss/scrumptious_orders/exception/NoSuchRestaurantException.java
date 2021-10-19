package com.ss.scrumptious_orders.exception;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * To be thrown when a {@link com.ss.scrumptious_orders.entity.Restaurant}
 * cannot be found.
 *
 * <p>
 * Contains the offending ID that can be retrieved with
 * {@link #getRestaurantId()}.
 */
public class NoSuchRestaurantException extends NoSuchElementException {

    private final Long restaurantId;

    public NoSuchRestaurantException(Long id) {
        super("No restaurant record found for id=" + id);
        this.restaurantId = id;
    }

    public Optional<Long> getRestaurantId() {
        return Optional.ofNullable(restaurantId);
    }

}