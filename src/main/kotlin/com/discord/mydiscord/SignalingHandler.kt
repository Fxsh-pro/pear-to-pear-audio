package com.discord.mydiscord

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus

@Component
class SignalingHandler : TextWebSocketHandler() {

    private val sessions = mutableMapOf<String, WebSocketSession>()
    private val mapper = jacksonObjectMapper()

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val signal = mapper.readValue(message.payload, SignalMessage::class.java)
        when (signal.type) {
            "offer" -> broadcast(signal)
            "answer" -> broadcast(signal)
            "iceCandidate" -> broadcast(signal)
        }
    }

    private fun broadcast(signal: SignalMessage) {
        sessions.values.forEach {
            it.sendMessage(TextMessage(mapper.writeValueAsString(signal)))
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions[session.id] = session
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session.id)
    }
}

data class SignalMessage(
    val type: String,
    val offer: Any? = null,
    val answer: Any? = null,
    val candidate: Any? = null,
    val userName: String
)
