package com.discord.mydiscord

import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.stereotype.Component

@Component
class SignalingHandler : TextWebSocketHandler() {
    var LOG = LoggerFactory.getLogger(SignalingHandler::class.java)

    private val sessions = mutableMapOf<String, WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        LOG.info("new session $session")
        sessions[session.id] = session // Register a new session
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        LOG.info("handleTextMessage $message")
        // Forward SDP/ICE messages to the other peer
        sessions.values.forEach { peer ->
            if (peer.id != session.id) {
                peer.sendMessage(message) // Send message to the other peer
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: org.springframework.web.socket.CloseStatus) {
        LOG.info("afterConnectionClosed $status")

        sessions.remove(session.id) // Remove session on disconnect
    }
}
