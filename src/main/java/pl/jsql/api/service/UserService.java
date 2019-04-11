package pl.jsql.api.service

import com.fasterxml.jackson.core.type.TypeReference
import org.apache.commons.codec.digest.DigestUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.jsql.api.dto.request.UserRequest
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.enums.SettingEnum
import pl.jsql.api.model.user.Company
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.ApplicationDao
import pl.jsql.api.repo.ApplicationMembersDao
import pl.jsql.api.repo.SettingsDao
import pl.jsql.api.repo.UserDao
import pl.jsql.api.security.service.SecurityService

import javax.transaction.Transactional
import java.util.concurrent.TimeUnit

import static pl.jsql.api.enums.HttpMessageEnum.*

@Transactional
@Service
public class  UserService {

    @Autowired
    UserDao userDao

    @Autowired
    ApplicationDao applicationDao

    @Autowired
    SettingsDao settingsDao

    @Autowired
    ApplicationMembersDao applicationMembersDao

    @Autowired
    SecurityService securityService

    @Autowired
    AuthService authService

    def validate(UserRequest request) {

        if (request.email) {

            if (request.email.length() > 255) {
                return [code: EMAIL_TOO_LONG.getCode(), description: EMAIL_TOO_LONG.getDescription()]
            }
            if (userDao.findByEmail(request.email) != null) {
                return [code: EMAIL_USED.getCode(), description: EMAIL_USED.getDescription()]
            }

        }

        return [code: SUCCESS.getCode(), description: "Success"]
    }

    /**
     * Method updates existing user's account information
     *
     * @param user User to be updated
     * @param update Update data request
     * @return Map[:] Map containing result's code and description message
     */
    def update(Long id, UserRequest userRequest) {

        //clear unwanted changes
        userRequest.role = null
        userRequest.password = null
        userRequest.company = null
        userRequest.application = null

        User user = securityService.getCurrentAccount()
        User beingUpdated = userDao.findById(id)

        if (!beingUpdated) {

            return [code: ACCOUNT_NOT_EXIST.getCode(), description: ACCOUNT_NOT_EXIST.getDescription()]

        }
        if (user != beingUpdated) {

            if (beingUpdated.role.authority == RoleTypeEnum.COMPANY_ADMIN || user.role.authority == RoleTypeEnum.APP_DEV) {

                return [code: FORBIDDEN.getCode(), description: FORBIDDEN.getDescription()]

            }
        }

        def validation = validate(userRequest)

        if (validation.code != 200) {

            return validation

        }

        if (userRequest.email) {
            beingUpdated.setField("email", userRequest.email)
        }
        if (userRequest.firstName) {
            beingUpdated.setField("firstName", userRequest.firstName)
        }
        if (userRequest.lastName) {
            beingUpdated.setField("lastName", userRequest.lastName)
        }

        return validation
    }


    def activateAccount(String token) {

        User userEntry = userDao.findByActivationToken(token)

        if (!userEntry) {

            return [code: ACTIVATION_TOKEN_ERROR.getCode(), data: ACTIVATION_TOKEN_ERROR.getDescription()]

        }

        Date currentDate = new Date()
        Long diff = currentDate.getTime() - userEntry.activationDate.getTime()
        Long duration = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        Long expirationDays = Long.valueOf(settingsDao.findByType(SettingEnum.ACTIVATION_LINK_EXPIRATION).value)

        if (duration > expirationDays) {

            return [code: ACTIVATION_URL_EXPIRED.getCode(), data: ACTIVATION_URL_EXPIRED.getDescription()]

        }

        userEntry.activated = true

        userDao.save(userEntry)

        return [code: SUCCESS.getCode(), data: "Activated successfully. Now sign in."]
    }

    def reActivate(String token, String origin) {

        User user = userDao.findByActivationToken(token)

        if (user != null) {

            authService.sendActivationMail(user, origin)

            return [code: SUCCESS.getCode(), data: null]

        } else {

            return [code: ACTIVATION_TOKEN_ERROR.getCode(), data: ACTIVATION_TOKEN_ERROR.getDescription()]

        }

    }


    def sendMessageEndLicense(User user, Integer daysRemaining) {

        if (user.enabled && user.activated) {

            InputStream is = TypeReference.class .getResourceAsStream("/templates/license_end.html")
            Document template = Jsoup.parse(is, "UTF-8", "")

            String titleEmail = "JSQL licence is about to end!"

            Element date_end = template.getElementById("date-end")
            Element usr_username = template.getElementById("usr_nickname")

            date_end.text("" +
                    "Your license ends in: " + daysRemaining + " days")
            usr_username.text(user.firstName + " " + user.lastName)

            EmailService.sendEmail(titleEmail, template.html(), user.email)
        }

    }


