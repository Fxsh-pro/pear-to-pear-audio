package com.discord.mydiscord

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class SignalingHandler : TextWebSocketHandler() {

    private val roomSessions = mutableMapOf<String, MutableList<WebSocketSession>>() // Rooms map
    private val peerSessions = mutableMapOf<String, WebSocketSession>() // Store peer sessions by peerId

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val roomCode = session.uri?.query?.split("=")?.get(1)
        val peerId = session.id.toString() // Use session ID as peerId (or generate a unique one)

        if (roomCode != null) {
            roomSessions.computeIfAbsent(roomCode) { mutableListOf() }.add(session)
            peerSessions[peerId] = session
            println("User with peerId $peerId joined room $roomCode")
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val roomCode = session.uri?.query?.split("=")?.get(1)
        val data = ObjectMapper().readTree(message.payload)

        val peerId = data.get("peerId").asText()

        if (roomCode != null) {
            // Send message to all peers in the room except sender
            roomSessions[roomCode]?.forEach { peer ->
                if (peer.id != session.id) {
                    peer.sendMessage(message) // Forward message to other peers in the room
                }
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val roomCode = session.uri?.query?.split("=")?.get(1)
        val peerId = session.id.toString()

        if (roomCode != null) {
            roomSessions[roomCode]?.remove(session)
            peerSessions.remove(peerId)
            if (roomSessions[roomCode]?.isEmpty() == true) {
                roomSessions.remove(roomCode) // Remove empty rooms
            }
            println("User with peerId $peerId left room $roomCode")
        }
    }
}
