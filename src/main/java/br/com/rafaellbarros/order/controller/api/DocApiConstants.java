package br.com.rafaellbarros.order.controller.api;

public final class DocApiConstants {
    static final String OK_CODE = "200";
    static final String BAD_REQUEST_CODE = "400";
    static final String BAD_REQUEST_DESCRIPTION = "The request was malformed, omitting mandatory attributes, "
        + "either in the payload or through attributes in the URL.";
    static final String NOT_FOUND_CODE = "404";
    static final String NOT_FOUND_DESCRIPTION = "The resource could not be found.";
    static final String INTERNAL_SERVER_ERROR_CODE = "500";
    static final String INTERNAL_SERVER_ERROR_DESCRIPTION = "An error occurred in the API gateway or microservice.";

    private DocApiConstants() {
        throw new UnsupportedOperationException("Instantiation not allowed");
    }
}