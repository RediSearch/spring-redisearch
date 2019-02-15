package com.redislabs.springredisearch;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.redislabs.lettusearch.RediSearchClient;
import com.redislabs.lettusearch.StatefulRediSearchConnection;

import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "spring.redisearch")
@Data
public class RediSearchConfiguration {

	@Autowired
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private RedisProperties props;
	private String host;
	private Integer port;
	private String password;
	private Duration timeout;
	private Pool pool;

	@Bean(destroyMethod = "shutdown")
	ClientResources clientResources() {
		return DefaultClientResources.create();
	}

	@Bean(destroyMethod = "shutdown")
	RediSearchClient client(ClientResources clientResources) {
		RedisURI redisURI = RedisURI.create(host(), port());
		String password = password();
		if (password != null) {
			redisURI.setPassword(password);
		}
		Duration timeout = timeout();
		if (timeout != null) {
			redisURI.setTimeout(timeout);
		}
		return RediSearchClient.create(clientResources, redisURI);
	}

	@Bean(destroyMethod = "close")
	StatefulRediSearchConnection<String, String> connection(RediSearchClient rediSearchClient) {
		return rediSearchClient.connect();
	}

	@Bean(name = "rediSearchConnectionPool", destroyMethod = "close")
	GenericObjectPool<StatefulRediSearchConnection<String, String>> rediSearchConnectionPool(
			RediSearchClient rediSearchClient) {
		GenericObjectPool<StatefulRediSearchConnection<String, String>> pool = ConnectionPoolSupport
				.createGenericObjectPool(() -> rediSearchClient.connect(),
						new GenericObjectPoolConfig<StatefulRediSearchConnection<String, String>>());
		Pool poolProps = pool();
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

	private Pool pool() {
		if (pool == null) {
			return props.getLettuce().getPool();
		}
		return pool;
	}

	private String host() {
		if (host == null) {
			return props.getHost();
		}
		return host;
	}

	private int port() {
		if (port == null) {
			return props.getPort();
		}
		return port;
	}

	private String password() {
		if (password == null) {
			return props.getPassword();
		}
		return password;
	}

	private Duration timeout() {
		if (timeout == null) {
			return props.getTimeout();
		}
		return timeout;
	}
}
