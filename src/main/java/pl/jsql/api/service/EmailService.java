package pl.jsql.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.jsql.api.enums.SettingEnum;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.SettingDao;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;

@Service
public class EmailService {

    @Autowired
    private SettingDao settingDao;

    @Autowired
    public JavaMailSender mailSender;

    @Value("${application.origin}")
    private String applicationOrigin;

    @Value("${spring.mail.from}")
    private String mailFrom;

    private void sendEmail(String to, String subject, String content) {

        new Thread(() -> {

            try {

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
                message.setContent(content, "text/html");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setFrom(EmailService.this.mailFrom);
                message.setText(content);
                EmailService.this.mailSender.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }).start();

    }

    @Value("${activationCompany.mail.subject}")
    private String activationCompanyAdminEmailSubject;

    public void sendActivationCompanyAdminMail(User user) {

        String origin = settingDao.findByType(SettingEnum.ORIGIN_URL).value;

        InputStream is = TypeReference.class.getResourceAsStream("/templates/verify.html");

        Document template = null;
        try {
            template = Jsoup.parse(is, "UTF-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element usrNickname = template.getElementById("usr_nickname");
        Element usrActivateBtn = template.getElementById("usr_activatebtn");

        usrNickname.text(user.firstName + " " + user.lastName);
        usrActivateBtn.attr("href", origin + "/auth/activate/" + user.token);

        this.sendEmail(user.email, activationCompanyAdminEmailSubject, template.html());
    }

    @Value("${activationDeveloper.mail.subject}")
    private String activationDeveloperEmailSubject;

    public void sendActivationDeveloperMail(User user, String password) {

        String origin = settingDao.findByType(SettingEnum.ORIGIN_URL).value;

        InputStream is = TypeReference.class.getResourceAsStream("/templates/welcome_member.html");

        Document template = null;
        try {
            template = Jsoup.parse(is, "UTF-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element usr_email = template.getElementById("usr_email");
        Element usr_password = template.getElementById("usr_password");
        Element usr_nickname = template.getElementById("usr_nickname");
        Element usrActivateBtn = template.getElementById("usr_activatebtn");

        usr_email.text("Your login: " + user.email);
        usr_password.text("Your password: " + password);
        usr_nickname.text(user.firstName + " " + user.lastName);
        usrActivateBtn.attr("href", origin + "/auth/activate/" + user.token);

        this.sendEmail(user.email, activationDeveloperEmailSubject, template.html());
    }

}
