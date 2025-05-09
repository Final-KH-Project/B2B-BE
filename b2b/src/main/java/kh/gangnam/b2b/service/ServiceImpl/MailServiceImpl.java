package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.mail.MailDTO;
import kh.gangnam.b2b.dto.mail.request.SendMail;
import kh.gangnam.b2b.dto.mail.response.ReadMails;
import kh.gangnam.b2b.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private ReceiveMailService receiveMailService;

    @Override
    public ResponseEntity<MailDTO> sendMail(SendMail sendMail) {
        return null;
    }

    @Override
    public ResponseEntity<ReadMails> readMails(Long userId) {
        return null;
    }

    @Override
    public ResponseEntity<MailDTO> readMail(Long mailId) {
        return null;
    }
}
