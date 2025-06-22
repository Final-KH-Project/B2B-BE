package kh.gangnam.b2b.config.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//@Component
public class WebSocketSessionManager {

    // 사용자ID -> 세션ID 저장소 (한 명당 하나의 세션만)
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    // 세션 추가
    public void addSession(String loginId, String sessionId) {
        userSessionMap.put(loginId, sessionId);
        System.out.println("세션 추가됨: " + loginId + " -> " + sessionId);
    }

    // 세션 제거
    public void removeSession(String sessionId) {
        // 세션ID로 사용자 찾아서 제거
        userSessionMap.values().removeIf(id -> id.equals(sessionId));
        System.out.println("세션 제거됨: " + sessionId);
    }

    // 사용자의 세션ID 가져오기
    public String getSessionId(String loginId) {
        return userSessionMap.get(loginId);
    }

    // 사용자가 연결되어 있는지 확인
    public boolean isConnected(String loginId) {
        return userSessionMap.containsKey(loginId);
    }

    // 연결된 모든 사용자 목록
    public Set<String> getAllUsers() {
        return userSessionMap.keySet();
    }
}
