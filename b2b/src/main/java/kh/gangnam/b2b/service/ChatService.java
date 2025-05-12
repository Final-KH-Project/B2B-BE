package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.chat.request.CreateRoom;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ReadRoom;
import kh.gangnam.b2b.dto.chat.response.ReadRooms;
import kh.gangnam.b2b.entity.chat.ChatMessage;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChatService {

    /**
     * 메시지 전송
     * 메세지 전송을 위한 SendChat DTO
     * @param sendChat
     * 반환값으로 전송 성공 여부 status
     * @return
     */
    ResponseEntity<?> send(SendChat sendChat);

    /**
     * 채팅방 생성
     * 채팅방 생성을 위한 CreateRoom DTO
     * @param createRoom
     * 반환값으로 전송 성공 여부 status
     * @return
     */
    ResponseEntity<?> createRoom(CreateRoom createRoom);

    /**
     * 채팅방 리스트 조회
     * User 엔티티의 userId
     *
     * @param userId ChatRoomUser 리스트 -> 최근 이용 내역 순으로 정렬할 예정
     * @return
     */
    ResponseEntity<List<ReadRooms>> readRooms(Long userId);

    /**
     * 채팅방 입장, 클릭
     * 채팅방 리스트 화면에서 채팅방을 클릭했을 때 해당 채팅방 id
     * @param roomId
     * ReadRoom -> 채팅 내역을 가지고 있는 DTO
     * @return
     */
    ResponseEntity<ReadRoom> readRoom(Long roomId);

    /**
     * 채팅 내용 저장
     *
     * @param message
     * @param userId  handleMessage
     * @return
     */
    ResponseEntity<ChatMessage> handleMessage(SendChat message, Long userId);
}