    public MessageResponse forgotPassword(String email) {

        User userEntry = userDao.findByEmail(email)

        if (userEntry == null) {

            return [code: USER_EMAIL_NOT_FOUND.getCode(), description: USER_EMAIL_NOT_FOUND.getDescription()]

        }

        userEntry.changePasswordDate = new Date()
        userEntry.blocked = true
        userEntry.forgotToken = DigestUtils.sha256Hex(email + new Date().getTime())

        userDao.save(userEntry)

        InputStream is = TypeReference.class .getResourceAsStream("/templates/forgot_password.html")
        Document template = Jsoup.parse(is, "UTF-8", "")

        Element usrNickname = template.getElementById("usr_nickname")
        Element usrResetBtn = template.getElementById("usr_resetbtn")

        usrNickname.text(userEntry.firstName + " " + userEntry.lastName)
        usrResetBtn.attr("href", origin + "/auth/reset/" + userEntry.forgotToken)

        EmailService.sendEmail("Change your password", template.html(), userEntry.email)

        return [code: SUCCESS.getCode(), description: "Check your inbox and follow the instruction"]
    }

    def resetPassword(String forgotToken, String newPassword) {

        User userEntry = userDao.findByForgotToken(forgotToken)

        if (userEntry == null) {

            return [code: ACCOUNT_NOT_EXIST.getCode(), description: ACCOUNT_NOT_EXIST.getDescription()]

        }

        Date currentDate = new Date()
        Long diff = currentDate.getTime() - userEntry.changePasswordDate.getTime()
        Long duration = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        Long expirationDays = Long.valueOf(settingsDao.findByType(SettingEnum.RESET_PASSWORD_LINK_EXPIRATION).value)

        if (duration > expirationDays) {

            return [code: RESET_PASSWORD_URL_EXPIRED.getCode(), description: RESET_PASSWORD_URL_EXPIRED.getDescription()]

        }

        userEntry.blocked = false
        userEntry.password = DigestUtils.sha256Hex(newPassword)
        userEntry.forgotToken = null

        userDao.save(userEntry)

        return [code: SUCCESS.getCode(), description: "Password changed. Now sign in using new password."]
    }

    def changePassword(def changePasswordRequest) {

        User user = securityService.getCurrentAccount()

        String oldPasswordHash = DigestUtils.sha256Hex(changePasswordRequest.oldPassword)

        if (oldPasswordHash == user.password) {

            user.password = DigestUtils.sha256Hex(changePasswordRequest.newPassword)

            userDao.save(user)

            return [code: SUCCESS.getCode(), description: 'Password changed successfully.']

        }

        return [code: CHANGE_PASSWORD_ERROR.getCode(), description: CHANGE_PASSWORD_ERROR.getDescription()]

    }

    def disableAccount() {
        disableAccount(securityService.getCurrentAccount(), null)
    }

    def disableAccount(Long id) {
        disableAccount(securityService.getCurrentAccount(), id)
    }

    def disableAccount(User user, Long id) {

        User beingDeleted = user

        if (id != null) {

            beingDeleted = userDao.findById(id)

            if (beingDeleted == null) {

                return [code: ACCOUNT_NOT_EXIST.getCode(), description: ACCOUNT_NOT_EXIST.getDescription()]

            }

            if (beingDeleted.role.authority == RoleTypeEnum.COMPANY_ADMIN || user.role.authority == RoleTypeEnum.APP_DEV || user.company != beingDeleted.company) {

                return [code: FORBIDDEN.getCode(), description: FORBIDDEN.getDescription()]

            }
        }
        applicationMembersDao.clearJoinsByUser(beingDeleted)
        applicationMembersDao.deleteAllByUser(beingDeleted)

        beingDeleted.email = DigestUtils.md5Hex(beingDeleted.email + System.currentTimeMillis())
        beingDeleted.firstName = DigestUtils.md5Hex(beingDeleted.firstName + System.currentTimeMillis())
        beingDeleted.lastName = DigestUtils.md5Hex(beingDeleted.lastName + System.currentTimeMillis())
        beingDeleted.enabled = false
        beingDeleted.activated = false
        beingDeleted.blocked = true

        userDao.save(beingDeleted)

        Company company = beingDeleted.company

        company.name = company.name == null ? null : DigestUtils.md5Hex(company.name + System.currentTimeMillis())
        company.street = company.street == null ? null : DigestUtils.md5Hex(company.street + System.currentTimeMillis())
        company.city = company.city == null ? null : DigestUtils.md5Hex(company.city + System.currentTimeMillis())
        company.postalCode = company.postalCode == null ? null : DigestUtils.md5Hex(company.postalCode + System.currentTimeMillis())
        company.country = company.country == null ? null : DigestUtils.md5Hex(company.country + System.currentTimeMillis())
        company.isLicensed = false

        if (beingDeleted.role.authority == RoleTypeEnum.COMPANY_ADMIN) {

            applicationDao.updateApplicationToNotActiveByUser(beingDeleted)

        }

        return [code: SUCCESS.getCode(), data: null]
    }

    def getUser() {

        User user = securityService.getCurrentAccount()

        def data = [
                id       : user.id,
                email    : user.email,
                firstName: user.firstName,
                lastName : user.lastName
        ]

        return [code: SUCCESS.getCode(), data: data]

    }

}