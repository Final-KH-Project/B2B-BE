package kh.gangnam.b2b.dto.mail.request;


import java.util.List;

public class SendMail {

    private List<String> receiver;
    private String sender;
    private String title;
    private String content;

    public SendMail(){}

    public SendMail(List<String> receiver, String sender, String title, String content) {
        this.receiver = receiver;
        this.sender = sender;
        this.title = title;
        this.content = content;
    }

    public List<String> getReceiver() {
        return receiver;
    }

    public void setReceiver(List<String> receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}