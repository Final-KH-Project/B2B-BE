package kh.gangnam.b2b.dto.chat.request;

import java.util.List;

public class CreateRoom {
    private String title;
    private List<Long> userIds;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
}
