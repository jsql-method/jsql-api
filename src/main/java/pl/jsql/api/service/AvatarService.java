package pl.jsql.api.service;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import pl.jsql.api.dto.response.AvatarResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.exceptions.NotFoundException;
import pl.jsql.api.model.user.Avatar;
import pl.jsql.api.repo.AvatarDao;
import pl.jsql.api.security.service.SecurityService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Transactional
@Service
public class AvatarService {

    @Autowired
    private AvatarDao avatarDao;

    @Autowired
    private SecurityService securityService;

    public MessageResponse uploadAvatar(MultipartFile multipartFile, String realPath) throws IOException {

        if (multipartFile == null || multipartFile.getSize() == 0) {
            throw new NotFoundException("avatar_not_provided");
        }

        String originalName = multipartFile.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf(".") + 1, originalName.length()).toLowerCase();

        Avatar avatar = new Avatar();
        avatar.name = RandomString.make();
        avatar.type = extension;
        avatar.user = securityService.getCurrentAccount();

        avatarDao.save(avatar);

        File imageFile = new File(realPath + avatar.name + "." + extension);
        multipartFile.transferTo(imageFile);

        return new MessageResponse("avatar_uploaded");

    }

    public AvatarResponse getAvatar(String realPath) throws IOException {

        Avatar avatar = avatarDao.findByUser(securityService.getCurrentAccount());

        if (avatar == null) {
            return new AvatarResponse();
        }

        realPath = realPath + avatar.name + "." + avatar.type;

        Path path = Paths.get(realPath);
        byte[] imageBytes = Files.readAllBytes(path);

        AvatarResponse avatarResponse = new AvatarResponse();
        avatarResponse.length = imageBytes.length;
        avatarResponse.bytes = imageBytes;

        switch (avatar.type) {
            case "png":
                avatarResponse.type = MediaType.IMAGE_PNG;
                break;
            case "jpg":
            case "jpeg":
                avatarResponse.type = MediaType.IMAGE_JPEG;
                break;
            case "gif":
                avatarResponse.type = MediaType.IMAGE_GIF;
                break;
        }

        avatarResponse.data = Base64Utils.encodeToString(imageBytes);

        return avatarResponse;

    }

}
