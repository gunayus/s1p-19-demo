package org.springmeetup.s1p19demo.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springmeetup.s1p19demo.model.Match;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

	@Value("${spring.redis.hostname}")
	private String hostname;

	@Value("${spring.redis.port}")
	private Integer port;

//	@Value("${spring.redis.password}")
//	String password;

	private final ObjectMapper objectMapper;

	@Bean
	public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
		RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration(hostname, port);
		//redisConfiguration.setPassword(password);

		return new LettuceConnectionFactory(redisConfiguration);
	}

	@Bean
	public ReactiveRedisTemplate<String, Match> matchReactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		RedisSerializationContext<String, Match> serializationContext = RedisSerializationContext
				.<String, Match>newSerializationContext(new StringRedisSerializer())
				.hashKey(new StringRedisSerializer())
				.hashValue(configureJackson2JsonRedisSerializer(Match.class))
				.build();

		return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}

	public <T> Jackson2JsonRedisSerializer<T> configureJackson2JsonRedisSerializer(Class<T> t) {
		Jackson2JsonRedisSerializer<T> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(t);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

		return jackson2JsonRedisSerializer;
	}

}
