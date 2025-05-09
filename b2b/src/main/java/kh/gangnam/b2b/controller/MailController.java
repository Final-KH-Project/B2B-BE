package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.mail.request.SendMail;
import kh.gangnam.b2b.dto.mail.response.ReadMails;
import kh.gangnam.b2b.service.ServiceImpl.MailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailServiceImpl mailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody SendMail sendMail){
        try{
            mailService.sendEmail(sendMail);  // 이메일 발송 서비스 호출
            return ResponseEntity.ok("Email sent successfully.");
        }catch (){

        }
    }
    // 이메일 수신 API (GET 요청)
    @GetMapping("/receive")
    public ResponseEntity<ReadMails> fetchEmails(@RequestParam Long userId) {
        try {
            ReadMails emails = mailS
        }
    }

}
