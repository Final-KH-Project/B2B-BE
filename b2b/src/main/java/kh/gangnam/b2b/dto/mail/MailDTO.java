package kh.gangnam.b2b.dto.mail;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailDTO {

    private String to;
    private String subject;
    private String text;


}
