package ca.ulaval.trotti_ul.domain.notification;

public record EmailMessage(
        String to,
        String subject,
        String body,
        EmailContentType contentType
) { }
