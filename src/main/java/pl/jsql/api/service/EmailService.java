package pl.jsql.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
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
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class EmailService {

    @Autowired
    private SettingDao settingDao;

   // @Autowired
   // public JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String mailFrom;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private Integer mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mailAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String mailTls;

    private void sendEmail(User user, String subject, String content) {

        String to = user.email;
        String fullName = user.firstName + " "+user.lastName;

        new Thread(() -> {

            try {

                HtmlEmail email = new HtmlEmail();
                email.setHostName(mailHost);
                email.setAuthentication(mailUsername, mailPassword);
                email.setSmtpPort(mailPort);
                email.setStartTLSEnabled(true);
                email.addTo(to, fullName);
                email.setFrom(mailFrom, "JSQL");
                email.setSubject(subject);

                URL url = new URL("https://jsql.it/wp-content/uploads/2018/10/cropped-jsql-logo-alfa-300x215.png");
                String cid = email.embed(url, "JSQL Logo");

                email.setHtmlMsg(content.replace("{cid}",cid));
                email.setCharset("UTF-8");
                email.send();

            } catch (MalformedURLException | EmailException e) {
                e.printStackTrace();
            }

        }).start();

    }

    @Value("${deactivationCompany.mail.subject}")
    private String deactivationCompanyAdminEmailSubject;

    public void sendDeactivationCompanyAdminMail(User user) {

        //String origin = settingDao.findByType(SettingEnum.ORIGIN_URL).value;

        InputStream is = TypeReference.class.getResourceAsStream("/templates/deactivate-company.html");

        Document template = null;
        try {
            template = Jsoup.parse(is, "UTF-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String html = template.html();
        html = html.replace("{fullName}", user.firstName + " " + user.lastName);
        html = html.replace("{accountId}", user.id.toString());

        this.sendEmail(user, deactivationCompanyAdminEmailSubject, html);
    }

    @Value("${activationCompany.mail.subject}")
    private String activationCompanyAdminEmailSubject;

    public void sendActivationCompanyAdminMail(User user) {

        String origin = settingDao.findByType(SettingEnum.ORIGIN_URL).value;

        InputStream is = TypeReference.class.getResourceAsStream("/templates/activate-company.html");

        Document template = null;
        try {
            template = Jsoup.parse(is, "UTF-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String html = template.html();
        html = html.replace("{fullName}", user.firstName + " " + user.lastName);
        html = html.replace("{activationUrl}", origin + "/activate/" + user.token);

        this.sendEmail(user, activationCompanyAdminEmailSubject, html);
    }

    @Value("${activationDeveloper.mail.subject}")
    private String activationDeveloperEmailSubject;

    public void sendActivationDeveloperMail(User user, String password) {

        String origin = settingDao.findByType(SettingEnum.ORIGIN_URL).value;

        InputStream is = TypeReference.class.getResourceAsStream("/templates/activate-developer.html");

        Document template = null;
        try {
            template = Jsoup.parse(is, "UTF-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String html = template.html();
        html = html.replace("{fullName}", user.firstName + " " + user.lastName);
        html = html.replace("{email}", user.email);
        html = html.replace("{password}", password);
        html = html.replace("{companyName}", user.company.name);
        html = html.replace("{activationUrl}", origin + "/activate/" + user.token);

        this.sendEmail(user, activationDeveloperEmailSubject, html);
    }

    @Value("${forgotPassword.mail.subject}")
    private String forgotPasswordEmailSubject;

    public void sendForgotPasswordEmail(User user) {

        String origin = settingDao.findByType(SettingEnum.ORIGIN_URL).value;

        InputStream is = TypeReference.class.getResourceAsStream("/templates/forgot-password.html");

        Document template = null;

        try {
            template = Jsoup.parse(is, "UTF-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String html = template.html();
        html = html.replace("{fullName}", user.firstName + " " + user.lastName);
        html = html.replace("{activationUrl}", origin + "/reset-password/" + user.token);

        this.sendEmail(user, forgotPasswordEmailSubject, html);

    }

}
