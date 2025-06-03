package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.chat.request.CreateRoom;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ChatEmployee;
import kh.gangnam.b2b.dto.chat.response.ReadRoom;
import kh.gangnam.b2b.dto.chat.response.ReadRooms;

import java.util.List;
import java.util.Map;

/**
 * 채팅 비즈니스 로직 서비스
 */
public interface ChatService {

    /** 전체 직원 목록 조회 */
    List<ChatEmployee> getAllEmployees();

    /** 로그인 ID로 직원 정보 조회 */
    ChatEmployee getCurrentEmployee(String loginId);

    /** 채팅 메시지 전송 */
    void send(SendChat sendChat);

    /** 채팅방 생성 */
    Long createRoom(CreateRoom createRoom);

    /** 내 채팅방 목록 조회 */
    List<ReadRooms> readRooms(Long employeeId);

    /** 채팅방 상세(메시지 내역) 조회 */
    ReadRoom readRoom(Long roomId, Long employeeId);

    /** 채팅방 나가기 */
    void leaveRoom(Long roomId, Long employeeId);

    /** 메시지 읽음 처리 */
    void markAsRead(Long roomId, Long employeeId, Long lastReadMessageId);

    /** 특정 채팅방의 안읽은 메시지 개수 조회 */
    int getUnreadCount(Long roomId, Long employeeId);

    /** 내가 속한 모든 채팅방의 안읽은 메시지 개수 조회 */
    Map<String, Integer> getAllUnreadCountsAsStringKey(Long employeeId);
}
