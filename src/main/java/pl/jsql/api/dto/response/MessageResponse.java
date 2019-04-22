package pl.jsql.api.dto.response;

import java.util.HashMap;

public class  MessageResponse {

    public String errorMessage;
    public String message;
    public HashMap<String, String> messages;

    public MessageResponse() {
    }

    public MessageResponse(Boolean isError, String message) {

        if(isError){
            this.errorMessage = message;
        }else{
            this.message = message;
        }

    }

    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(Boolean isError, String message, HashMap<String, String> messages) {

        if(isError){
            this.errorMessage = message;
        }else{
            this.message = message;
        }

        this.messages = messages;
    }

    public MessageResponse(String message, HashMap<String, String> messages) {
        this.message = message;
        this.messages = messages;
    }

    public MessageResponse(HashMap<String, String> messages) {
        this.messages = messages;
    }

}
