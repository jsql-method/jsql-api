package pl.jsql.api.service

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.SimpleEmail

class EmailService {
    private static final String USERNAME = "mailer@softwarecartoon.com"
    private static final String PASSWORD = "m_@_i_l_e_r#123"
    private static final String HOST = "softwarecartoon.com"
    private static final int PORT = 25
    private static final String FROM = "mailer@softwarecartoon.com"


    static void sendEmail(String subject, String message, String recipient) {
        new Thread(new Runnable() {
            @Override
            void run() {
                Email email = new SimpleEmail()
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
