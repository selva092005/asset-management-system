package com.learn.demo.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    // Thread-safe map of active user emails to their WebSocket sessions (multiple sessions allowed per email)
    private static final Map<String, List<WebSocketSession>> sessionsMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String email = getEmailFromSession(session);
        if (email != null) {
            String key = email.toLowerCase().trim();
            sessionsMap.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(session);
            log.info("WebSocket connection established for user: {}", key);
        } else {
            log.warn("WebSocket connection attempted without email query parameter");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String email = getEmailFromSession(session);
        if (email != null) {
            String key = email.toLowerCase().trim();
            List<WebSocketSession> sessions = sessionsMap.get(key);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    sessionsMap.remove(key);
                }
            }
            log.info("WebSocket connection closed for user: {}", key);
        }
    }

    /**
     * Sends a real-time notification text message to all active sessions of a user.
     */
    public void sendNotificationToUser(String email, String message) {
        if (email == null) return;
        String key = email.toLowerCase().trim();
        List<WebSocketSession> sessions = sessionsMap.get(key);
        if (sessions != null && !sessions.isEmpty()) {
            TextMessage textMessage = new TextMessage(message);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                        log.info("WebSocket notification successfully pushed to {}", key);
                    } catch (IOException e) {
                        log.error("Failed to send WebSocket message to user: {}", key, e);
                    }
                }
            }
        }
    }

    private String getEmailFromSession(WebSocketSession session) {
        if (session.getUri() == null) return null;
        String query = session.getUri().getQuery();
        if (query != null && query.contains("email=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("email=")) {
                    return param.substring(6);
                }
            }
        }
        return null;
    }
}
