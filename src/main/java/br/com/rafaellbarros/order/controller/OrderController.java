package br.com.rafaellbarros.order.controller;

import br.com.rafaellbarros.order.controller.api.OrderApi;
import br.com.rafaellbarros.order.model.dto.OrderDTO;
import br.com.rafaellbarros.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @Override
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        final var orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<OrderDTO> processOrder(@Valid @RequestBody final OrderDTO order) {
        final var processedOrder = orderService.processOrder(order);
        return ResponseEntity.ok(processedOrder);
    }

    @Override
    public ResponseEntity<OrderDTO> getOrderByExternalId(@PathVariable final String externalId) {
        final var order = orderService.getOrderByExternalId(externalId);
        return ResponseEntity.ok(order);
    }
}
