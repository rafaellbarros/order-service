package br.com.rafaellbarros.order.service;

import br.com.rafaellbarros.order.exception.DuplicateOrderException;
import br.com.rafaellbarros.order.model.dto.OrderDTO;
import br.com.rafaellbarros.order.model.dto.ProductDTO;
import br.com.rafaellbarros.order.model.entity.Order;
import br.com.rafaellbarros.order.model.mapper.OrderMapper;
import br.com.rafaellbarros.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderServiceTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final OrderCacheService orderCacheService = Mockito.mock(OrderCacheService.class);
    private final OrderMapper orderMapper = Mockito.mock(OrderMapper.class);
    private final OrderService orderService = new OrderService(orderRepository, orderCacheService, orderMapper);

    @Test
    void shouldReturnOrderFromCache() {
        // Arrange
        Long orderId = 1L;
        String externalId = "external-123";
        OrderDTO cachedOrderDTO = new OrderDTO(
                orderId,
                externalId,
                "PROCESSED",
                new BigDecimal("100.00"),
                List.of(new ProductDTO(1L, null, "Product A", new BigDecimal("50.00"), 2)),
                LocalDateTime.now()
        );

        Mockito.when(orderCacheService.getCachedOrder(externalId)).thenReturn(cachedOrderDTO);

        // Act
        OrderDTO orderDTO = orderService.getOrderByExternalId(externalId);

        // Assert
        assertEquals(cachedOrderDTO, orderDTO);
    }

    @Test
    void shouldReturnOrderFromDatabaseWhenNotInCache() {
        // Arrange
        Long orderId = 1L;
        String externalId = "external-456";
        Order dbOrder = new Order();
        dbOrder.setId(orderId);
        dbOrder.setStatus("PROCESSED");

        OrderDTO orderDTO = new OrderDTO(
                orderId,externalId,
                "PROCESSED",
                new BigDecimal("200.00"),
                List.of(new ProductDTO(2L, null, "Product B", new BigDecimal("100.00"), 2)),
                LocalDateTime.now()
        );

        Mockito.when(orderCacheService.getCachedOrder(externalId)).thenReturn(null);
        Mockito.when(orderRepository.findByExternalId(externalId)).thenReturn(Optional.of(dbOrder));
        Mockito.when(orderMapper.toDTO(dbOrder)).thenReturn(orderDTO);

        // Act
        OrderDTO resultDTO = orderService.getOrderByExternalId(externalId);

        // Assert
        assertEquals(orderDTO, resultDTO);
    }

    @Test
    void shouldThrowExceptionWhenOrderExternalIdNotFound() {
        // Arrange
        String externalId = "external-456";

        Mockito.when(orderCacheService.getCachedOrder(externalId)).thenReturn(null);
        Mockito.when(orderRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.getOrderByExternalId(externalId));
    }

    @Test
    void shouldProcessOrderSuccessfully() {
        // Arrange
        Long orderId = 1L;
        String externalId = "external-789";
        ProductDTO product1 = new ProductDTO(null, null, "Product 1", new BigDecimal("10.00"), 2);
        ProductDTO product2 = new ProductDTO(null, null, "Product 2", new BigDecimal("5.00"), 1);
        OrderDTO inputOrderDTO = new OrderDTO(
                orderId,
                externalId,
                null,
                null,
                List.of(product1, product2),
                LocalDateTime.now()
        );

        BigDecimal expectedTotalValue = new BigDecimal("25.00");
        Order inputOrderEntity = new Order();
        Order savedOrderEntity = new Order();
        OrderDTO savedOrderDTO = new OrderDTO(
                orderId,
                externalId,
                "PROCESSED",
                expectedTotalValue,
                List.of(product1, product2),
                LocalDateTime.now()
        );

        Mockito.when(orderCacheService.isOrderCached(externalId)).thenReturn(false);
        Mockito.when(orderMapper.toEntity(inputOrderDTO)).thenReturn(inputOrderEntity);
        Mockito.when(orderRepository.save(inputOrderEntity)).thenReturn(savedOrderEntity);
        Mockito.when(orderMapper.toDTO(savedOrderEntity)).thenReturn(savedOrderDTO);

        // Act
        OrderDTO resultDTO = orderService.processOrder(inputOrderDTO);

        // Assert
        assertEquals(expectedTotalValue, resultDTO.getTotalValue());
        assertEquals("PROCESSED", resultDTO.getStatus());
        Mockito.verify(orderCacheService).cacheOrder(savedOrderDTO);

    }

    @Test
    void shouldThrowDuplicateOrderExceptionWhenOrderIsCached() {
        // Arrange
        Long orderId = 1L;
        String externalId = "external-101";
        OrderDTO inputOrderDTO = new OrderDTO(
                orderId,
                externalId,
                null,
                null,
                List.of(),
                LocalDateTime.now()
        );

        Mockito.when(orderCacheService.isOrderCached(externalId)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateOrderException.class, () -> orderService.processOrder(inputOrderDTO));
    }
}
