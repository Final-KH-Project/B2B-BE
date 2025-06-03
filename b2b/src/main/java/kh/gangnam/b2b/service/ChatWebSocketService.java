package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ChatMessages;

/**
 * WebSocket 기반 실시간 채팅 메시지 서비스 인터페이스
 */
public interface ChatWebSocketService {
    /**
     * 메시지 저장 및 DTO 변환
     * @param message 메시지 요청 DTO
     * @param userId  발신자 ID
     * @return        응답용 메시지 DTO
     */
    ChatMessages handleMessage(SendChat message, Long userId);
}
