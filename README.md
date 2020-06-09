[![license](https://img.shields.io/github/license/RediSearch/spring-redisearch.svg)](https://github.com/RediSearch/spring-redisearch)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.redislabs/spring-redisearch/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.redislabs/spring-redisearch)
[![Javadocs](https://www.javadoc.io/badge/com.redislabs/spring-redisearch.svg)](https://www.javadoc.io/doc/com.redislabs/spring-redisearch)
[![CircleCI](https://circleci.com/gh/RediSearch/spring-redisearch/tree/master.svg?style=svg)](https://circleci.com/gh/RediSearch/spring-redisearch/tree/master)
[![GitHub issues](https://img.shields.io/github/release/RediSearch/spring-redisearch.svg)](https://github.com/RediSearch/spring-redisearch/releases/latest)


# Spring RediSearch
[![Forum](https://img.shields.io/badge/Forum-RediSearch-blue)](https://forum.redislabs.com/c/modules/redisearch/)
[![Gitter](https://badges.gitter.im/RedisLabs/RediSearch.svg)](https://gitter.im/RedisLabs/RediSearch?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

Spring RediSearch provides access to RediSearch from Spring applications

## Usage
Add Spring RediSearch to your application dependencies, e.g. with Maven:
```
<dependency>
    <groupId>com.redislabs</groupId>
    <artifactId>spring-redisearch</artifactId>
    <version>1.1.1</version>
</dependency>
```

Inject and use RediSearchClient or StatefulRediSearchConnection:
```java
public class Example {

	@Autowired
	StatefulRediSearchConnection<String, String> connection;

	public void testSearch() {
		connection.sync().search("myIndex", "Lalo Schifrin", SearchOptions.builder().build());
	}

}
```
