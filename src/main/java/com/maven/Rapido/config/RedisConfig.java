package com.maven.Rapido.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.maven.Rapido.payload.request.driver.DriverLocationDTO;
import com.maven.Rapido.payload.request.radis.RadisDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Slf4j
@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.password}")
    private String redisPassword;

    //! This is For Local
    /*@Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        log.info("Created RedisConnectionFactory bean");
        return new LettuceConnectionFactory("localhost", 6379);
    }*/

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        config.setPassword(RedisPassword.of(redisPassword));

        // Enable SSL
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()
                .build();

        log.info("Created RedisConnectionFactory bean for host: {} port: {}", redisHost, redisPort);
        return new LettuceConnectionFactory(config, clientConfig);
    }

     @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("Creating RedisTemplate bean with connection factory: {}", connectionFactory);
        /*
      // RedisTemplate<String, DriverLocationDTO> template = new RedisTemplate<>();
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        log.info("create Redis Template----{}", template);
        return template;*/

         RedisTemplate<String, Object> template = new RedisTemplate<>();
         template.setConnectionFactory(connectionFactory);
         template.setKeySerializer(new StringRedisSerializer());


         // Use GenericJackson2JsonRedisSerializer for value serialization (handles JSON)
         template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

         template.setHashKeySerializer(new StringRedisSerializer());
         template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

         template.afterPropertiesSet();
         return template;
    }

    /*@Bean
    public RedisTemplate<String, RadisDTO> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, RadisDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);

        // Use the custom serializer with the configured ObjectMapper
        CustomJackson2JsonRedisSerializer<RadisDTO> serializer =
                new CustomJackson2JsonRedisSerializer<>(RadisDTO.class, objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }*/


}
