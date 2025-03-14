package br.com.rafaellbarros.order.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(final String message) { super(message); }
}
