package br.com.rafaellbarros.order.exception.handler;

import br.com.rafaellbarros.order.exception.DuplicateOrderException;
import br.com.rafaellbarros.order.exception.OrderNotFoundException;
import br.com.rafaellbarros.order.model.dto.ErrorDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateOrderException.class)
    public ResponseEntity<ErrorDetailDTO> handleDuplicateOrderException(final DuplicateOrderException ex) {
        log.error("DUPLICATE_ORDER_ERROR: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST,  ex.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorDetailDTO> handleOrderNotFoundException(final OrderNotFoundException ex) {
        log.error("ORDER_NOT_FOUND: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDetailDTO> handleBadRequest(final RuntimeException ex) {
        log.error("BAD_REQUEST: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDetailDTO> handleInternalError(final Exception ex) {
        log.error("INTERNAL_SERVER_ERROR: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetailDTO> handleRuntimeException(final RuntimeException ex) {
        log.error("INTERNAL_SERVER_ERROR: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ErrorDetailDTO> buildErrorResponse(final HttpStatus status, final String message) {
        final var errorDetail = createErrorDetail(String.valueOf(status.value()), message);
        return ResponseEntity.status(status).body(errorDetail);
    }

    private ErrorDetailDTO createErrorDetail(final String statusCode, final String message) {
        return ErrorDetailDTO.builder().statusCode(statusCode).message(message).build();
    }

}
