package AndreFranco.LocationService.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import AndreFranco.LocationService.dto.LocationMetricsDto;
import AndreFranco.LocationService.dto.LocationSummaryDto;
import AndreFranco.LocationService.dto.TopEntryDto;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.json.JsonMapper;

@Configuration
public class RedisConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${cache.ttl-hours:24}")
    private long ttlHours;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        JsonMapper mapper = JsonMapper.builder().build();
        Duration ttl = Duration.ofHours(ttlHours);

        // cada cache com o seu serializer tipado: sem @class, sem default typing,
        // write/read simétricos por construção.
        RedisCacheConfiguration locationsCfg        = configFor(mapper, listType(mapper, LocationSummaryDto.class), ttl);
        RedisCacheConfiguration locationSummaryCfg  = configFor(mapper, mapper.constructType(LocationSummaryDto.class), ttl);
        RedisCacheConfiguration locationMetricsCfg  = configFor(mapper, mapper.constructType(LocationMetricsDto.class), ttl);
        RedisCacheConfiguration metricsListCfg      = configFor(mapper, listType(mapper, LocationMetricsDto.class), ttl);
        RedisCacheConfiguration topCfg              = configFor(mapper, listType(mapper, TopEntryDto.class), ttl);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(locationsCfg) // fallback razoável
                .withCacheConfiguration("locations",         locationsCfg)
                .withCacheConfiguration("location-summary",  locationSummaryCfg)
                .withCacheConfiguration("location-by-id",    locationMetricsCfg)
                .withCacheConfiguration("location-by-code",  locationMetricsCfg)
                .withCacheConfiguration("locations-compare", metricsListCfg)
                .withCacheConfiguration("locations-top",     topCfg)
                .build();
    }

    private RedisCacheConfiguration configFor(JsonMapper mapper, JavaType type, Duration ttl) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(mapper, type);
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    private JavaType listType(JsonMapper mapper, Class<?> element) {
        return mapper.getTypeFactory().constructCollectionType(java.util.List.class, element);
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) {
                log.error("Cache PUT error cache={} key={}: {}", cache.getName(), key, ex.toString(), ex);
            }
            @Override
            public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) {
                log.error("Cache GET error cache={} key={}: {}", cache.getName(), key, ex.toString(), ex);
            }
            @Override
            public void handleCacheEvictError(RuntimeException ex, Cache cache, Object key) {
                log.error("Cache EVICT error cache={} key={}: {}", cache.getName(), key, ex.toString(), ex);
            }
            @Override
            public void handleCacheClearError(RuntimeException ex, Cache cache) {
                log.error("Cache CLEAR error cache={}: {}", cache.getName(), ex.toString(), ex);
            }
        };
    }
}