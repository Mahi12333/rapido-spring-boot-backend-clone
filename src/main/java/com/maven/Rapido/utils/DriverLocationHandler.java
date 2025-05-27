//package com.maven.Rapido.utils;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.maven.Rapido.payload.request.driver.DriverLocationDTO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//public class DriverLocationHandler extends TextWebSocketHandler {
//
//    private final RedisTemplate<String, DriverLocationDTO> redisTemplate;
//
//    public DriverLocationHandler(RedisTemplate<String, DriverLocationDTO> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//        log.info("Received message: {}", message.getPayload());
//        String json = message.getPayload();
//        ObjectMapper mapper = new ObjectMapper();
//        DriverLocationDTO location = mapper.readValue(json, DriverLocationDTO.class);
//
//        redisTemplate.opsForValue().set("driver:" + location.getDriverId(), location);
//    }
//}
