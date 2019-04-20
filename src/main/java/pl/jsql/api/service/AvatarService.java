package pl.jsql.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.jsql.api.dto.response.AvatarResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.exceptions.NotFoundException;
import pl.jsql.api.model.user.Avatar;
import pl.jsql.api.model.user.Session;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.AvatarDao;
import pl.jsql.api.repo.SessionDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.utils.HashingUtil;
import pl.jsql.api.utils.TokenUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@Transactional
@Service
public class AvatarService {

    @Autowired
    private AvatarDao avatarDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private UserDao userDao;

    private HashMap<String, AvatarResponse> avatarCache = new HashMap<>();

    public MessageResponse uploadAvatar(MultipartFile multipartFile) throws IOException {

        if (multipartFile == null || multipartFile.getSize() == 0) {
            throw new NotFoundException("avatar_not_provided");
        }

        String originalName = multipartFile.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf(".") + 1, originalName.length()).toLowerCase();

        User currentUser = securityService.getCurrentAccount();

        Avatar avatar = avatarDao.findByUser(currentUser);

        if (avatar == null) {
            avatar = new Avatar();
        }

        avatar.type = extension;
        avatar.user = currentUser;
        avatar.image = ArrayUtils.toObject(multipartFile.getBytes());

        avatarDao.save(avatar);

        avatarCache.remove(HashingUtil.md5(currentUser.email));
        avatarCache.remove(securityService.getAuthorizationToken());

        return new MessageResponse("avatar_uploaded");

    }

    public AvatarResponse getAvatar(Avatar avatar, Boolean saveToSessionCache) throws IOException {

        AvatarResponse avatarResponse = new AvatarResponse();

        if (avatar == null) {

            InputStream is = TypeReference.class.getResourceAsStream("/images/avatar.png");
            avatarResponse.bytes = IOUtils.toByteArray(is);
            avatarResponse.length = avatarResponse.bytes.length;
            avatarResponse.type = MediaType.IMAGE_PNG;

            return avatarResponse;
        }

        byte[] imageBytes = ArrayUtils.toPrimitive(avatar.image);

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

        avatarCache.put(avatar.user.email, avatarResponse);

        if (saveToSessionCache) {
            avatarCache.put(securityService.getAuthorizationToken(), avatarResponse);
        }

        return avatarResponse;

    }

    public AvatarResponse getAvatarBySession(String sessionToken) throws IOException {

        if (avatarCache.get(sessionToken) != null) {
            return avatarCache.get(sessionToken);
        }

        Session session = sessionDao.selectByHash(sessionToken);

        Avatar avatar = null;

        if (session != null) {
            avatar = avatarDao.findByUser(session.user);
        }

        return this.getAvatar(avatar, true);
    }

    public AvatarResponse getAvatarById(Long id) throws IOException {

        if (avatarCache.get(id.toString()) != null) {
            return avatarCache.get(id.toString());
        }

        User user = userDao.findById(id).orElse(null);
        Avatar avatar = null;

        if(user != null){
            avatar = avatarDao.findByUser(user);
        }

        return this.getAvatar(avatar, false);

    }

}
