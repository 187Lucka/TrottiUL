package ca.ulaval.trotti_ul.domain.notification;

public interface EmailSender {
    void send(EmailMessage message);

    default void send(String to, String subject, String body) {
        send(new EmailMessage(to, subject, body, EmailContentType.TEXT));
    }
}
