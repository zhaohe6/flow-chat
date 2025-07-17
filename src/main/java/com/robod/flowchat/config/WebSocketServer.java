package com.robod.flowchat.config;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Component
@ServerEndpoint("/websocket")
public class WebSocketServer {
    private static ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        String userId = session.getId();
        sessionPool.put(userId, session);
        log.info("WebSocket connection opened for user: {} pool size:{}", userId, sessionPool.size());
        sendMessage(userId,"Welcome to FlowChat! Your session ID is: " + userId);
    }
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("Received message! session:{}, message:{}", session.getRequestParameterMap(), message);

    }
    @OnClose
    public void onClose(CloseReason closeReason,Session session) {
        String userId = session.getId();
        sessionPool.remove(userId);
        log.info("WebSocket connection closed for user: {} pool size:{}", userId, sessionPool.size());
    }
    @OnError
    public void onError(Throwable throwable) {
        log.error("WebSocket connection error for user: {} pool size:{}", sessionPool.size(), throwable.getMessage());
    }
    public void sendMessage(String userId, String message) {
        if (!sessionPool.containsKey(userId)) {
            log.warn("No WebSocket session found for user: {}", userId);
            return;
        }
        sessionPool.get(userId).getAsyncRemote().sendText(message);
        log.info("Sent message to user {}: {}", userId, message);
    }
}
