//package com.maven.Rapido.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//
//public class CustomJackson2JsonRedisSerializer<T> extends Jackson2JsonRedisSerializer<T> {
//    public CustomJackson2JsonRedisSerializer(Class<T> type, ObjectMapper objectMapper) {
//        super(type);
//        // Deprecated method call encapsulated here, sirf ek baar
//        super.setObjectMapper(objectMapper);
//    }
//}
