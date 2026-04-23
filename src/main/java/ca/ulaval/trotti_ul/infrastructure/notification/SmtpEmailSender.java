package ca.ulaval.trotti_ul.infrastructure.notification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ca.ulaval.trotti_ul.domain.common.TechnicalException;
import ca.ulaval.trotti_ul.domain.notification.EmailContentType;
import ca.ulaval.trotti_ul.domain.notification.EmailMessage;
import ca.ulaval.trotti_ul.domain.notification.EmailSender;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class SmtpEmailSender implements EmailSender {

    private final Session session;
    private final String from;

    public SmtpEmailSender() {
        Properties props = loadProperties();

        this.from = props.getProperty("SMTP_FROM");
        boolean auth = Boolean.parseBoolean(props.getProperty("SMTP_AUTH", "true"));

        if (auth) {
            String username = props.getProperty("SMTP_USERNAME");
            String password = props.getProperty("SMTP_PASSWORD");
            this.session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            this.session = Session.getInstance(props);
        }

        if (from == null || from.isBlank()) {
            throw new TechnicalException("SMTP_FROM must be configured");
        }
    }

    @Override
    public void send(EmailMessage message) {
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.to()));
            mimeMessage.setSubject(message.subject());
            if (message.contentType() == EmailContentType.HTML) {
                mimeMessage.setContent(message.body(), "text/html; charset=UTF-8");
            } else {
                mimeMessage.setText(message.body());
            }
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            throw new TechnicalException("Failed to send email", e);
        }
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new TechnicalException("Failed to load SMTP configuration", e);
        }
        overrideFromEnv(props, "SMTP_HOST");
        overrideFromEnv(props, "SMTP_PORT");
        overrideFromEnv(props, "SMTP_USERNAME");
        overrideFromEnv(props, "SMTP_PASSWORD");
        overrideFromEnv(props, "SMTP_FROM");
        overrideFromEnv(props, "SMTP_AUTH");
        overrideFromEnv(props, "SMTP_STARTTLS");
        props.putIfAbsent("mail.smtp.auth", props.getProperty("SMTP_AUTH", "true"));
        props.putIfAbsent("mail.smtp.starttls.enable", props.getProperty("SMTP_STARTTLS", "true"));
        props.putIfAbsent("mail.smtp.host", props.getProperty("SMTP_HOST"));
        props.putIfAbsent("mail.smtp.port", props.getProperty("SMTP_PORT"));
        return props;
    }

    private void overrideFromEnv(Properties props, String key) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            props.setProperty(key, envValue);
        }
    }
}
