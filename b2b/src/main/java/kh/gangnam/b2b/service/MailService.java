package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.mail.MailDTO;
import kh.gangnam.b2b.dto.mail.request.SendMail;
import kh.gangnam.b2b.dto.mail.response.ReadMails;
import org.springframework.http.ResponseEntity;

public interface MailService {

    // 메일 전송 로직

    /**
     * 이메일 전송 로직
     * SendMail 에는 이메일을 전송하기 위한 전송자, 수신자, 메일 내용, 메일 제목 등이 필요
     * @param sendMail
     * 성공 여부를 전달하는 status 필요
     * @return
     */
    ResponseEntity<MailDTO> sendMail(SendMail sendMail);

    // 메일 수신 로직

    /**
     * 메일함 확인 및 새로고침 로직
     *
     * 로직 플로우
     * 1. 메일 수신 외부 API로 수신된 메일 리스트 가져오기
     * 2. 메일 테이블에서 젤 최근에 받은 메일 엔티티 가져오기
     * 3. 가져온 엔티티와 메일 리스트를 같지 않다면 데이터베이스에 저장
     * 4. 가져온 엔티티와 메일 리스트가 같게 된다면 이후 메일 리스트는 이미 저장되어 있는 리스트임
     * !!여기 로직에는 외부 API로 가져온 메일 리스트가 최신순으로 온다고 가정해야 함
     * 5. 데이터베이스에 메일 리스트 가져와서 DTO로 매핑 후 반환
     * 해당 userId 를 파라미터로 받음
     * @param userId
     * 메일함 리스트를 데이터베이스에서 출력하는 것
     * @return
     */
    ResponseEntity<ReadMails> readMails(Long userId);

    /**
     * 메일 상세 보기
     * 메일 리스트에서 하나의 메일을 클릭했을 때 mailId 를 뜻함
     * @param mailId
     * 해당 메일의 엔티티 내용을 MailDTO 에 매핑해서 반환
     * @return
     */
    ResponseEntity<MailDTO> readMail(Long mailId);

}
