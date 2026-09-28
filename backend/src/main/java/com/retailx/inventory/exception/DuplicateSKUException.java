package com.retailx.inventory.exception;

public class DuplicateSKUException extends RuntimeException {
    public DuplicateSKUException(String message) {
        super(message);
    }
}
