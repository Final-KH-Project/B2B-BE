package kh.gangnam.b2b.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageResponse {

    private String message;

    public static MessageResponse sendMessage(String message){
        return  MessageResponse.builder().message(message).build();
    }
}
