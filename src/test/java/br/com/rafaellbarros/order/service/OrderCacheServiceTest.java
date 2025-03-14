package br.com.rafaellbarros.order.service;

import br.com.rafaellbarros.order.model.dto.OrderDTO;
import br.com.rafaellbarros.order.model.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class OrderCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private OrderCacheService orderCacheService;

    @Test
    void shouldCacheOrderSuccessfully() {
        // Arrange
        Long orderId = 1L;
        String externalId = "external-123";
        OrderDTO order = new OrderDTO(
                orderId,
                externalId,
                "PROCESSED",
                new BigDecimal("100.00"),
                List.of(new ProductDTO(1L, null, "Product A", new BigDecimal("50.00"), 2)),
                LocalDateTime.now()
        );
        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        orderCacheService.cacheOrder(order);

        // Assert
        Mockito.verify(valueOperations).set("order_" + externalId, order);
    }

    @Test
    void shouldLogErrorWhenCacheOrderFails() {
        // Arrange
        OrderDTO order = new OrderDTO();
        Mockito.when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis error"));

        // Act
        orderCacheService.cacheOrder(order);

        // Assert
        // Verifica apenas que o método não lançou uma exceção
        Mockito.verify(redisTemplate).opsForValue();
    }

    @Test
    void shouldReturnTrueWhenOrderIsCached() {
        // Arrange
        String externalId = "external-456";
        Mockito.when(redisTemplate.hasKey("order_" + externalId)).thenReturn(true);

        // Act
        boolean isCached = orderCacheService.isOrderCached(externalId);

        // Assert
        assertTrue(isCached);
        Mockito.verify(redisTemplate).hasKey("order_" + externalId);
    }

    @Test
    void shouldReturnFalseWhenOrderIsNotCached() {
        // Arrange
        String externalId = "external-456";
        Mockito.when(redisTemplate.hasKey("order_" + externalId)).thenReturn(false);

        // Act
        boolean isCached = orderCacheService.isOrderCached(externalId);

        // Assert
        assertFalse(isCached);
        Mockito.verify(redisTemplate).hasKey("order_" + externalId);
    }

    @Test
    void shouldLogErrorWhenCheckingIfOrderIsCachedFails() {
        // Arrange
        String externalId = "external-789";
        Mockito.when(redisTemplate.hasKey("order_" + externalId)).thenThrow(new RuntimeException("Redis error"));

        // Act
        boolean isCached = orderCacheService.isOrderCached(externalId);

        // Assert
        assertFalse(isCached);
    }

    @Test
    void shouldReturnOrderWhenCachedOrderExists() {
        // Arrange
        Long orderId = 1L;
        String externalId = "external-123";
        OrderDTO cachedOrder = new OrderDTO(
                orderId,
                externalId,
                "PROCESSED",
                new BigDecimal("100.00"),
                List.of(new ProductDTO(1L, null, "Product A", new BigDecimal("50.00"), 2)),
                LocalDateTime.now()
        );

        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.get("order_" + externalId)).thenReturn(cachedOrder);

        // Act
        OrderDTO result = orderCacheService.getCachedOrder(externalId);

        // Assert
        assertNotNull(result);
        assertEquals(cachedOrder, result);
    }

    @Test
    void shouldReturnNullWhenCachedOrderDoesNotExist() {
        // Arrange
        String externalId = "external-123";
        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.get("order_" + externalId)).thenReturn(null);

        // Act
        OrderDTO result = orderCacheService.getCachedOrder(externalId);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldLogErrorWhenGettingCachedOrderFails() {
        // Arrange
        String externalId = "external-123";
        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.get("order_" + externalId)).thenThrow(new RuntimeException("Redis error"));

        // Act
        OrderDTO result = orderCacheService.getCachedOrder(externalId);

        // Assert
        assertNull(result);
    }
}
