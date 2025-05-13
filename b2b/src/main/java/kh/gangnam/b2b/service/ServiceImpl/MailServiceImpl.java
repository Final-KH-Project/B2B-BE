package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.mail.MailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl {

    private final JavaMailSender javaMailSender;

    public void sendSimpleMessage(MailDTO mailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("qkansgus1209@gmail.com");  // 보내는 사람 이메일
        message.setTo(mailDTO.getTo());  // 수신자 이메일
        message.setSubject(mailDTO.getSubject());  // 이메일 제목
        message.setText(mailDTO.getText());  // 이메일 본문
        javaMailSender.send(message);  // 메일 전송
    }
}