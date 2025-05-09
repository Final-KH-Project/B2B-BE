package kh.gangnam.b2b.dto.mail.response;

import java.util.Date;
import java.util.List;

public class ReadMails {

    private String sender;
    private List<String> receiver;
    private String title;
    private String content;
    private Date sentDate;
    private boolean isRead;

    public ReadMails(String sender, List<String> receiver, String title, String content, Date sentDate, boolean isRead){

    }
}
