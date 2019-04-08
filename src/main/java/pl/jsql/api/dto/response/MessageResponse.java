package pl.jsql.api.dto.response;

import java.util.HashMap;

public class  MessageResponse {

    public String message;
    public HashMap<String, String> messages;

    public MessageResponse(String message) {
        this.message = message;
        this.messages = new HashMap<>();
    }

    public MessageResponse(String message, HashMap<String, String> messages) {
        this.message = message;
        this.messages = messages;
    }

}
