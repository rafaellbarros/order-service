package br.com.rafaellbarros.order.controller.api;

import br.com.rafaellbarros.order.model.dto.ErrorDetailDTO;
import br.com.rafaellbarros.order.model.dto.OrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static br.com.rafaellbarros.order.controller.api.DocApiConstants.BAD_REQUEST_CODE;
import static br.com.rafaellbarros.order.controller.api.DocApiConstants.BAD_REQUEST_DESCRIPTION;
import static br.com.rafaellbarros.order.controller.api.DocApiConstants.INTERNAL_SERVER_ERROR_CODE;
import static br.com.rafaellbarros.order.controller.api.DocApiConstants.INTERNAL_SERVER_ERROR_DESCRIPTION;
import static br.com.rafaellbarros.order.controller.api.DocApiConstants.NOT_FOUND_CODE;
import static br.com.rafaellbarros.order.controller.api.DocApiConstants.NOT_FOUND_DESCRIPTION;
import static br.com.rafaellbarros.order.controller.api.DocApiConstants.OK_CODE;

@Tag(name = "Order Api")
@RequestMapping("/orders")
public interface OrderApi {

    @Operation(
            summary = "Get all orders",
            description = "Get all orders.")
    @ApiResponse(responseCode = OK_CODE, description = "Get all orders.",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class) )})
    @ApiResponse(responseCode = BAD_REQUEST_CODE, description = BAD_REQUEST_DESCRIPTION,
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailDTO.class) )})
    @ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION,
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailDTO.class) )})
    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION,
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailDTO.class) )})
    @GetMapping
    ResponseEntity<List<OrderDTO>> getAllOrders();

    @Operation(
            summary = "Process order",
            description = "Process order")
    @ApiResponse(responseCode = OK_CODE, description = "Order process.",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class) )})
    @ApiResponse(responseCode = BAD_REQUEST_CODE, description = BAD_REQUEST_DESCRIPTION,
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailDTO.class) )})
    @ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION,
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailDTO.class) )})
    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION,
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailDTO.class) )})
    @PostMapping
    ResponseEntity<OrderDTO> processOrder(@Valid @RequestBody final OrderDTO order);

    @Operation(
            summary = "Get order",
            description = "Get order by externalId")
    @ApiResponse(responseCode = OK_CODE, description = "Get order by externalId.",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class) )})
    @ApiResponse(responseCode = BAD_REQUEST_CODE, description = BAD_REQUEST_DESCRIPTION,
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailDTO.class) )})
    @ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION,
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailDTO.class) )})
    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION,
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailDTO.class) )})
    @GetMapping("/{externalId}")
    ResponseEntity<OrderDTO> getOrderByExternalId(@PathVariable final String externalId);
}
