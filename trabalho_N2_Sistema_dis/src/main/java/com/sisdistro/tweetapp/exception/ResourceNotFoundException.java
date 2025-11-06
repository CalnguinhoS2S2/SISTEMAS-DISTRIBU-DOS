package com.sisdistro.tweetapp.exception;

/**
 * Exceção lançada quando uma entidade não é encontrada.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
