# Spring RediSearch
Spring RediSearch provides access to RediSearch from Spring applications

## Usage
Add Spring RediSearch to your application dependencies, e.g. with Maven:
```
<dependency>
    <groupId>com.redislabs</groupId>
    <artifactId>spring-redisearch</artifactId>
    <version>1.0.8</version>
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