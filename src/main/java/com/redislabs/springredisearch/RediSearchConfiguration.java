package com.redislabs.springredisearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.redisearch.client.Client;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "redisearch")
@Data
public class RediSearchConfiguration {

	private static final int DEFAULT_TIMEOUT = 1000;
	private static final int DEFAULT_POOLSIZE = 1;

	@Autowired
	private RedisProperties redisProps;
	private String host;
	private Integer port;

	public String getIndexName(String id) {
		return id + "Idx";
	}

	public String getSuggestIndexName(String id) {
		return id + "SuggestIdx";
	}

	public Client getClient(String index) {
		return new Client(index, getHost(), getPort(), getTimeout(), getPoolSize());
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

	public Client getSearchClient(String id) {
		return getClient(getIndexName(id));
	}

	public Client getSuggestClient(String id) {
		return getClient(getSuggestIndexName(id));
	}

}
