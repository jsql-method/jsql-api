package pl.jsql.api.dto.response;

import java.util.HashMap;

public class  MessageResponse {

    public String message;
    public HashMap<String, String> messages;

    public MessageResponse() {
    }

    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(String message, HashMap<String, String> messages) {
        this.message = message;
        this.messages = messages;
    }

}
