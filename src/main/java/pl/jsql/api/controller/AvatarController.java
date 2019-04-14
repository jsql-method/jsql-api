package pl.jsql.api.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.response.AvatarResponse;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.AvatarService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

;

@CrossOrigin
@RestController
@RequestMapping("/api/avatar")
public class AvatarController extends ValidateController {

    @Autowired
    private AvatarService avatarService;

    @Security
    @PostMapping
    public BasicResponse<MessageResponse> uploadAvatar(@RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        MessageResponse response = avatarService.uploadAvatar(file);
        return new BasicResponse<>(200, response);
    }

    @Security(requireActiveSession = false)
    @GetMapping("/{session}")
    public Object getPreview(@PathVariable("session") String session, HttpServletRequest request) throws IOException {

        AvatarResponse avatar = avatarService.getAvatar(session);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(avatar.type);
        headers.setContentLength(avatar.length);

        return new HttpEntity<>(avatar.bytes, headers);

    }

}
