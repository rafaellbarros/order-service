package br.com.rafaellbarros.order.exception;

public class DuplicateOrderException extends RuntimeException {

    public DuplicateOrderException(final String message) { super(message); }

}
