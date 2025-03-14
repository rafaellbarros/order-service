package br.com.rafaellbarros.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Objects;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        log.info("RedisCacheConfiguration.redisConnectionFactory() - Starting...");
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        validateConnection(factory, "Standalone");
        log.info("RedisCacheConfiguration.redisConnectionFactory() - Returning - configuration");
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // Configurar serializadores para chave/valor
        // template.setKeySerializer(new StringRedisSerializer());
        // template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer();
        template.setDefaultSerializer(serializer);

        return template;
    }

    private void validateConnection(final LettuceConnectionFactory factory, final String type) {
        try {
            factory.afterPropertiesSet();
            StringRedisTemplate template = new StringRedisTemplate(factory);
            String pingResponse = Objects.requireNonNull(template.getConnectionFactory()).getConnection().ping();
            log.info("Redis {} connection validated successfully. Response: {}", type, pingResponse);
        } catch (final Exception e) {
            log.error("Redis {} connection validation failed!", type, e);
        }
    }
}
