package kh.gangnam.b2b.dto.mail.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ReadMails {

    private String sender;
    private List<String> receiver;
    private String title;
    private String content;
    private Date sentDate;
    private Date receivedDate;
    private boolean isRead;


    public ReadMails(String sender, List<String> receiver, String title, String content,
                       Date sentDate, Date receivedDate, boolean isRead){
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.content = content;
        this.sentDate = sentDate;
        this.receivedDate = receivedDate;
        this.isRead = isRead;
    }
}



