package br.com.rafaellbarros.order.service;

import br.com.rafaellbarros.order.model.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "order_";

    public void cacheOrder(final OrderDTO order) {

        final var externalId = order.getExternalId();
        final var cacheKey = generateCacheKey(order.getExternalId());
        try {
            log.info("Caching order with externalId: {}", externalId);
            redisTemplate.opsForValue().set(cacheKey, order);
        } catch (Exception e) {
            log.error("Error while storing the order in cache with externalId: {}", externalId, e);
        }
    }

    public boolean isOrderCached(final String externalId) {
        String cacheKey = generateCacheKey(externalId);
        try {
            final var isCached = Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey));
            log.debug("Order with externalId {} {} in cache (Key: {})", externalId, isCached ? "found" : "not found", cacheKey);
            return isCached;
        } catch (final Exception e) {
            log.error("Error while checking if the order is cached (Key: {}):", cacheKey, e);
            return false;
        }
    }

    @Cacheable(value = "orders", key = "#externalId", unless = "#result == null")
    public OrderDTO getCachedOrder(final String externalId) {
        final var cacheKey = generateCacheKey(externalId);
        try {
            log.info("Fetching order from cache with Key: {}", cacheKey);
            var order = (OrderDTO) redisTemplate.opsForValue().get(cacheKey);
            log.info("Order found in cache for externalId: {}", externalId);
            return order;
        } catch (final Exception e) {
            log.error("Error while fetching the order from cache with externalId: {}", externalId, e);
            return null;
        }
    }

    private String generateCacheKey(final String externalId) {
        return CACHE_KEY_PREFIX + externalId;
    }
}
