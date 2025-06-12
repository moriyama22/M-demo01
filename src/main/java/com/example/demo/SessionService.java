package com.example.demo;

import com.example.demo.entity.Session;
import com.example.demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    // セッションを保存する
    public void saveSession(String sessionId, String data, LocalDateTime expiresAt) {
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setData(data);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(expiresAt);
        sessionRepository.save(session);
    }

    // セッションを取得する
    public Optional<Session> getSession(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }

    // セッションを削除する
    public void deleteSession(String sessionId) {
        sessionRepository.deleteById(sessionId);
    }
}