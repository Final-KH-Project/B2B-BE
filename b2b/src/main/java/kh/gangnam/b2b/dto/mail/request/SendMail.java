package kh.gangnam.b2b.dto.mail.request;

import lombok.Getter;

@Getter
public class SendMail {

    private String receiver;
    private String sender;
    private String title;
    private String content;

    public SendMail(String receiver,String sender,String title,String content){
        this.receiver = receiver;
        this.sender = sender;
        this.title = title;
        this.content = content;
    }
    public String getReceiver(){
        return receiver;
    }
    public String getSender(){
        return sender;
    }
    public String getTitle(){
        return title;
    }
    public String getContent(){
        return content;
    }
}
