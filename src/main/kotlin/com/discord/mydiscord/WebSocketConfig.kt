package com.discord.mydiscord

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {

    init {
        println("WebSocketConfig initialized")  // Debug log to verify if this is getting initialized
        //    const signalingServerUrl = 'ws://192.168.1.31:8080/signal';
    }


    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(SignalingHandler(), "/signal")
            .setAllowedOrigins("*")
    }
}