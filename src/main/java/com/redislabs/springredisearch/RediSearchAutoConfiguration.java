package com.redislabs.springredisearch;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.redislabs.lettusearch.RediSearchClient;
import com.redislabs.lettusearch.StatefulRediSearchConnection;

import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.support.ConnectionPoolSupport;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RedisProperties.class)
public class RediSearchAutoConfiguration {

	@Bean(destroyMethod = "shutdown")
	ClientResources clientResources() {
		return DefaultClientResources.create();
	}

	@Bean(destroyMethod = "shutdown")
	RediSearchClient client(RedisProperties props, ClientResources clientResources) {
		RedisURI redisURI = RedisURI.create(props.getHost(), props.getPort());
		if (props.getPassword() != null) {
			redisURI.setPassword(props.getPassword());
		}
		Duration timeout = props.getTimeout();
		if (timeout != null) {
			redisURI.setTimeout(timeout);
		}
		return RediSearchClient.create(clientResources, redisURI);
	}

	@Bean(name = "rediSearchConnection", destroyMethod = "close")
	StatefulRediSearchConnection<String, String> connection(RediSearchClient rediSearchClient) {
		return rediSearchClient.connect();
	}

	@Bean(name = "rediSearchConnectionPool", destroyMethod = "close")
	GenericObjectPool<StatefulRediSearchConnection<String, String>> pool(RedisProperties props,
			RediSearchClient rediSearchClient) {
		GenericObjectPoolConfig<StatefulRediSearchConnection<String, String>> config = new GenericObjectPoolConfig<StatefulRediSearchConnection<String, String>>();
		config.setJmxEnabled(false);
		GenericObjectPool<StatefulRediSearchConnection<String, String>> pool = ConnectionPoolSupport
				.createGenericObjectPool(() -> rediSearchClient.connect(), config);
		Pool poolProps = props.getLettuce().getPool();
		if (poolProps != null) {
			pool.setMaxTotal(poolProps.getMaxActive());
			pool.setMaxIdle(poolProps.getMaxIdle());
			pool.setMinIdle(poolProps.getMinIdle());
			if (poolProps.getMaxWait() != null) {
				pool.setMaxWaitMillis(poolProps.getMaxWait().toMillis());
			}
		}
		return pool;
	}

}
