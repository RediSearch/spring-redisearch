package com.redislabs.springredisearch;

import com.redislabs.lettusearch.RediSearchClient;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RedisProperties.class)
public class RediSearchAutoConfiguration {

    @Bean
    RedisURI redisURI(RedisProperties properties) {
        RedisProperties.Sentinel sentinel = properties.getSentinel();

        RedisURI redisURI = null;

        if (sentinel != null) {
            //Since nodes may contain a port, delete it just in case.
            String firstNode = sentinel.getNodes().get(0).replaceAll(":.*", "");
            RedisURI.Builder builder = RedisURI.Builder.sentinel(firstNode, properties.getPort(), sentinel.getMaster());

            for (int i = 1; i < sentinel.getNodes().size(); i++) {
                String node = sentinel.getNodes().get(i).replaceAll(":.*", "");
                builder = builder.withSentinel(node);
            }

            redisURI = builder.build();
        } else {
            redisURI = RedisURI.create(properties.getHost(), properties.getPort());
        }

        if (properties.getPassword() != null) {
            redisURI.setPassword(properties.getPassword().toCharArray());
        }

        Duration timeout = properties.getTimeout();
        if (timeout != null) {
            redisURI.setTimeout(timeout);
        }
        return redisURI;
    }

    @Bean(destroyMethod = "shutdown")
    ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean(destroyMethod = "shutdown")
    RediSearchClient client(RedisURI redisURI, ClientResources clientResources) {
        return RediSearchClient.create(clientResources, redisURI);
    }

    @Bean(name = "rediSearchConnection", destroyMethod = "close")
    StatefulRediSearchConnection<String, String> connection(RediSearchClient rediSearchClient) {
        return rediSearchClient.connect();
    }

    @Bean(name = "rediSearchConnectionPoolConfig")
    GenericObjectPoolConfig<StatefulRediSearchConnection<String, String>> poolConfig(RedisProperties redisProperties) {
        return configure(redisProperties, new GenericObjectPoolConfig<>());
    }

    public <K, V> GenericObjectPoolConfig<StatefulRediSearchConnection<K, V>> configure(RedisProperties redisProperties,
                                                                                        GenericObjectPoolConfig<StatefulRediSearchConnection<K, V>> config) {
        config.setJmxEnabled(false);
        Pool poolProps = redisProperties.getLettuce().getPool();
        if (poolProps != null) {
            config.setMaxTotal(poolProps.getMaxActive());
            config.setMaxIdle(poolProps.getMaxIdle());
            config.setMinIdle(poolProps.getMinIdle());
            if (poolProps.getMaxWait() != null) {
                config.setMaxWaitMillis(poolProps.getMaxWait().toMillis());
            }
        }
        return config;
    }

    @Bean(name = "rediSearchConnectionPool", destroyMethod = "close")
    GenericObjectPool<StatefulRediSearchConnection<String, String>> pool(
            GenericObjectPoolConfig<StatefulRediSearchConnection<String, String>> config, RediSearchClient client) {
        return ConnectionPoolSupport.createGenericObjectPool(client::connect, config);
    }

}