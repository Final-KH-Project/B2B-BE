package kh.gangnam.b2b.controller;

import lombok.RequiredArgsConstructor;
import kh.gangnam.b2b.dto.mail.MailDTO;
import kh.gangnam.b2b.service.ServiceImpl.MailServiceImpl;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailServiceImpl mailService;

    @PostMapping("/send")
    public String sendEmail(@RequestBody MailDTO mailDTO) {
        mailService.sendSimpleMessage(mailDTO);
        return "mail sent successfully";
    }
}
