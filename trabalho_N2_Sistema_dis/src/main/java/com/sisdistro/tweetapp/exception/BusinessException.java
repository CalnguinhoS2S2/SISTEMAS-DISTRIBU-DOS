package com.sisdistro.tweetapp.exception;

/**
 * Exceção genérica para regras de negócio violadas.
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }
}
