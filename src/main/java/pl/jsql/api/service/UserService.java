package pl.jsql.api.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.*;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.dto.response.UserResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.enums.SettingEnum;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.ApplicationDao;
import pl.jsql.api.repo.ApplicationDevelopersDao;
import pl.jsql.api.repo.SettingDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.utils.HashingUtil;
import pl.jsql.api.utils.TokenUtil;

import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;

;

@Transactional
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationDevelopersDao applicationDevelopersDao;

    @Autowired
    private SecurityService securityService;

    public MessageResponse update(UpdateUserRequest updateUserRequest) {

        User currentUser = securityService.getCurrentAccount();

        currentUser.email = updateUserRequest.email == null ? currentUser.email : updateUserRequest.email;
        currentUser.firstName = updateUserRequest.email == null ? currentUser.firstName : updateUserRequest.firstName;
        currentUser.lastName = updateUserRequest.email == null ? currentUser.lastName : updateUserRequest.lastName;

        userDao.save(currentUser);

        return new MessageResponse();

    }


    public MessageResponse activateAccount(String token) {

        User userEntry = userDao.findByToken(token);

        if (userEntry == null) {
            return new MessageResponse("activation_token_not_found");
        }

        userEntry.enabled = true;
        userDao.save(userEntry);

        return new MessageResponse();
    }

    public MessageResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {

        User user = userDao.findByEmail(forgotPasswordRequest.email);

        if (user == null) {
            return new MessageResponse("user_not_exists");
        }

        user.enabled = false;
        user.token = TokenUtil.generateToken(forgotPasswordRequest.email);

        userDao.save(user);

        emailService.sendForgotPasswordEmail(user);

        return new MessageResponse();

    }

    public MessageResponse resetPassword(String token, ResetPasswordRequest resetPasswordRequest) {

        User userEntry = userDao.findByToken(token);

        if (userEntry == null) {
            return new MessageResponse("user_not_exists");
        }

        userEntry.enabled = true;
        userEntry.password = HashingUtil.encode(resetPasswordRequest.newPassword);

        userDao.save(userEntry);

        return new MessageResponse();

    }

    public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest) {

        User currentUser = securityService.getCurrentAccount();
        String oldPasswordHash = HashingUtil.encode(changePasswordRequest.oldPassword);

        if (!oldPasswordHash.equals(currentUser.password)) {

            return new MessageResponse("old_password_does_not_match");

        }

        currentUser.password = HashingUtil.encode(changePasswordRequest.newPassword);
        userDao.save(currentUser);

        return new MessageResponse();

    }

    public MessageResponse disableCurrentAccount() {
        return disableAccount(securityService.getCurrentAccount(), null);
    }

    public MessageResponse disableDeveloperAccount(Long developerId) {
       return disableAccount(securityService.getCurrentAccount(), developerId);
    }

    public MessageResponse disableAccount(User currentUser, Long developerId) {

        User accountToDelete = currentUser;

        if (developerId != null) {

            accountToDelete = userDao.findById(developerId).orElse(null);

            if (accountToDelete == null) {
                return new MessageResponse("user_does_not_exist");
            }

            if (currentUser.role.authority == RoleTypeEnum.APP_DEV || currentUser.company != accountToDelete.company) {
                return new MessageResponse("unable_to_disable_account");
            }

        }

        applicationDevelopersDao.clearJoinsByUser(accountToDelete);
        applicationDevelopersDao.deleteAllByUser(accountToDelete);

        accountToDelete.email = TokenUtil.generateToken(accountToDelete.email);
        accountToDelete.firstName = TokenUtil.generateToken(accountToDelete.firstName);
        accountToDelete.lastName = TokenUtil.generateToken(accountToDelete.lastName);
        accountToDelete.enabled = false;

        if(accountToDelete.role.authority == RoleTypeEnum.COMPANY_ADMIN){
            accountToDelete.company.isLicensed = false;
            applicationDao.updateApplicationToNotActiveByUser(accountToDelete);
        }

        userDao.save(accountToDelete);

       return new MessageResponse();

    }

    public UserResponse getUser() {

        User user = securityService.getCurrentAccount();

        UserResponse userResponse = new UserResponse();
        userResponse.id = user.id;
        userResponse.email = user.email;
        userResponse.firstName = user.firstName;
        userResponse.lastName = user.lastName;

        return userResponse;

    }

}