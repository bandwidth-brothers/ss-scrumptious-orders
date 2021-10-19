package com.ss.scrumptious_orders.exception;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * To be thrown when a {@link com.ss.scrumptious_orders.entity.Menuitem} cannot
 * be found.
 *
 * <p>
 * Contains the offending ID that can be retrieved with
 * {@link #getMenuitemId()}.
 */
public class NoSuchMenuitemException extends NoSuchElementException {

    private final Long menuitemId;

    public NoSuchMenuitemException(Long id) {
        super("No menuitem record found for id=" + id);
        this.menuitemId = id;
    }

    public Optional<Long> getMenuitemId() {
        return Optional.ofNullable(menuitemId);
    }

}