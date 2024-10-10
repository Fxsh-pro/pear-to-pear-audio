package com.discord.mydiscord

import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus

@Component
class SignalingHandler : TextWebSocketHandler() {

    private val roomSessions = mutableMapOf<String, MutableList<WebSocketSession>>() // Rooms map

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val roomCode = session.uri?.query?.split("=")?.get(1)
        if (roomCode != null) {
            roomSessions.computeIfAbsent(roomCode) { mutableListOf() }.add(session)
            println("User joined room $roomCode")
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val roomCode = session.uri?.query?.split("=")?.get(1)
        if (roomCode != null) {
            roomSessions[roomCode]?.forEach { peer ->
                if (peer.id != session.id) {
                    peer.sendMessage(message) // Forward message to other peers in the room
                }
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val roomCode = session.uri?.query?.split("=")?.get(1)
        roomSessions[roomCode]?.remove(session)
        if (roomSessions[roomCode]?.isEmpty() == true) {
            roomSessions.remove(roomCode) // Remove empty rooms
        }
        println("User left room $roomCode")
    }
}
