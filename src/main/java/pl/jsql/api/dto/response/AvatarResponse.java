package pl.jsql.api.dto.response;

import org.springframework.http.MediaType;

public class AvatarResponse {

    public MediaType type;
    public String data;
    public byte[] bytes;
    public long length;

}
