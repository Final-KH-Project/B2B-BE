package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.chat.request.CreateRoom;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ReadRoom;
import kh.gangnam.b2b.dto.chat.response.ReadRooms;

import java.util.List;

public interface ChatService {

    /**
     * 메시지 전송
     * 메세지 전송을 위한 SendChat DTO
     * @param sendChat
     * 반환값으로 전송 성공 여부 status
     * @return
     */
    void send(SendChat sendChat);

    /**
     * 채팅방 생성
     * 채팅방 생성을 위한 CreateRoom DTO
     * @param createRoom
     * 반환값으로 전송 성공 여부 status
     * @return
     */
    Long createRoom(CreateRoom createRoom);

    /**
     * 채팅방 리스트 조회
     * User 엔티티의 userId
     * @param employeeId
     * ChatRoomUser 리스트 -> 최근 이용 내역 순으로 정렬할 예정
     * @return
     */
    List<ReadRooms> readRooms(Long employeeId);

    /**
     * 채팅방 입장, 클릭
     * 채팅방 리스트 화면에서 채팅방을 클릭했을 때 해당 채팅방 id
     * @param roomId
     * ReadRoom -> 채팅 내역을 가지고 있는 DTO
     * @return
     */
    ReadRoom readRoom(Long roomId);

}