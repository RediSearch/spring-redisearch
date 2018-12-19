package com.redislabs.springredisearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.redisearch.client.Client;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "redisearch")
@Data
public class RediSearchConfiguration {

	private static final int DEFAULT_TIMEOUT = 1000;
	private static final int DEFAULT_POOLSIZE = 1;

	@Autowired
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private RedisProperties redisProps;
	private String host;
	private Integer port;
	private String password;

	public Client getClient(String index) {
		return new Client(index, getHost(), getPort(), getTimeout(), getPoolSize(), getPassword());
	}

	private String getPassword() {
		if (password == null) {
			return redisProps.getPassword();
		}
		return password;
	}

	private int getPort() {
		if (port == null) {
			return redisProps.getPort();
		}
		return port;
	}

	private String getHost() {
		if (host == null) {
			return redisProps.getHost();
		}
		return host;
	}

	private int getPoolSize() {
		if (redisProps.getJedis().getPool() == null) {
			return DEFAULT_POOLSIZE;
		}
		return redisProps.getJedis().getPool().getMaxActive();
	}

	private int getTimeout() {
		if (redisProps.getTimeout() == null) {
			return DEFAULT_TIMEOUT;
		}
		return (int) redisProps.getTimeout().toMillis();
	}

}
