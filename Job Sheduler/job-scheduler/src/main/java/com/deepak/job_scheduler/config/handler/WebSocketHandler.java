package com.deepak.job_scheduler.config.handler;

import com.deepak.job_scheduler.model.Job;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        //
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
    }

    public void broadcastJobUpdate(Job job) {
        TextMessage message = new TextMessage(
                String.format("{\"name\":\"%s\", \"status\":\"%s\", \"duration\":%d}", job.getName(), job.getStatus(), job.getDuration())
        );

        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
