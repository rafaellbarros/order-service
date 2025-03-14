package br.com.rafaellbarros.order.service;

import br.com.rafaellbarros.order.exception.DuplicateOrderException;
import br.com.rafaellbarros.order.exception.OrderNotFoundException;
import br.com.rafaellbarros.order.model.dto.OrderDTO;
import br.com.rafaellbarros.order.model.entity.Order;
import br.com.rafaellbarros.order.model.mapper.OrderMapper;
import br.com.rafaellbarros.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderCacheService orderCacheService;
    private final OrderMapper orderMapper;

    public List<OrderDTO> getAllOrders() {
        final var orders = orderRepository.findAll();
        return orderMapper.toDTO(orders);
    }

    public OrderDTO processOrder(final OrderDTO order) {

        validateOrder(order);

        calculateTotalValue(order);

        order.setStatus("PROCESSED");

        Order savedOrder = saveOrder(order);

        OrderDTO savedOrderDTO = orderMapper.toDTO(savedOrder);

        cacheOrder(savedOrderDTO);

        return savedOrderDTO;
    }

    public OrderDTO getOrderByExternalId(final String externalId) {
        OrderDTO cachedOrder = fetchFromCache(externalId);

        if (cachedOrder != null) {
            return cachedOrder;
        }

        OrderDTO orderDTO = fetchFromDatabase(externalId);

        cacheOrder(orderDTO);

        return orderDTO;
    }

    private void validateOrder(final OrderDTO order) {
        if (orderCacheService.isOrderCached(order.getExternalId())) {
            throw new DuplicateOrderException("Order already processed.");
        }
    }

    private void calculateTotalValue(final OrderDTO order) {
        BigDecimal totalValue = order.getProducts().stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalValue(totalValue);
    }

    private Order saveOrder(final OrderDTO order) {
        Order orderEntity = orderMapper.toEntity(order);

        orderEntity.getProducts().forEach(product -> product.setOrder(orderEntity));

        return orderRepository.save(orderEntity);
    }

    private void cacheOrder(final OrderDTO orderDTO) {
        orderCacheService.cacheOrder(orderDTO);
    }

    private OrderDTO fetchFromCache(final String externalId) {
        return orderCacheService.getCachedOrder(externalId);
    }

    private OrderDTO fetchFromDatabase(final String externalId) {
        Order order = orderRepository.findByExternalId(externalId)
                .orElseThrow(() -> new OrderNotFoundException("Order with externalId '" + externalId + "' not found."));
        return orderMapper.toDTO(order);
    }
}
