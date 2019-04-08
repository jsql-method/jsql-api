package pl.jsql.api.service

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.SimpleEmail

public class  EmailService {
    private static final String USERNAME = "notification@jsql.it"
    private static final String PASSWORD = "xogvhonzmnnxbess"
    private static final String HOST = "smtp.gmail.com"
    private static final int PORT = 587
    private static final String FROM = "notification@jsql.it"


    static void sendEmail(String subject, String message, String recipient) {
        new Thread(new Runnable() {
            @Override
            void run() {
                Email email = new SimpleEmail()
                email.setStartTLSEnabled(true)
                email.setSmtpPort(PORT)
                email.setAuthenticator(new DefaultAuthenticator(USERNAME,
                        PASSWORD))
                email.setDebug(false)
                email.setHostName(HOST)
                email.setFrom(FROM)
                email.setSubject(subject)
                email.setContent(message, 'text/html; charset=utf-8')
                email.addTo(recipient)
                email.setSSLOnConnect(false)
                email.send()
            }
        }).start()

    }

}
