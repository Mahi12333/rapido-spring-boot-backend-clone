package com.maven.Rapido.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;


@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
   /* private final DriverLocationHandler handler;

    public WebSocketConfig(DriverLocationHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("Registering WebSocket handler for driver Location Updated");
        registry.addHandler(handler, "/ws/driver-location").setAllowedOrigins("*");
    }*/


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering STOMP endpoint for WebSocket");
        registry.addEndpoint("/socket") // client connects WebSocket here
                .setAllowedOriginPatterns("*");
               // .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // Prefix for messages FROM client
        registry.enableSimpleBroker("/topic", "/queue");    // Prefix for messages TO client
        registry.setUserDestinationPrefix("/user"); // For user-specific messages
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        log.info("configuring message converters for WebSocket, {}", messageConverters);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        messageConverters.add(converter);
        return true;
    }

}
