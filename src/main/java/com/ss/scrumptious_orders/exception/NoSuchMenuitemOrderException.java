package com.ss.scrumptious_orders.exception;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.ss.scrumptious.common_entities.entity.MenuitemOrderKey;

/**
 * To be thrown when a {@link com.ss.scrumptious_orders.entity.MenuitemOrder}
 * cannot be found.
 *
 * <p>
 * Contains the offending ID that can be retrieved with
 * {@link #getMenuitemOrderId()}.
 */
public class NoSuchMenuitemOrderException extends NoSuchElementException {

    private final MenuitemOrderKey menuitemOrderId;

    public NoSuchMenuitemOrderException(MenuitemOrderKey id) {
        super("No menuitemOrder record found for orderId = " + id.getOrderId() + ", menuitemId = "
                + id.getMenuitemId());
        this.menuitemOrderId = id;
    }

    public Optional<MenuitemOrderKey> getMenuitemOrderId() {
        return Optional.ofNullable(menuitemOrderId);
    }

}