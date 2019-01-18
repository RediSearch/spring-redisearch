package com.redislabs.springredisearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.redislabs.lettusearch.RediSearchClient;

import io.lettuce.core.RedisURI;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "redisearch")
@Data
public class RediSearchConfiguration {

	@Autowired
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private RedisProperties props;
	private String host;
	private Integer port;
	private String password;

	@Bean
	public RediSearchClient getClient() {
		RedisURI redisURI = RedisURI.create(host == null ? props.getHost() : host,
				port == null ? props.getPort() : port);
		if (password != null || props.getPassword() != null) {
			redisURI.setPassword(password == null ? props.getPassword() : password);
		}
		if (props.getTimeout() != null) {
			redisURI.setTimeout(props.getTimeout());
		}
		return RediSearchClient.create(redisURI);
	}

}
