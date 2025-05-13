package kh.gangnam.b2b.webSocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {
    // username -> sessionId 매핑
    //사용자 세션을 ConcurrentHashMap 등록,제거
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    // 세션 추가
    public void addSession(String username, String sessionId) {
        userSessionMap.put(username, sessionId);
    }

    // 세션 제거
    public void removeSession(String sessionId) {
        userSessionMap.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));
    }

    // 사용자명으로 세션 ID 조회
    public String getSessionId(String username) {
        return userSessionMap.get(username);
    }

    // 연결된 모든 사용자 확인용
    public Set<String> getAllConnectedUsers() {
        return userSessionMap.keySet();
    }

}
